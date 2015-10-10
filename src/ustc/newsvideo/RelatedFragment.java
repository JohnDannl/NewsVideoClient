package ustc.newsvideo;

import ustc.bitmap.imagecache.ImageFetcher;
import ustc.bitmap.imagecache.Utils;
import ustc.newsvideo.data.Constant;
import ustc.newsvideo.data.parser.NewsInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RelatedFragment extends Fragment {
	private String title,thumb;
	private int index=0;
	private ImageFetcher mImageFetcher;
	private ImageView mImageView;
	private TextView mTitle;
	private Button btn_play;
	private int width;
	private int height;
	
	public static RelatedFragment newInstance(NewsInfo newsInfo,int position){
		RelatedFragment rf=new RelatedFragment();
		
		final Bundle args = new Bundle();
        args.putString("title", newsInfo.getTitle());
        args.putString("thumb", newsInfo.getThumb());
        args.putInt("index", position);
        rf.setArguments(args);
		return rf;
	}
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        if (args!=null){
        	title=args.getString("title");
        	thumb=args.getString("thumb");
        	index=args.getInt("index");
        }
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        final int height = displayMetrics.heightPixels;
        height = displayMetrics.heightPixels/5;
        width=displayMetrics.widthPixels/3;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.related_news_item, container, false);
        LinearLayout newsItem=(LinearLayout)v.findViewById(R.id.related_item);
        newsItem.setLayoutParams(new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.MATCH_PARENT));
        mTitle=(TextView)v.findViewById(R.id.related_title);
        mTitle.setText(title);
        if(((PlayerActivity)getActivity()).getPlayingRelatedIndex()==index){
        	mTitle.setTextColor(getResources().getColor(R.color.dark_tangerine));
        }else{
        	mTitle.setTextColor(getResources().getColor(R.color.white));
        }
        mImageView = (ImageView) v.findViewById(R.id.related_thumb);
        btn_play=(Button)v.findViewById(R.id.related_play_btn);
        return v;
    }
    
    /**
     * Execution order:onCreated --> onCreatedView -->onActivityCreated
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (PlayerActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((PlayerActivity) getActivity()).getImageFetcher();
            mImageFetcher.loadImage(thumb, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        btn_play.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((PlayerActivity) getActivity()).playRelatedVideo(index,Constant.mode_click);
			}
        	
        });
    }
    public void updateTextColour(){
    	if(((PlayerActivity)getActivity()).getPlayingRelatedIndex()==index){
        	mTitle.setTextColor(getResources().getColor(R.color.dark_tangerine));
        }else{
        	mTitle.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
