package ustc.newsvideo.data.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class VideoInfoParser {
	public static List<VideoInfo> parse(InputStream is)throws Exception{
		List<VideoInfo> vods=null;
		VideoInfo vod=null;
		List<String> urls=null;
		
		XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");               
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
                vods = new ArrayList<VideoInfo>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("video")) {  
                    vod = new VideoInfo();   
                    urls=new ArrayList<String>();
                } else if (parser.getName().equals("furl")) {  
                    eventType = parser.next();  
                    if(parser.getText()!=null)urls.add(parser.getText());  
                } 
                break;
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("video")) {  
                	vod.setUrls(urls);
                    vods.add(vod);  
                    vod = null;     
                    urls=null;
                }  
                break;  
            }
            eventType = parser.next();  
        }
        
		return vods;
	}
}
