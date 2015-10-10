package ustc.newsvideo;

import io.vov.vitamio.LibsChecker;
import ustc.bitmap.imagecache.ImageCache;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.XViewPager;
import ustc.newsvideo.search.SearchActivity;
import ustc.newsvideo.setting.SettingActivity;
import ustc.utils.update.UpdateManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private static String TAG="NewsVideo_MainActivity";
    private CollectionPagerAdapter mCollectionPagerAdapter;
    private XViewPager mViewPager;
    private ImageFetcher mImageFetcher;
	private static final String IMAGE_CACHE_DIR = "thumbs";
	private UpdateManager mUpdateManager;
    private boolean auto_update=true;    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//set audio controls
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		if(!new AudioPreset(this).requestVolumeControll()){
			Toast.makeText(this, R.string.audio_device_occupation, Toast.LENGTH_SHORT).show();
		}
		if (!LibsChecker.checkVitamioLibs(this))
		      return;
		// check update info
		auto_update=PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_auto_update", true);
		if(auto_update&&MyApplication.start_launch){
			MyApplication.start_launch=false; //To ensure just to execute when app launches
			mUpdateManager = new UpdateManager(this);	        
	        mUpdateManager.checkUpdateInfo();
				}
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//      final int height = displayMetrics.heightPixels;
		int screenHeight = displayMetrics.heightPixels;
		//Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", height,width));
        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, screenHeight/5);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);//will revoke the background empty image
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        
		// ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mCollectionPagerAdapter = new CollectionPagerAdapter(getSupportFragmentManager(),this);		
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        //actionBar.setHomeButtonEnabled(false);
        //Hide action bar title
        //actionBar.setDisplayShowTitleEnabled(false);
        // Hide activity bar icon
        //actionBar.setDisplayUseLogoEnabled(false);
        //actionBar.setDisplayShowHomeEnabled(false);
        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
     // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (XViewPager) findViewById(R.id.pager);
        mViewPager.setScrollEnabled(true);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
            	// corresponding tab.
                getActionBar().setSelectedNavigationItem(position);
            }
        });
        
     // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
            	 // When the given tab is selected, switch to the corresponding page in the ViewPager.
//                mViewPager.setCurrentItem(tab.getPosition());
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mCollectionPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener));
        }
	}
	 @Override
    public void onResume() {			
        super.onResume();        
        if(mImageFetcher!=null)mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {	    	
        super.onPause();
        if(mImageFetcher!=null){
        	mImageFetcher.setExitTasksEarly(true);
    	    mImageFetcher.flushCache();
        }
    }

    @Override
    protected void onDestroy() {    	
        super.onDestroy();    
        if(mImageFetcher!=null)mImageFetcher.closeCache();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
//		return false;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    	case R.id.setting_search:
	    		startSearchActivity();
	    		return true;
	        case R.id.setting_custom:
	            startSettingActivity();
	            return true;	
	        case R.id.setting_exit:
	        	stopActivity();
                return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void startSearchActivity(){
		Intent searchIntent=new Intent(this,SearchActivity.class);
		startActivity(searchIntent);
	}
	private void startSettingActivity(){
//		Toast.makeText(this, "custom", Toast.LENGTH_SHORT).show();
		Intent settingIntent=new Intent(this,SettingActivity.class);
		startActivity(settingIntent);
	}
	private void stopActivity(){
		finish();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
	public ImageFetcher getImageFetcher(){
		return mImageFetcher;
	}
}
