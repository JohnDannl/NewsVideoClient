package ustc.newsvideo.data.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class NewsInfoParser {
	public static List<NewsInfo> parse(InputStream is) throws Exception {
		// TODO Auto-generated method stub
		List<NewsInfo> vods = null;  
        NewsInfo vod = null;
        
        XmlPullParser parser = Xml.newPullParser(); 
        parser.setInput(is, "UTF-8");               
        
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
                vods = new ArrayList<NewsInfo>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("item")) {  
                    vod = new NewsInfo();                  
                } else if (parser.getName().equals("vid")) {  
                    eventType = parser.next();  
                    vod.setVid(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("title")) {  
                    eventType = parser.next();  
                    vod.setTitle(parser.getText()==null?"":parser.getText());  
                } else if(parser.getName().equals("url")){
                	eventType = parser.next();  
                    vod.setUrl(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("thumb")){
                	eventType = parser.next();  
                    vod.setThumb(parser.getText()==null?NewsInfo.noImage:parser.getText());
                }else if(parser.getName().equals("brief")){
                	eventType = parser.next();  
                    vod.setBrief(parser.getText()==null?"":parser.getText());                
                }else if(parser.getName().equals("source")){
                	eventType = parser.next();  
                    vod.setSource(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("loadtime")){
                	eventType = parser.next();  
                    vod.setLoadtime(Long.parseLong(parser.getText()==null?"0":parser.getText()));
                } else if (parser.getName().equals("duration")) {  
                    eventType = parser.next();  
                    vod.setDuration(parser.getText()==null?"":parser.getText());  
                } else if (parser.getName().equals("web")) {  
                    eventType = parser.next();  
                    vod.setWeb(parser.getText()==null?"":parser.getText()); 
                }else if (parser.getName().equals("mvid")) {  
                    eventType = parser.next();  
                    vod.setMVid(parser.getText()==null?"":parser.getText()); 
                }else if(parser.getName().equals("mtype")){
                	eventType = parser.next();  
                    vod.setmType(parser.getText()==null?"":parser.getText());
                }else if(parser.getName().equals("click")){
                	eventType = parser.next();  
                    vod.setClick(Integer.parseInt(parser.getText()==null?"0":parser.getText()));
                }
                
                break;
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("item")) {  
                    vods.add(vod);  
                    vod = null;      
                }  
                break;  
            }
            eventType = parser.next();  
        }
        
		return vods;
	}
}
