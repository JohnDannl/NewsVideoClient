package ustc.utils;

import java.util.UUID;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

/** 
 * <strong>getDeviceId():</strong>Returns the unique device ID, for example, 
 * the IMEI for GSM and the MEID or ESN for CDMA phones.<br>
 * <strong>getSimSerialNumber():</strong>Returns the serial number of the SIM, if applicable.<br>
 * <strong>Settings.Secure.ANDROID_ID:</strong>A 64-bit number (as a hex string) that is randomly 
 * generated when the user first sets up the device and should remain constant for 
 * the lifetime of the user's device.<br>
 * <strong>android.os.Build.SERIAL:</strong>A hardware serial number, if available. Alphanumeric only, 
 * case-insensitive<br>
 * @author JohnDannl
 *
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class AndroidDeviceId {
	/**
	 * Requires Permission:
     *   {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
	 * @param mContext
	 * @return deviceId
	 */
	public static String getUUId(Context mContext){
		final TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDeviceId, tmSimSerial, androidId,buildSerial;
	    tmDeviceId = "" + teleManager.getDeviceId();
	    tmSimSerial = "" + teleManager.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	    buildSerial=""+android.os.Build.SERIAL;
	    //hashCode() returns a 32-bit signed integer according to the object's equals()
	    UUID deviceUuid = new UUID((long)androidId.hashCode()<<32 | buildSerial.hashCode(), ((long)tmDeviceId.hashCode() << 32) | tmSimSerial.hashCode());
	    String uuidStr = deviceUuid.toString();
		return uuidStr;
	    /*return "DeviceID:"+tmDeviceId.hashCode()+"\nSimSerial:"+tmSimSerial.hashCode()
	    		+"\nandroidId:"+androidId+"\nserial:"+buildSerial
	    		+"\nspace:"+(""+null).hashCode()+"\ndeviceId:"+deviceId;*/
	}
	public static String getDeviceId(Context mContext){
		final TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDeviceId;
	    tmDeviceId = "" + teleManager.getDeviceId();
	    return tmDeviceId;
	}
	public static String getSimSerial(Context mContext){
		final TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmSimSerial;
	    tmSimSerial = "" + teleManager.getSimSerialNumber();
	    return tmSimSerial;
	}
	public static String getAndroidId(Context mContext){
		final TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

	    final String androidId;
	    androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	    return androidId;
	}
	public static String getHwSerial(Context mContext){
		final TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

	    final String buildSerial;
	    buildSerial=""+android.os.Build.SERIAL;
	    return buildSerial;
	}
}
