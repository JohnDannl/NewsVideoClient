package ustc.newsvideo;

import ustc.utils.update.UpdateManager;
import android.app.Application;
import android.preference.PreferenceManager;

public class MyApplication extends Application {
	
    public static boolean start_launch=true;
    
	public MyApplication(){
		// this method fires only once per application start. 
        // getApplicationContext returns null here
	}
	
	@Override
    public void onCreate() {
        super.onCreate();
        // this method fires once as well as constructor 
        // but also application has context here
		start_launch=true;
	}
}
