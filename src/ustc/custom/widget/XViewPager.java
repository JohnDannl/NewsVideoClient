package ustc.custom.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * ViewPager overrides {@link #onTouchEvent(MotionEvent)} and {@link #onInterceptTouchEvent(MotionEvent)}}
 * <br>implements enable/disable scrolling action</br>
 * @author JohnDannl
 *
 */
public class XViewPager extends ViewPager {
	private boolean scrollEnabled=false;
	public XViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public XViewPager(Context context,AttributeSet attrs){
		super(context, attrs);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.scrollEnabled && super.onTouchEvent(event);        
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.scrollEnabled && super.onInterceptTouchEvent(event);
    }
	public void setScrollEnabled(boolean enabled){
		this.scrollEnabled=enabled;
	}

}
