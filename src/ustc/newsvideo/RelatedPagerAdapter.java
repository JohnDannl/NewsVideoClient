package ustc.newsvideo;

import java.util.List;

import ustc.newsvideo.data.parser.NewsInfo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;

public class RelatedPagerAdapter extends FragmentStatePagerAdapter {
	private List<NewsInfo> newsList=null;
	public RelatedPagerAdapter(FragmentManager fm,List<NewsInfo> newsInfoList) {
		super(fm);
		// TODO Auto-generated constructor stub
		newsList=newsInfoList;		
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return RelatedFragment.newInstance(newsList.get(arg0), arg0);
		//return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(newsList!=null)return newsList.size();
		return 0;
	}
	
	@Override
	public float getPageWidth (int position){
		return 0.42f;		
	}
	
	@Override
	public int getItemPosition(Object object) {
	    if (object instanceof RelatedFragment) {
	        ((RelatedFragment) object).updateTextColour();
	    }
	    //don't return POSITION_NONE, avoid fragment recreation. 
	    return super.getItemPosition(object);
	}
	
	public NewsInfo getNewsInfo(int position){
		if(newsList!=null&&position<getCount())return newsList.get(position);
		return null;
	}
}
