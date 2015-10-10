package ustc.utils.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.util.Xml;

public class VersionParser {
	
	public VersionParser(){}	
	
	/* 
	 * 用pull解析器解析服务器返回的xml文件 (xml封装了版本号) 
	 */  
	public static ServerVersionInfo parse(InputStream is){  
	    XmlPullParser  parser = Xml.newPullParser();    
	    ServerVersionInfo info = new ServerVersionInfo();//实体  
	    try {
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();  		    
		    while(type != XmlPullParser.END_DOCUMENT ){  
		        switch (type) {  
		        case XmlPullParser.START_TAG:  
		        	if("versionCode".equals(parser.getName())){
		        		info.setVersionCode(Integer.parseInt(parser.nextText()));
//		        		Log.d("XXXXXXXXXX", String.valueOf(info.getVersionCode()));
		        	}
		            if("versionName".equals(parser.getName())){  
		                info.setVersionName(parser.nextText()); //获取版本号  
		            }else if ("apkUrl".equals(parser.getName())){  
		                info.setApkUrl(parser.nextText()); //获取要升级的APK文件  
		            }else if ("description".equals(parser.getName())){  
		                info.setDescription(parser.nextText()); //获取该文件的信息  
		            }  
		            break;  
		        }  
		        type = parser.next();  
		    }  
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//设置解析的数据源   
	    catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	    
	    return info;  
	} 	
}
