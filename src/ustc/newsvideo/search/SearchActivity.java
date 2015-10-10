package ustc.newsvideo.search;

import ustc.bitmap.imagecache.ImageCache;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.newsvideo.PlayerActivity;
import ustc.newsvideo.R;
import ustc.newsvideo.data.Constant;
import ustc.newsvideo.data.parser.NewsInfo;
import ustc.newsvideo.search.SearchFragment.OnSearchListItemClick;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class SearchActivity extends FragmentActivity {
	private TextView query_str=null;
	private static String userId;
	private NewsInfo playingNews;
	private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "thumbs";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);	       
		setupActionBar();
		handleIntent(getIntent());
		setupSearchView();	 
	 	userId=getUserId();
	 	
	 	final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
//        final int width = displayMetrics.widthPixels;
//        Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", height,width));
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, height*1/6);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);//fade in effection
        mImageFetcher.setLoadingImage(R.drawable.empty_photo); 
	}	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }
	private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
    		query_str=(TextView)findViewById(R.id.query_status);
            String query = intent.getStringExtra(SearchManager.QUERY);
          //use the query to search your data somehow
            if(query!=null){
            	updateSuggestionProvider(query);
            	query_str.setText("\""+query+"\""+getResources().getString(R.string.search_result));
            	replaceSearchFragment(query);
            }            
        }
    }
	@Override
    public void onResume() {
//		Log.d("XXXXXXXXXXXX", mtype+" resumed");
        super.onResume();
        if(mImageFetcher!=null)mImageFetcher.setExitTasksEarly(false);
    }

    @Override
	public void onPause() {
        super.onPause();
        if(mImageFetcher!=null){
	        mImageFetcher.setExitTasksEarly(true);
	        mImageFetcher.flushCache();
        }
//        Log.d("XXXXXXXXXXXX", mtype+" paused");
    }

    @Override
	public void onDestroy() {
        super.onDestroy();
//        Log.d("XXXXXXXXXXXX", mtype+" destoryed");
        if(mImageFetcher!=null)mImageFetcher.closeCache();
    }
	
	private void replaceSearchFragment(String keywords){
		if (findViewById(R.id.search_fragment_container) == null) return;
		// Create fragment and give it an argument specifying the article it should show
		SearchFragment newFragment = new SearchFragment();
		Bundle args = new Bundle();
		args.putString(SearchFragment.ARG_KEYWORDS, keywords);
		newFragment.setArguments(args);

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.search_fragment_container, newFragment);
		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
		/*newFragment.setOnSearchItemClickListener(new OnSearchListItemClick(){

			@Override
			public void OnItemClick(NewsInfo newsInfo) {
				// TODO Auto-generated method stub
				playNewsVideo(newsInfo,Constant.mode_search);				
			}
			
		});*/
	}
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		//Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//set screen orientation
		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		//Set full screen,the way using themes xml is preferred
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//        		WindowManager.LayoutParams.FLAG_FULLSCREEN);   
        final ActionBar actionBar = getActionBar();
		// Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(true);
        //Show action bar title
        actionBar.setDisplayShowTitleEnabled(false);
        // Hide activity bar icon
        //actionBar.setDisplayUseLogoEnabled(false);
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
	}
	private void setupSearchView(){
		SearchManager searchManager =
		           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView =
	            (SearchView)findViewById(R.id.search_view);
		searchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setQueryRefinementEnabled(true);
		// Two ways to expand the search menu item in order to show by default the query
	    //searchView.setIconified(false); //has been set in xml
		//searchView.onActionViewExpanded();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
//		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {	    
	    	case R.id.suggestions_clear:
	    		showClearAlertDialog();
	    		return true;
	    	case android.R.id.home:
	    		finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void showClearAlertDialog(){
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(getResources().getString(R.string.clear_suggestions));
		builder.setTitle(getResources().getString(R.string.clear_suggestions_title));
		builder.setPositiveButton(R.string.action_sure, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				clearSuggestionProvider();
				dialog.dismiss();
			}
			
		});
		builder.setNegativeButton(R.string.action_cancel, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
			
		});
		builder.create().show();
	}
	private void updateSuggestionProvider(String query){
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
	}
	private void clearSuggestionProvider(){
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
		        MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
		suggestions.clearHistory();
	}	
	public String getUserId(){
		 String userId=Network.getMacAddress(this);
		 if(userId==null)userId= AndroidDeviceId.getUUId(this);
		 if(userId==null)userId=Constant.pa_anonymous;
		 return userId;
	 }	
	
	public ImageFetcher getImageFetcher(){
		return mImageFetcher;
	}
}
