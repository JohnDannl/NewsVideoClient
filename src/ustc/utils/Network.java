package ustc.utils;

import ustc.newsvideo.R;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class Network {
	private static final String TAG = "ustc.utils.network";
		/**
	    * Simple network connection check.
	    *
	    * @param context
	    */
    public static boolean checkConnection(Context mContext) {
    	try{
    		final ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                Toast.makeText(mContext, R.string.no_network_connection_toast, Toast.LENGTH_LONG).show();
//                Log.e(TAG, "checkConnection - no connection found");
                return false;
            }
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
        return true;
    }
    public static String getMacAddress(Context mContext){
		WifiManager manager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String address = info.getMacAddress().replace(":", "_");
		return address;
	}
}
