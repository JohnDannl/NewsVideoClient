package ustc.utils.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class VersionDetector {
	private Context mContext;
	public VersionDetector(Context context){
		mContext=context;
	}
	public String getVersionName() {  
	    //��ȡpackagemanager��ʵ��   
	    PackageManager packageManager =mContext.getPackageManager();  
	    //getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ  
	    String versionName="";
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			versionName=packInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    return versionName;   
	}  
	public int getVersionCode() {  
	    //��ȡpackagemanager��ʵ��   
	    PackageManager packageManager =mContext.getPackageManager();  
	    //getPackageName()���㵱ǰ��İ�����0�����ǻ�ȡ�汾��Ϣ  
	    int versionCode=-1;
	    PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			versionCode=packInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    return versionCode;   
	}  
}
