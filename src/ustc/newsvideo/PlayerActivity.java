package ustc.newsvideo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import ustc.bitmap.imagecache.ImageCache;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.XViewPager;
import ustc.newsvideo.data.Constant;
import ustc.newsvideo.data.parser.NewsInfo;
import ustc.newsvideo.data.parser.NewsInfoParser;
import ustc.newsvideo.data.parser.VideoInfo;
import ustc.newsvideo.data.parser.VideoInfoParser;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import ustc.video.player.XCPlayer;
import ustc.video.player.XCPlayer.PlayerExtraController;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

public class PlayerActivity extends FragmentActivity{
	
	private XCPlayer mPlayer=null;
	private static String userId;
	private static boolean autoPlayEnable=true;
	private GetVideoInfo videoInfoTask=null;
	private NewsInfo playingNews;	
	private LinearLayout player_part2=null;
	private TextView news_brief=null;
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "thumbs";
	private ViewPager mViewPager;
	private RelatedPagerAdapter mRelatedAdapter;
	private int playingRelatedIndex=-1;
	private ProgressBar relatedPgBar;
	private GetRelatedNews mGetRelatedTask=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);	
		//Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// UI initialization
		player_part2=(LinearLayout) findViewById(R.id.player_part2);
		Button tbrbtn=(Button)findViewById(R.id.titlebar_return_btn);
		tbrbtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//NavUtils.navigateUpFromSameTask(this);	//will reload the activity
				//onBackPressed();	// or use finish()
				if(mPlayer!=null&&mPlayer.isFullScreen()){	
					//When is full-screen,switch to in-screen
					mPlayer.setFullScreen(false);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
					if(player_part2!=null)player_part2.setVisibility(View.VISIBLE);
					setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
				else
					finish();
			}
			
		});		

		news_brief=(TextView)findViewById(R.id.news_brief);
		news_brief.setMovementMethod(new ScrollingMovementMethod());        
        // Related news
		relatedPgBar=(ProgressBar)findViewById(R.id.related_progress);	
  		mViewPager=(ViewPager)findViewById(R.id.related_news);
  		mViewPager.setOffscreenPageLimit(2); 
  		
		userId=getUserId();
		autoPlayEnable=getIsAutoPlayEnabled();
		init_player();		
		
		Intent intent=getIntent();
		Bundle bundle=intent.getBundleExtra("newsInfo");
		String title=bundle.getString("title");
		String web=bundle.getString("web");
		String vid=bundle.getString("vid");
		String mode=bundle.getString("mode");
		String brief=bundle.getString("brief");
		long loadtime=bundle.getLong("loadtime");
		String source=bundle.getString("source");
		String mvid=bundle.getString("mvid");
		NewsInfo mainNews=new NewsInfo(web,vid,title,brief,loadtime,source,mvid);
		playNewsVideo(mainNews,mode);
		getRelatedNews(mainNews);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//      final int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		//Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", height,width));
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, width/4);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);//will revoke the background empty image
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);        	
	}
	private void init_player(){
		View rootView =this.findViewById(R.id.surfaceview_container);	
	    mPlayer=new XCPlayer(this,rootView); 
	    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(autoPlayEnable)playNextRelated();
			}
		});
	    mPlayer.setPlayerExtraController(new PlayerExtraController(){

			@Override
			public void switchtoFullScreen() {
				// TODO Auto-generated method stub
				if(player_part2!=null)player_part2.setVisibility(View.GONE);
				// Hide status bar
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				//set screen orientation
				setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}

			@Override
			public void switchtoInlineScreen() {
				// TODO Auto-generated method stub
				// Show status bar
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				if(player_part2!=null)player_part2.setVisibility(View.VISIBLE);
				setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}

			@Override
			public void onClickPrev() {
				// TODO Auto-generated method stub
				playPreviousRelated();
			}

			@Override
			public void onClickNext() {
				// TODO Auto-generated method stub
				playNextRelated();
			}

			@Override
			public String getCurrentTitle() {
				// TODO Auto-generated method stub
				if(playingNews!=null)return playingNews.getTitle();
				return null;
			}

			@Override
			public void toggleRelatedButton() {
				// TODO Auto-generated method stub
				
			}
	    	
	    });
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.player, menu);
		return false;
		//return true;
	}	
	/*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
	@Override
    public void onResume() {
        super.onResume();
        if(mImageFetcher!=null)mImageFetcher.setExitTasksEarly(false);
    }   
	@Override
	public void onPause() {
	 if(videoInfoTask!=null)videoInfoTask.cancel(true);
        if(mPlayer!=null){
        	mPlayer.doCleanUp();
        	mPlayer.releaseMediaPlayer();
        }        
        super.onPause();
        if(mImageFetcher!=null){
	        mImageFetcher.setExitTasksEarly(true);
	        mImageFetcher.flushCache();
        }
        }
	@Override
	public void onDestroy() {
//	        Log.d("XXXXXXXXXXXX", mtype+" destoryed");
        if(videoInfoTask!=null)videoInfoTask.cancel(true);
        if(mPlayer!=null){
        	mPlayer.doCleanUp();
        	mPlayer.releaseMediaPlayer();
        }
        super.onDestroy();
        if(mImageFetcher!=null)mImageFetcher.closeCache();
    }
	
	private void playNewsVideo(NewsInfo nInfo,String mode){
		 if(videoInfoTask!=null){
				videoInfoTask.cancel(true);
				if(mPlayer!=null)mPlayer.hidePlayerProgressBar();
				}
		 if(nInfo!=null){
			 	playingNews=nInfo;
				videoInfoTask=new GetVideoInfo();
				try{
					videoInfoTask.execute(nInfo.getWeb(),nInfo.getVid(),userId,mode);
				}catch(IllegalStateException e){
					e.printStackTrace();
				}				
		 	}				
	 }
	public void playRelatedVideo(int position,String mode){
		playingRelatedIndex=position;
		if(mRelatedAdapter!=null){
			playNewsVideo(mRelatedAdapter.getNewsInfo(position),mode);
			mRelatedAdapter.notifyDataSetChanged();
		}
		
	}
	private void playNextRelated(){
		if(mRelatedAdapter!=null){
			if(playingRelatedIndex<mRelatedAdapter.getCount()-1){		
				playRelatedVideo(playingRelatedIndex+1,Constant.mode_auto);
			}
			else{
				if(mRelatedAdapter.getCount()>0)
				Toast.makeText(this, R.string.current_the_last, Toast.LENGTH_SHORT).show();
				else Toast.makeText(this, R.string.current_playlist_empty, Toast.LENGTH_SHORT).show();
			}
		}
	}
	private void playPreviousRelated(){
		if(mRelatedAdapter!=null){
			if(playingRelatedIndex>0){
				playRelatedVideo(playingRelatedIndex-1,Constant.mode_auto);
			}
			else{
				Toast.makeText(this,R.string.current_the_first, Toast.LENGTH_SHORT).show();
			}
		}
	}
	public int getPlayingRelatedIndex(){
		return playingRelatedIndex;
	}
	private class GetVideoInfo extends AsyncTask<String,Void,List<VideoInfo>>{
//	 	private Dialog loadingDialog=null;
	 	
		@Override
		protected void onPreExecute(){	
//			loadingDialog = MyProgressBar.createLoadingDialog(PlayerActivity.this, "");
//			loadingDialog.show();
			if(mPlayer!=null){
				mPlayer.showPlayerProgressBar();
			}
		}
		@Override
		protected List<VideoInfo> doInBackground(String... params) {
			// TODO Auto-generated method stub
			List<VideoInfo> infos=null;
			try{					
				URL url=new URL(Constant.videoHost+Constant.pa_webEq+params[0]+Constant.pa_vidEq+params[1]+
						Constant.pa_useridEq+params[2]+Constant.pa_modeEq+params[3]); 
	        	HttpURLConnection conn=(HttpURLConnection)url.openConnection();
	        	conn.setConnectTimeout(8*1000);
				conn.setDoInput(true);
				if(this.isCancelled()){						
					conn.disconnect();
					return infos;
				}
				conn.connect();
				InputStream is=conn.getInputStream();  	  
				if(this.isCancelled()){
					is.close();
//					Log.d("XXXAfter", newsInfoList.get(mRelatedAdapter.getSelectedIndex()).getVid()+" cancelled");
//					Log.d("XXXAfter", this.toString()+" cancelled");
					return infos; 						
				}
	            infos = VideoInfoParser.parse(is);
		        is.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			return infos;			
		}
		@Override
		protected void onPostExecute(List<VideoInfo> list){
//			if(loadingDialog.isShowing())loadingDialog.dismiss();	
			if(mPlayer!=null)mPlayer.hidePlayerProgressBar();
			if(list!=null){
//				for(VideoInfo item:list){
//					Log.d("XXXXXXXX", item.getTitle()+"->"+item.getWeb());					
//				}
				
				if(mPlayer!=null&&list.size()>0){
					//mPlayer.playVideo(list.get(0).getUrls().get(0));					
					//Log.d("XXXXGetVideoInfo", list.get(0).getUrls().get(0));
					mPlayer.playVideo(list.get(0).getUrls().toArray(new String[list.size()]));
					if(news_brief!=null){
						news_brief.setText(playingNews.getTitle()+"\n"
								+getResources().getString(R.string.source)+playingNews.getSource()+"\n"
								+new SimpleDateFormat("yyyy-MM-dd HH:mm:SS").format(new Date(playingNews.getLoadtime()*1000))+"\n"
								+getResources().getString(R.string.brief)+playingNews.getBrief());
					}
				}
				else if(list.size()<=0){
					Toast.makeText(PlayerActivity.this, R.string.video_loading_failure, Toast.LENGTH_SHORT).show();
				}
			}
			else{
				if(Network.checkConnection(PlayerActivity.this))
				Toast.makeText(PlayerActivity.this, R.string.server_connection_timeout, Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onCancelled(List<VideoInfo> list){
			super.onCancelled();			
		}
	}
	public void getRelatedNews(NewsInfo newsInfo){
		if(mGetRelatedTask!=null)mGetRelatedTask.cancel(true);
		
		mGetRelatedTask=new GetRelatedNews();
		String reqUrl=Constant.newsHost+Constant.op_related+Constant.pa_webEq+newsInfo.getWeb()
        		+Constant.pa_mvidEq+newsInfo.getMVid()+Constant.pa_numEq+"9";
		mGetRelatedTask.execute(reqUrl);
	}
	private class GetRelatedNews extends AsyncTask<String,Void,List<NewsInfo>>{
			
			@Override
			protected void onPreExecute(){	
				relatedPgBar.setVisibility(View.VISIBLE);
			}
			@Override
			protected List<NewsInfo> doInBackground(String... params) {
				// TODO Auto-generated method stub
				List<NewsInfo> infos=null;
				try{
					URL url=new URL(params[0]); 
		        	HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		        	conn.setConnectTimeout(8*1000);
					conn.setDoInput(true);
					conn.connect();
					InputStream is=conn.getInputStream();  	  
		             
		            infos = NewsInfoParser.parse(is);
			        is.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				return infos;			
			}
			@Override
			protected void onPostExecute(List<NewsInfo> list){		
				if(list!=null){
					if(list.size()>0){
						mRelatedAdapter=new RelatedPagerAdapter(getSupportFragmentManager(),list);
				 		mViewPager.setAdapter(mRelatedAdapter);	
					}
					relatedPgBar.setVisibility(View.GONE);
				}
				else{
					if(Network.checkConnection(PlayerActivity.this))
					Toast.makeText(PlayerActivity.this, R.string.server_connection_timeout, Toast.LENGTH_SHORT).show();
					//if size of newsInfoList is zero,triple operations are possible				
				}
			}
		}
	private String getUserId(){
		 String userId=Network.getMacAddress(this);
		 if(userId==null)userId= AndroidDeviceId.getUUId(this);
		 if(userId==null)userId=Constant.pa_anonymous;
		 return userId;
	 }
	 private boolean getIsAutoPlayEnabled(){
		 SharedPreferences sharedPrefs=PreferenceManager.getDefaultSharedPreferences(this);
		 return sharedPrefs.getBoolean("pref_auto_play", true);
	 }
	public ImageFetcher getImageFetcher(){
			return mImageFetcher;
	 }
}
