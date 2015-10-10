package ustc.video.player;

import io.vov.vitamio.MediaPlayer;
import java.io.File;
import ustc.custom.widget.XSurfaceView;
import ustc.newsvideo.R;
import ustc.utils.cache.Cache;
import ustc.video.player.controller.XVideoControllerView;
import ustc.video.player.controller.XVideoControllerView.MediaPlayerControl;
import ustc.video.player.controller.XVolumeController;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.DigitalClock;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class XCPlayer {
	private static String TAG="XCPlayer";
	private static String cacheDir="video";
	private static MediaPlayer mMediaPlayer=null;
	private XSurfaceView mSurface;
	private SurfaceHolder mHolder;
	private LinearLayout progressBarLayout;
	private ProgressBar progressBar;
	private TextView downloadRateView, loadRateView;
	private DigitalClock digital_clock;
	private Activity mActivity;
	private View rootView;
	private MediaPlayer.OnCompletionListener completionCallback=null;
	private XVideoControllerView mVideoController;
	private XVolumeController mVolumeController;
	private boolean isPlayerPrepared=false;
	private boolean isFullScreen=false;
	private PlayerExtraController playerExtraController;
	private TextView mPlayingTitle=null;
	private ImageButton mRelatedButton=null;
	private Handler mHandler=new Handler();
	public XCPlayer(Activity activity,View rootView){
		mActivity=activity;
		this.rootView=rootView;
		initialization(activity,rootView);
	}
	private void initialization(Activity activity,View rootView){
		progressBarLayout=(LinearLayout)rootView.findViewById(R.id.player_progressbar);		
		progressBar = (ProgressBar) rootView.findViewById(R.id.probar);
	    downloadRateView = (TextView) rootView.findViewById(R.id.download_rate);
	    loadRateView = (TextView) rootView.findViewById(R.id.load_rate);
	    progressBarLayout.setVisibility(View.GONE); 
	    mPlayingTitle=(TextView)rootView.findViewById(R.id.c_textview);
	    /*mRelatedButton=(ImageButton)rootView.findViewById(R.id.related_video);
	    mRelatedButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Controller and prepared is not necessary.
//				if(mVideoController!=null&&isPlayerPrepared&&playerExtraController!=null)
				if(playerExtraController!=null)
					playerExtraController.toggleRelatedButton();
			}
	    	
	    });*/
	    mPlayingTitle.setVisibility(View.GONE);
	    //mRelatedButton.setVisibility(View.GONE);
	    digital_clock=(DigitalClock)rootView.findViewById(R.id.digital_clock);
	    digital_clock.setVisibility(View.GONE);
	    createSurfaceView();
	    createVideoController();
	    createVolumeController();
	}
	// this method is replaced by playVideo(String[])
	/*public void playVideo(String vPath) {
		doCleanUp();				
		mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mSurface.setVisibility(View.VISIBLE);	
		mSurface.requestFocus();
		progressBarLayout.setVisibility(View.VISIBLE);
		// Create a new media player and set the listeners
		mMediaPlayer = new MediaPlayer(mActivity);
		try {
			mMediaPlayer.setDataSource(vPath);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.setDisplay(mHolder);
		isPlayerPrepared=false;
		mMediaPlayer.prepareAsync();
		mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				switch (what) {
			    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			      if (mMediaPlayer.isPlaying()) {
			        mMediaPlayer.pause();
			        progressBarLayout.setVisibility(View.VISIBLE);
			      }
			      break;
			    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			      mMediaPlayer.start();
			      progressBarLayout.setVisibility(View.GONE);
			      break;
			    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			      downloadRateView.setText("" + extra + "kb/s" + "  ");
			      break;
		    }
		    return true;
			}
		});
		mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				loadRateView.setText(percent + "%");
			}	    	
	    });			
		mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	        @Override
	        public void onPrepared(MediaPlayer mediaPlayer) {
	          // optional need Vitamio 4.0
	          mediaPlayer.setPlaybackSpeed(1.0f);
	          isPlayerPrepared=true;
	        }
	      });
		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
			}
		});		
	}*/
	
	public void playVideo(String[] urls) {
		doCleanUp();	
		//Volume controls should better be set in the onCreate() of activity
//		mActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		progressBarLayout.setVisibility(View.VISIBLE);
//		mSurface.setVisibility(View.VISIBLE);
		// Create a new media player and set the listeners
		mMediaPlayer = new MediaPlayer(mActivity);
//			Log.d("XXXXXXXXXXX", getCacheDir().toString());
		mMediaPlayer.setDataSegments(urls, getCacheDir().toString());
		mMediaPlayer.setDisplay(mHolder);
		mMediaPlayer.prepareAsync();
		mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				switch(extra){
				case MediaPlayer.MEDIA_ERROR_IO:
					Log.e(TAG, "Network related error");
					Toast.makeText(mActivity, R.string.video_loading_failure, Toast.LENGTH_SHORT).show();
					break;
				case MediaPlayer.MEDIA_ERROR_MALFORMED:
					Log.e(TAG, "Codec error");
					break;
				case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
					Log.e(TAG, "Video not surpports streaming");
					break;
				case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
					Log.e(TAG, "Time out");
					break;
				default:
					Log.e(TAG, "Error unknown");
					Toast.makeText(mActivity, R.string.video_failure_network, Toast.LENGTH_SHORT).show();
					break;					
				}
//				return false;
				doCleanUp();
				return true;
			}
		});
		mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			
			@Override
			public boolean onInfo(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				switch (what) {
			    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
			      if (mp.isPlaying()) {
			        mp.pause();				        
			        progressBarLayout.setVisibility(View.VISIBLE);
			      }
			      break;
			    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			      if(isPlayerPrepared)mp.start();
			      progressBarLayout.setVisibility(View.GONE);
			      break;
			    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
			      downloadRateView.setText("" + extra + "kb/s" + "  ");
			      break;
		    }
		    return true;
			}
		});
		mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener(){

			@Override
			public void onBufferingUpdate(MediaPlayer mp, int percent) {
				// TODO Auto-generated method stub
				loadRateView.setText(percent + "%");
			}	    	
	    });
		
		if (completionCallback!=null)mMediaPlayer.setOnCompletionListener(completionCallback);
		
		mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	        @Override
	        public void onPrepared(MediaPlayer mediaPlayer) {
	          // optional need Vitamio 4.0
	          mediaPlayer.setPlaybackSpeed(1.0f);
	          isPlayerPrepared=true;
	        }
	      });		
	}
	public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener){
		if(listener!=null)this.completionCallback=listener;
	}
	public void doCleanUp() {	
		//To hide videoController is necessary because the mVideoController depends on mMediaPlayer
		if(mVideoController!=null)mVideoController.hide();
		
		releaseMediaPlayer();	
		isPlayerPrepared=false;
		if(progressBarLayout!=null)progressBarLayout.setVisibility(View.GONE); 
		// This is not necessary,because it's set not to be shown when mMediaPlayer is null
//		if(mPlayingTitle!=null)mPlayingTitle.setVisibility(View.GONE);
		//To keep volume controller existence,mSurface should not be gone
//		if(mSurface!=null)mSurface.setVisibility(View.GONE);
	}
	
	public void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}
	public void showPlayerProgressBar(){
		if(progressBarLayout!=null){
			progressBarLayout.setVisibility(View.VISIBLE);
			downloadRateView.setText("");
			loadRateView.setText("");
		}		
	}
	public void pausePlayer(){
		if(mMediaPlayer != null&&mMediaPlayer.isPlaying()&&isPlayerPrepared)
			mMediaPlayer.pause();
	}
	public void resumePlayer(){
		if(mMediaPlayer != null&&isPlayerPrepared)
			mMediaPlayer.start();
	}
	public void hidePlayerProgressBar(){
		if(progressBarLayout!=null)progressBarLayout.setVisibility(View.GONE);		
	}
	public File getCacheDir(){
		File diskCacheDir=Cache.getDiskCacheDir(mActivity, cacheDir);
		if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
		if (!diskCacheDir.isDirectory())Log.e(TAG, "Video cache dir creation failure.");
		return diskCacheDir;
	}
	private void createSurfaceView(){
		mSurface = (XSurfaceView) rootView.findViewById(R.id.player_surface);
//	    mSurface.setVideoLayout(SurfaceView.VIDEO_LAYOUT_STRETCH, 0);
	    mHolder=mSurface.getHolder();
	    mHolder.setFormat(PixelFormat.RGBA_8888); //This set may cause the app crash
	    mHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
//				releaseMediaPlayer();
//				Log.d("XXXXXXXXXXXX", "destroyed");
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
//				mHolder=holder;
//				Log.d("XXXXXXXXXXXX", "created");				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub	
//				mHolder=holder;
//				Log.d("XXXXXXXXXXXX", "changeed");
			}
		});
	   /* mSurface.setOnTouchListener(new View.OnTouchListener() {
  			
  			@Override
  			public boolean onTouch(View v, MotionEvent event) {
  				// TODO Auto-generated method stub
  				// getDuration() can not be called before player is prepared
  				if(mMediaPlayer!=null&&mController!=null&&isPlayerPrepared)mController.show();
  				return true;
//  				return false;
  			}
  		});*/
	    mSurface.setOnGestureDetected(new XSurfaceView.OnGestureDetected() {
			
			@Override
			public void onDown(MotionEvent e) {
				// TODO Auto-generated method stub
				// getDuration() can not be called before player is prepared
  				if(mMediaPlayer!=null&&mVideoController!=null&&isPlayerPrepared)mVideoController.show();
  				// isPlayerPrepared is not necessary
  				if(playerExtraController!=null){
  					//show current playing video title
  					if(mPlayingTitle!=null&&playerExtraController.getCurrentTitle()!=null&&isFullScreen){
  						mPlayingTitle.setText(mActivity.getResources().getString(R.string.current_playing)
  								+playerExtraController.getCurrentTitle());
  						mPlayingTitle.setVisibility(View.VISIBLE);
  						//hide title after 3 seconds
  						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(mPlayingTitle!=null)mPlayingTitle.setVisibility(View.GONE);
							}
  							
  						}, 3000);
  					}
  					if(mRelatedButton!=null){
  						mRelatedButton.setVisibility(View.VISIBLE);
  						//hide title after 3 seconds
  						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(mRelatedButton!=null)mRelatedButton.setVisibility(View.GONE);
							}
  							
  						}, 3000);
  					}
  					if(digital_clock!=null&&isFullScreen){
  						digital_clock.setVisibility(View.VISIBLE);
  						mHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(digital_clock!=null)digital_clock.setVisibility(View.GONE);
							}
  							
  						},3000);
  					}
  						
  				}
  				// Single click to pause and resume player
  				/*if(mMediaPlayer!=null&&mVideoController!=null&&isPlayerPrepared){
  					if(mMediaPlayer.isPlaying())mMediaPlayer.pause();
  					else mMediaPlayer.start();
  				}*/
			}
			
			@Override
			public void onDoubleTap(MotionEvent e) {
				// TODO Auto-generated method stub
//				if(mMediaPlayer!=null&&mVideoController!=null&&isPlayerPrepared)
				// Constraint as before is not necessary
				isFullScreen=!isFullScreen;
				if(playerExtraController!=null){
					if(isFullScreen)playerExtraController.switchtoFullScreen();
					else playerExtraController.switchtoInlineScreen();
				}					
			}

			@Override
			public void onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
				// TODO Auto-generated method stub
				//rawX/Y is according to left-top corner of screen,but getY() is according to left-top corner of view
				float oldX = e1.getRawX(),y = e2.getY();
				int historySize = e2.getHistorySize();
				int pointerCount = e2.getPointerCount();
				if(historySize>0&&pointerCount>0){
					float oldY = e2.getHistoricalY(0, 0);
	                final DisplayMetrics displayMetrics = new DisplayMetrics();
	                mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	                final int height = displayMetrics.heightPixels;
	                final int width = displayMetrics.widthPixels;
	                if (oldX < width / 5.0){// left side sliding	                	
	                	if(mVolumeController!=null){
	                		mVolumeController.show(3000);
	                		mVolumeController.setIncreMediaVolume((oldY-y)*5/height);
	                		}
	                }
				}
//				Log.d("XXXXXXXX_E1", Integer.toString(e1.getHistorySize()));
			}
		});	    
//	    mSurface.setVisibility(View.GONE);
	    mSurface.setVisibility(View.VISIBLE);		
//		mSurface.requestFocus();	    
	}
	
	private void createVolumeController(){
		mVolumeController=new XVolumeController(mActivity);
		mVolumeController.setAnchorView((FrameLayout)rootView.findViewById(R.id.surfaceview_container));
	}
	private void createVideoController(){
		mVideoController=new XVideoControllerView(mActivity);
	    mVideoController.setAnchorView((FrameLayout)rootView.findViewById(R.id.surfaceview_container));
	    mVideoController.setMediaPlayer(new MediaPlayerControl(){
	    	
			@Override
			public void start() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared)mMediaPlayer.start();
			}

			@Override
			public void pause() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&mMediaPlayer.isPlaying())mMediaPlayer.pause();
			}

			@Override
			public int getDuration() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared) return (int) mMediaPlayer.getDuration();
				return 0;
			}

			@Override
			public int getCurrentPosition() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null) return (int) mMediaPlayer.getCurrentPosition();
				return 0;
			}

			@Override
			public void seekTo(int pos) {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared) mMediaPlayer.seekTo(pos);
			}

			@Override
			public boolean isPlaying() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null) return mMediaPlayer.isPlaying();
				return false;
			}

			@Override
			public int getBufferPercentage() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null) return mMediaPlayer.getBufferProgress();
				return 0;
			}

			@Override
			public boolean canPause() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean canSeekBackward() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean canSeekForward() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean isFullScreen() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared) return isFullScreen;
				return false;
			}

			@Override
			public void toggleFullScreen() {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared){
					isFullScreen=!isFullScreen;
					if(playerExtraController!=null){
						if(isFullScreen)playerExtraController.switchtoFullScreen();
						else playerExtraController.switchtoInlineScreen();
					}					
				}				
			}	    	
	    });		
	  mVideoController.setPrevNextListeners(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared&&playerExtraController!=null){
					playerExtraController.onClickNext();
				}
			}
		}, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mMediaPlayer!=null&&isPlayerPrepared&&playerExtraController!=null){
					playerExtraController.onClickPrev();
				}
			}
		});
	}
	public void setPlayerExtraController(PlayerExtraController controller){
		this.playerExtraController=controller;
	}
	public interface PlayerExtraController{
		public void switchtoFullScreen();
		public void switchtoInlineScreen();
		public void onClickPrev();
		public void onClickNext();
		public String getCurrentTitle();
		public void toggleRelatedButton();
	}
	public void setPrevButtonEnabled(boolean enabled){
		if(mMediaPlayer!=null&&isPlayerPrepared&&mVideoController!=null){
			mVideoController.setPrevButtonEnabled(enabled);
		}
	}
	public void setNextButtonEnabled(boolean enabled){
		if(mMediaPlayer!=null&&isPlayerPrepared&&mVideoController!=null){
			mVideoController.setNextButtonEnabled(enabled);
		}
	}
	public Boolean isFullScreen(){
		return isFullScreen;
	}
	public void setFullScreen(boolean fullScreen){
		isFullScreen=fullScreen;
	}
}
