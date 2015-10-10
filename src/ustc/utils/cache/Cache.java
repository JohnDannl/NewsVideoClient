package ustc.utils.cache;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
/**
 * Manage yourself cache directory and do not rely on system garbage collection
 * @author JohnDannl
 *
 */
public class Cache {
	/**
	 * Get directory or file size in bytes
	 * @param fileOrDirectory
	 * @return
	 */
	public static long getCacheSize(File fileOrDirectory){
		long count=0;
		if(fileOrDirectory.isDirectory()){
			for(File child:fileOrDirectory.listFiles())
				count+=getCacheSize(child);
			return count;
		}
		count=fileOrDirectory.length();
		return count;
	}
	/**
	 * You may want to use File.separator instead of "/" or "\" to construct a path
	 * @param context
	 * @return
	 */
	public static File getCacheDir(Context context) {        
    	File cachePath =context.getCacheDir();
        return cachePath;
    }
	/**
	 * You may want to use File.separator instead of "/" or "\" to construct a path
	 * @param context
	 * @return
	 */
	public static File getDiskCacheDir(Context context) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final File cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context) :
                                context.getCacheDir();
//    	 final String cachePath =context.getCacheDir().getPath();
        return cachePath;
    }
	/**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                                context.getCacheDir().getPath();
//    	 final String cachePath =context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @TargetApi(9)
    public static boolean isExternalStorageRemovable() {
        if (SdkVersion.hasGingerbread()) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) {
        if (SdkVersion.hasFroyo()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }  
    /**
     * Delete a directory including itself,if wanting to exclude itself,using {@link #clearDir(File)}
     * @param fileOrDirectory
     */
    public static void deleteDir(File fileOrDirectory){
    	if(fileOrDirectory.exists()){
    		DeleteCacheAsync cacheTask=new DeleteCacheAsync();
    		cacheTask.execute(fileOrDirectory);
    	}
    }
    /**
     * Delete content of specified directory excluding itself,<br>
     * if wanting to include itself using {@link #deleteDir(File)}
     * @param directory
     */
    public static void clearDir(File directory){
    	if(directory.isDirectory()){
    		ClearCacheAsync cacheTask=new ClearCacheAsync();
    		cacheTask.execute(directory);
    	}
    }
    /**
     * Delete all files under a directory and its sub-directories recursively but keep the <br>
     * directory tree.If need to delete the directory as well,use {@link #clearDir(File)}
     * @param directory
     */
    public static void clearDirFiles(File directory){
    	if(directory.isDirectory()){
    		ClearFilesAsync cacheTask=new ClearFilesAsync();
    		cacheTask.execute(directory);
    	}
    }
    /**
     * Delete a directory including itself,if excluding itself,using {@link #clearDirectory(File)}<br>
     * Deleted directory must be empty,so use recursive call
     * @param fileOrDirectory
     */
    private static void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                DeleteRecursive(child);
        
        fileOrDirectory.delete();
    }
    /**
     * Delete all files under a directory and its sub-directories recursively but keep the <br>
     * directory tree.If need to delete the directory as well,use {@link #DeleteRecursive(File)}
     * @param fileOrDirectory
     */
    private static void clearRecursive(File fileOrDirectory){
    	if(fileOrDirectory.isDirectory())
    		for (File child:fileOrDirectory.listFiles())
    			clearRecursive(child);
    	else fileOrDirectory.delete();    	
    }
    /**
     * Delete content of specified directory
     * @param directory
     */
    private static void clearDirectory(File directory){
    	if (directory.isDirectory())
            for (File child : directory.listFiles())
                DeleteRecursive(child);
    }
    private static class DeleteCacheAsync extends AsyncTask<File,Void,Void>{

		@Override
		protected Void doInBackground(File... params) {
			// TODO Auto-generated method stub
			DeleteRecursive(params[0]);
			return null;
		}

    }
    private static class ClearCacheAsync extends AsyncTask<File,Void,Void>{

		@Override
		protected Void doInBackground(File... params) {
			// TODO Auto-generated method stub
			clearDirectory(params[0]);
			return null;
		}
    	
    }
    private static class ClearFilesAsync extends AsyncTask<File,Void,Void>{

		@Override
		protected Void doInBackground(File... params) {
			// TODO Auto-generated method stub
			clearRecursive(params[0]);
			return null;
		}
    	
    }
}
