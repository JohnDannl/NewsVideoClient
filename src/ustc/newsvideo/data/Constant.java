package ustc.newsvideo.data;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static String testUri="http://f01.v1.cn/group1/M00/01/73/ChQBFVQBJSmAY-GAAHLI_NrXKmY378.flv";
//	http://222.195.78.187:8889/news/more?num=10&web=merge&loadtime=2014-09-04%2017:37:00&vid=01f9b968-aed7-4a3b-b437-b732634d4936
//	http://222.195.78.187:8889/news/refresh?num=10&web=merge&loadtime=2014-09-04%2017:37:00&vid=01f9b968-aed7-4a3b-b437-b732634d4936
//	http://222.195.78.187:8889/news/top?num=10&web=merge

//	http://222.195.78.187:8889/video/a?web=sina&vid=135565908
//	public static String newsHost="http://222.195.78.187:8889/news/";
//	public static String videoHost="http://222.195.78.187:8889/video/a?";
//	public static String host="http://222.195.78.187:8889/search/a?";
	
	public static String newsHost="http://222.195.78.181:8889/news/";
	public static String videoHost="http://222.195.78.181:8889/video/a?";
	public static String searchHost="http://222.195.78.181:8889/search/a?";	
	public static String pa_webEq="&web=";
	public static String pa_numEq="&num=";
	public static String pa_mtypeEq="&mtype=";
	public static String pa_clickEq="&click=";
	public static String pa_ldtimeEq="&loadtime=";
	public static String pa_vidEq="&vid=";
	public static String pa_mvidEq="&mvid=";
	public static String pa_useridEq="&userid=";
	public static String pa_modeEq="&mode=";
	public static String pa_anonymous="anonymous";
	public static String pa_keywordsEq="&keywords=";
	public static String pa_pageEq="&page=";
	public static String mode_click="click";
	public static String mode_auto="auto";
	public static String mode_search="search";
	public static String mode_related="related";
	public static int pageNum=15;
	public static String[] web={"sina","sohu","v1","ifeng","kankan","china","qq","merge"};
	public static String op_top="top?",op_refresh="refresh?",op_more="more?",op_related="related?";
//	merge_en=['newest','hot','world','domestic','society','finance','military','science','entertain','sport','ipai','other']
	public static String[] category={"newest","hot","world","domestic","society","finance","military",
		"science","entertain","sport","ipai","other"};
	public static String[] category_ch={"最新","最热","国际","国内","社会","财经","军事",
		"科技","娱乐","体育","爱拍","其他"};
	
	public static Map<String,String> categoryMap=new HashMap<String,String>();
	
	static{
		for(int i=0;i<category.length;i++){
			categoryMap.put(category[i], category_ch[i]);
		}
	}
}
