package ustc.newsvideo;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

public class AudioPreset {
	private Context mContext;
	private AudioManager mAudioManager;
	public AudioPreset(Context mCont){
		mContext=mCont;
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	public boolean requestVolumeControll(){
		
		int result = mAudioManager.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//		mAudioManager.registerMediaButtonEventReceiver(RemoteControlReceiver);
		// Start playback.
			return true;
		}
		return false;
	}
	private OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {

		@Override
		public void onAudioFocusChange(int focusChange) {
			// TODO Auto-generated method stub
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
	            // Pause playback
	        }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
	            // Lower the volume
	        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
	            // Resume playback 
	        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
//	            mAudioManager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
	            mAudioManager.abandonAudioFocus(afChangeListener);
	            // Stop playback
	        }
		}		
	};
}
