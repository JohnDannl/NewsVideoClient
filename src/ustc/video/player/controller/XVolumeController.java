package ustc.video.player.controller;

import java.lang.ref.WeakReference;

import ustc.newsvideo.R;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class XVolumeController extends FrameLayout{
	private static final String TAG = "XVolumeController";
	private Context mContext;
	private AudioManager mAudioManager;
	private ViewGroup           mAnchor;
    private View                mRoot;
    private ProgressBar         mProgress;
    private boolean             mShowing;
    private boolean             mDragging;
    private static final int    sDefaultTimeout = 3000;
    private static final int    FADE_OUT = 1;
    private static final int    SHOW_PROGRESS = 2;
    private Handler             mHandler = new MessageHandler(this);
	
	public XVolumeController(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	public XVolumeController(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	public XVolumeController(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	/**
     * Set the view that acts as the anchor for the control view.
     * This can for example be a VideoView, or your Activity's main view.
     * @param view The view to which to anchor the controller when it is visible.
     */
    public void setAnchorView(ViewGroup view) {
        mAnchor = view;

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        removeAllViews();
        View v = makeControllerView();
        addView(v, frameParams);
    }
    /**
     * Create the view that holds the widgets that control playback.
     * Derived classes can override this to create their own.
     * @return The controller view.
     * @hide This doesn't work as advertised
     */
    protected View makeControllerView() {
        LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflate.inflate(R.layout.xvolume_controller, null);

        initControllerView(mRoot);

        return mRoot;
    }
    private void initControllerView(View v) {
    	mProgress = (ProgressBar) v.findViewById(R.id.volumecontroller_progress);
        if (mProgress != null) {
            if (mProgress instanceof SeekBar) {
                SeekBar seeker = (SeekBar) mProgress;
                seeker.setOnSeekBarChangeListener(mSeekListener);
            }
            mProgress.setMax(100);
        }
    }
 // There are two scenarios that can trigger the seekbar listener to trigger:
    //
    // The first is the user using the touchpad to adjust the posititon of the
    // seekbar's thumb. In this case onStartTrackingTouch is called followed by
    // a number of onProgressChanged notifications, concluded by onStopTrackingTouch.
    // We're setting the field "mDragging" to true for the duration of the dragging
    // session to avoid jumps in the position in case of ongoing playback.
    //
    // The second scenario involves the user operating the scroll ball, in this
    // case there WON'T BE onStartTrackingTouch/onStopTrackingTouch notifications,
    // we will simply apply the updated position without suspending regular updates.
    private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);

            mDragging = true;

            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactly one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (mContext == null) {
                return;
            }
            
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            setAbsMediaVolume(((float)progress)/mProgress.getMax());
        }

        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            show(sDefaultTimeout);

            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };
    private static class MessageHandler extends Handler {
        private final WeakReference<XVolumeController> mView; 

        MessageHandler(XVolumeController xVolumeController) {
            mView = new WeakReference<XVolumeController>(xVolumeController);
        }
        @Override
        public void handleMessage(Message msg) {
            XVolumeController view = mView.get();
            if (view == null || view.mAudioManager == null) {
                return;
            }
            
            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    view.hide();
                    break;
                case SHOW_PROGRESS:
                    pos = view.setProgress();
                    if (!view.mDragging && view.mShowing) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 10);
                    }
                    break;
            }
        }
    }
    /**
     * Show the controller on screen. It will go away
     * automatically after 'timeout' milliseconds of inactivity.
     * @param timeout The timeout in milliseconds. Use 0 to show
     * the controller until hide() is called.
     */
    public void show(int timeout) {
        if (!mShowing && mAnchor != null) {
            setProgress();

            FrameLayout.LayoutParams tlp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            );
            tlp.setMargins(0, 60, 0, 0);
            mAnchor.addView(this, tlp);
            mShowing = true;
        }        
        // cause the progress bar to be updated even if mShowing
        // was already true.  This happens, for example, if we're
        // paused with the progress bar showing the user hits play.
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }
    
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * Remove the controller from the screen.
     */
    public void hide() {
        if (mAnchor == null) {
            return;
        }

        try {
            mAnchor.removeView(this);
            mHandler.removeMessages(SHOW_PROGRESS);
        } catch (IllegalArgumentException ex) {
            Log.w("VolumeController", "already removed");
        }
        mShowing = false;
    }
    private int setProgress() {
        if (mAudioManager == null || mDragging) {
            return 0;
        }        
        int position = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int duration = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                int pos = 100 * position / duration;
                mProgress.setProgress(pos);
            }
        }
        return position;
    }
    public void setIncreMediaVolume(float percent){
//		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		int currentIndex = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//		Log.d("XXXXXpercent", Float.toString(percent));
//		Log.d("XXXXXXget", Integer.toString(currentIndex));
		int maxIndex=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if((currentIndex+percent*maxIndex)>maxIndex){
			currentIndex=maxIndex;}
		else if((currentIndex+percent*maxIndex)<0){
			currentIndex=0;			
		}else{
			currentIndex+=(int)(percent*maxIndex);
		}
//		Log.d("XXXXXXset", Integer.toString(currentIndex)+","+Integer.toString(maxIndex));
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentIndex, 0);
		setProgress();
	}
    private void setAbsMediaVolume(float percent){
		int maxIndex=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int	currentIndex=(int)(percent*maxIndex);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentIndex, 0);
	}
}
