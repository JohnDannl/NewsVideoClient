package ustc.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
/**
 * Surface view overrides {@link #onTouchEvent(MotionEvent)}
 * <br>implements {@link #OnDown()} and {@link #OnDoubleTap()}</br>
 * @author JohnDannl
 *
 */
public class XSurfaceView extends SurfaceView {	
	private GestureDetector gestureDetector;
	private OnGestureDetected gestureDetected;
	
	public XSurfaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		gestureDetector = new GestureDetector(context, new MyGestureListener());
	}
	public XSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		gestureDetector = new GestureDetector(context, new MyGestureListener());
	}
	public XSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
        gestureDetector = new GestureDetector(context, new MyGestureListener());
	}
	// skipping measure calculation and drawing

	// delegate the event to the gesture detector
	@Override
	public boolean onTouchEvent(MotionEvent e) {
	//Single Tap
	return gestureDetector.onTouchEvent(e);
	}
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDown(MotionEvent e) {
//			return false;
			if(gestureDetected!=null)gestureDetected.onDown(e);
		    return true;
		}
		// event when double tap occurs
		@Override
		public boolean onDoubleTap(MotionEvent e) {
//			return false;
			if(gestureDetected!=null)gestureDetected.onDoubleTap(e);
			return true;			
		}
		@Override
	    public boolean onScroll(MotionEvent e1, MotionEvent e2,
	             float distanceX, float distanceY){
			if(gestureDetected!=null)gestureDetected.onScroll(e1, e2, distanceX, distanceY);
			return true;	
		 }
	}
	/**
	 * Set gesture detector listener 
	 * @param gestureDet
	 */
	public void setOnGestureDetected(OnGestureDetected gestureDet){
		this.gestureDetected=gestureDet;
	}
	/**
	 * Public interface for gesture detector 
	 * @author JohnDannl
	 *
	 */
	public interface OnGestureDetected{		
		public void onDown(MotionEvent e);
		public void onDoubleTap(MotionEvent e);
		public void onScroll(MotionEvent e1, MotionEvent e2,
	             float distanceX, float distanceY);
	}
}
