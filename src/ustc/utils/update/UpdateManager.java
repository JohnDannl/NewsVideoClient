package ustc.utils.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ustc.newsvideo.R;
import ustc.utils.cache.Cache;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {
	private static String serverUrl="http://222.195.78.181:8889/static/apk/newsvideo_update.xml";
	private NoUpdateReminder noUpdateListener=null;
	private Context mContext;	
	private Dialog noticeDialog;	
	private Dialog downloadDialog;	
	public static String downloadDir="app";
	private static String downloadAbsDir="/data/data/ustc.update/cache/app/";
//	private static String sdCardDir="/sdcard/data/app/";
	private String apkName="update.apk";
    private static String apkWholeName = "update";//any name just for initialization
    
    private ProgressBar mProgress;    
    private static final int DOWN_UPDATE = 1;    
    private static final int DOWN_OVER = 2;  
    private static final int HAS_UPDATE=3;
    private static final int NO_UPDATE=4;
    private static final int STOP_UPDATE=5;
    private int progress;    
    private Thread downLoadThread;    
    private boolean interceptFlag = false;
    
    private VersionDetector mDetector;
    private ServerVersionInfo serverInfo;
    
    public UpdateManager(Context context) {		
		this.mContext = context;
		mDetector=new VersionDetector(mContext);
	}
    
    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case HAS_UPDATE:
    			showNoticeDialog();
    			break;
    		case NO_UPDATE:
    			if(noUpdateListener!=null)noUpdateListener.remind();
    			break;
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				if(downloadDialog!=null&&downloadDialog.isShowing())downloadDialog.dismiss();
				installApk();
				break;
			case STOP_UPDATE:
				if(downloadDialog!=null&&downloadDialog.isShowing())downloadDialog.dismiss();
				showStopDialog();
			default:
				break;
			}
    	};
    };
	/*
	 * check if there is an update,should be called asynchronously
	 */
	private boolean hasUpdatedVersion(){
        String path = serverUrl;         
        InputStream is;
		try {
	        URL url = new URL(path);  
	        HttpURLConnection conn =  (HttpURLConnection) url.openConnection();   
	        conn.setConnectTimeout(5000); 
			is = conn.getInputStream();
			serverInfo = VersionParser.parse(is);  
			if(mDetector.getVersionCode()<serverInfo.getVersionCode())return true;
			else return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}        
          return false;
	}	
	/**
	 * Check for an update,if has, remind the user
	 */
	public void checkUpdateInfo(){
		if(isNetworkConnected(mContext)){
			new Thread(){
				public void run()
				{
					if(hasUpdatedVersion()){
						mHandler.sendEmptyMessage(HAS_UPDATE);
					}else{
						mHandler.sendEmptyMessage(NO_UPDATE);
					}
				}
			}.start();
		}
		else Toast.makeText(mContext, R.string.no_network_connection_toast, Toast.LENGTH_LONG).show();
	}	
	private boolean isNetworkConnected(Context context) { 
    	if (context != null) { 
    		try{
    			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
    			    	.getSystemService(Context.CONNECTIVITY_SERVICE); 
    			    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo(); 
    			    	if (mNetworkInfo != null&&mNetworkInfo.isConnectedOrConnecting()) 
    			    	{ 
    			    		return mNetworkInfo.isAvailable(); 	    	
    			    	}	
    		}catch(Exception e){
    			e.printStackTrace();
    			return false;
    		}	    	    	
    	} 
    	return false; 
    	}
	
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.update_new_version_find)
				+serverInfo.getVersionName());
//		builder.setMessage(updateMsg);
		builder.setMessage(serverInfo.getDescription());
//		builder.setMessage(serverInfo.toString());
		builder.setPositiveButton(R.string.update_download,	new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton(R.string.update_nexttime, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.update_download);
		
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.progress);
		
		builder.setView(v);
		builder.setNegativeButton(R.string.update_cancel, new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		
		downloadApk();
	}
	private void showStopDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.update_error_title);
//		builder.setMessage(updateMsg);
		builder.setMessage(R.string.update_error_message);
//		builder.setMessage(serverInfo.toString());
		builder.setPositiveButton(R.string.update_ok, new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();			
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
	}
	private Runnable downLoadApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(serverInfo.getApkUrl());
			
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				// If this encoding not set,server will return gzip format file 
				// the getContentLength() method will fail
				conn .setRequestProperty("Accept-Encoding", "identity");
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
//				if(hasSDCard())downloadDir=sdCardDir;		
				downloadAbsDir=Cache.getDiskCacheDir(mContext)+File.separator+downloadDir;
				File fileDir =new File(downloadAbsDir);
//				Log.d("XXXXXXXXXXXXX", fileDir.toString());
				if(!fileDir.exists()&&fileDir!=null){
					boolean state=fileDir.mkdir();
//					Log.d("XXXXXXXXXXX", "create dir "+fileDir.toString()+":"+state);
				}
//				apkWholeName=fileDir.toString()+File.separator+apkName;
				apkWholeName=downloadAbsDir+File.separator+apkName;
//				Log.d("XXXXXXXXXX", apkWholeName);
				String apkFile = apkWholeName;
				File ApkFile = new File(apkFile);
//				if(ApkFile.exists())ApkFile.delete();// write() will overwrite if file has existed
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
		    		if(numread <= 0){
		    			mHandler.sendEmptyMessage(DOWN_OVER);
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {		
				mHandler.sendEmptyMessage(STOP_UPDATE);
				e.printStackTrace();
			}catch(FileNotFoundException e) {
				mHandler.sendEmptyMessage(STOP_UPDATE);
				e.printStackTrace();
			}catch(IOException e){
				mHandler.sendEmptyMessage(STOP_UPDATE);
				e.printStackTrace();
			}		
		}
	};
	
	 /**
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(downLoadApkRunnable);
		downLoadThread.start();
	}
	 /**
     * @param url
     */
	private void installApk(){
		File apkFile = new File(apkWholeName);
        if (!apkFile.exists()) {
            return;
        }    
        /*
		 * Change the private directory visibility when There is no SD card 
		 */
//		if(!hasSDCard())
			changeVisibility();
		
        Intent i = new Intent(Intent.ACTION_VIEW);
//        Log.d("XXXXXXXXXX", apkFile.toString());
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);	
	}	
	 /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
	private void changeVisibility(){
        // [705:drwx---r-x]
	    String[] args1 = { "chmod", "701", downloadAbsDir};
	    LinuxCmd.exec(args1);
	    // [604:-rw----r--]
	    String[] args2 = { "chmod", "604", apkWholeName};
	    LinuxCmd.exec(args2);
	}  
	public void setNoUpdateReminder(NoUpdateReminder listener){
		if(listener!=null)noUpdateListener=listener;
	}
	public interface NoUpdateReminder{
		public void remind();
	}
}
