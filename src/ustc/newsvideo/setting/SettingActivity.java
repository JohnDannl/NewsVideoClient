package ustc.newsvideo.setting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ustc.newsvideo.MainActivity;
import ustc.newsvideo.R;
import ustc.utils.cache.Cache;
import ustc.utils.update.UpdateManager;
import ustc.utils.update.VersionDetector;
import ustc.utils.update.UpdateManager.NoUpdateReminder;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity {
	private static VersionDetector mDetector;
	private static String versionNum="1.0";
	private static boolean categoryChanged=false;
	
	@Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preferences_headers, target);
    }
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
        //Keep screen on
//  		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  		//set screen orientation
  		//setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  		//Set full screen,the way using themes xml is preferred
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//        		WindowManager.LayoutParams.FLAG_FULLSCREEN);        
        // Display the fragment as the main content.
        /*getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();*/
  		/*getFragmentManager().beginTransaction()
        .replace(R.id.setting_custom_container, new SettingsFragment())
        .commit();*/
  		mDetector=new VersionDetector(this);
		if(mDetector.getVersionName()!=null)versionNum=mDetector.getVersionName();
		final ActionBar actionBar = getActionBar();
		//Show action bar title
        actionBar.setDisplayShowTitleEnabled(false);
        // Hide activity bar icon
        actionBar.setDisplayHomeAsUpEnabled(true);
        categoryChanged=false;
        //Log.d("XXXXXXXXonCreated", Boolean.toString(categoryChanged));
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {	
	    	case android.R.id.home:
	    		if(categoryChanged)
	    		NavUtils.navigateUpTo(this,
						new Intent(this, MainActivity.class));
	    		else finish();
	    		return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	public static class SettingsFragment extends PreferenceFragment {
		
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	        String setting = getArguments().getString("setting");
	        if(setting.equals("player")){
	        	// Load the preferences from an XML resource
		        addPreferencesFromResource(R.xml.prefs_player);
	        }	        
	        else if(setting.equals("category")){
	        	addPreferencesFromResource(R.xml.prefs_category);
	        	MultiSelectListPreference pref_category = (MultiSelectListPreference)findPreference("pref_category");
	        	if(pref_category!=null){
	        		//set the selected categories
	        		Set<String> valueSet=(Set<String>)pref_category.getValues();
					CharSequence[] entries=pref_category.getEntries();
					CharSequence[] values=pref_category.getEntryValues();
					if(valueSet.size()==entries.length){
						pref_category.setSummary(getResources().getString(R.string.pref_category_enum_summ));
					}
					else if(valueSet.size()>0){
						ArrayList<String> curEntries=new ArrayList<String>();
						for(int i=0;i<entries.length;i++){
							//to keep the order in show
							if(valueSet.contains((String)values[i]))curEntries.add((String)entries[i]);
						}						
						String cvStr="";
						for(int i=0;i<curEntries.size()-1;i++){
							cvStr+=curEntries.get(i)+"¡¢";
						}
						cvStr+=curEntries.get(curEntries.size()-1);							
						pref_category.setSummary(cvStr);
						}
					else{
						pref_category.setSummary(getResources().getString(R.string.pref_category_empty));					
					}
					//set OnChangeListener
	        		pref_category.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){

						@Override
						public boolean onPreferenceChange(
								Preference preference, Object newValue) {
							// TODO Auto-generated method stub
							MultiSelectListPreference pref_category=(MultiSelectListPreference)preference;
							Set<String> valueSet=(Set<String>)newValue;
							CharSequence[] entries=pref_category.getEntries();
							CharSequence[] values=pref_category.getEntryValues();
							if(valueSet.size()==entries.length){
								pref_category.setSummary(getResources().getString(R.string.pref_category_enum_summ));
							}
							else if(valueSet.size()>0){
								ArrayList<String> curEntries=new ArrayList<String>();
								for(int i=0;i<entries.length;i++){
									//to keep the order in show
									if(valueSet.contains((String)values[i]))curEntries.add((String)entries[i]);
								}						
								String cvStr="";
								for(int i=0;i<curEntries.size()-1;i++){
									cvStr+=curEntries.get(i)+"¡¢";
								}
								cvStr+=curEntries.get(curEntries.size()-1);							
								pref_category.setSummary(cvStr);
								}
							else{
								pref_category.setSummary(getResources().getString(R.string.pref_category_empty));					
							}				
//							return false;
							categoryChanged=true;
							return true;
						}	        			
	        		});
	        	}
	        }
	        else if(setting.equals("update")){
	        	addPreferencesFromResource(R.xml.prefs_update);
	        	Preference manual_update = (Preference)findPreference("button_manual_update");
	        	if(manual_update!=null){
	        		manual_update.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// TODO Auto-generated method stub
							UpdateManager mUpdateManager = new UpdateManager(getActivity());
					        mUpdateManager.setNoUpdateReminder(new NoUpdateReminder(){

								@Override
								public void remind() {
									// TODO Auto-generated method stub
									Toast.makeText(getActivity(),R.string.already_updated, Toast.LENGTH_SHORT).show();
								}
					        	
					        });
					        mUpdateManager.checkUpdateInfo();
//							Toast.makeText(getActivity(), R.string.already_updated, Toast.LENGTH_SHORT).show();
							return true;
//							return false;
						}	        			
	        		});
	        	}	        	
	        }
	        else if(setting.equals("cache")){
	        	addPreferencesFromResource(R.xml.prefs_cache);
	        	Preference clear_cache = (Preference)findPreference("button_clear_cache");
	        	Long bufSize=Cache.getCacheSize(Cache.getDiskCacheDir(getActivity()));
	        	clear_cache.setSummary(getActivity().getResources().getString(R.string.pref_cache_size)
	        			+String.format("%.2f", ((double)bufSize)/1000000)+" M");
	        	if(clear_cache!=null){
	        		clear_cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// TODO Auto-generated method stub
							//Cache.clearDir(Cache.getDiskCacheDir(getActivity()));	
							Cache.clearDirFiles(Cache.getDiskCacheDir(getActivity()));
				        	Toast.makeText(getActivity(), R.string.cache_cleared, Toast.LENGTH_SHORT).show();

							//cache clear is an AsyncTask,so set bufSize to zero directly
				        	preference.setSummary(getActivity().getResources().getString(R.string.pref_cache_size)
				        			+String.format("%.2f", 0.0)+" M");
							return true;
//							return false;
						}	        			
	        		});
	        	}	     
	        }
	        else if(setting.equals("disclaimer")){
	        	addPreferencesFromResource(R.xml.prefs_disclaimer);
	        	Preference disclaimer = (Preference)findPreference("pref_disclaimer");
	        	if(disclaimer!=null)
	        		disclaimer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

						@Override
						public boolean onPreferenceClick(Preference preference) {
							// TODO Auto-generated method stub
							AlertDialog.Builder builder = new Builder(getActivity());
							builder.setTitle(R.string.setting_disclaimer);
							builder.setMessage(R.string.pref_disclaimer);
							builder.setPositiveButton(R.string.ok, new OnClickListener(){

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
								
							});
							builder.create().show();
							return true;
						}	        		
	        	});
	        }
	        else if(setting.equals("about")){
	        	addPreferencesFromResource(R.xml.prefs_about);
	        	Preference about=(Preference)findPreference("pref_about");
	        	about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener(){

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setTitle(R.string.setting_about);
						String text = String.format(getResources().getString(R.string.pref_about_long),
								getResources().getString(R.string.app_name),versionNum);
						builder.setMessage(text);
						builder.setPositiveButton(R.string.ok, new OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
							
						});
						builder.create().show();
						return true;
					}
	        		
	        	});
	        }
	    }
	}
}
