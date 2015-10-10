package ustc.newsvideo.data.parser;

public class NewsInfo{
	
	public static String noImage="http://222.195.78.181:8889/static/news.ico";
	private String vid,title,url,thumb,brief,source,duration,web,mvid,mtype;
	private int click;
	private long loadtime;
	
	public NewsInfo(){}
	public NewsInfo(String web,String vid,String title,String brief,long loadtime,String source,String mvid){
		this.web=web;
		this.vid=vid;
		this.title=title;
		this.brief=brief;
		this.loadtime=loadtime;
		this.source=source;
		this.mvid=mvid;
	}
	public String getVid(){return this.vid;}
	public String getTitle(){return this.title;}
	public String getUrl(){return this.url;}
	public String getThumb(){return this.thumb;}
	public String getBrief(){return this.brief;}
	public String getSource(){return this.source;}
	public long getLoadtime(){return this.loadtime;}
	public String getDuration(){return this.duration;}
	public String getWeb(){return this.web;}
	public String getMVid(){return this.mvid;}
	public String getmType(){return this.mtype;}
	public int getClick(){return this.click;}
		
	public void setVid(String vid){this.vid=vid;}
	public void setTitle(String title){this.title=title;}
	public void setUrl(String url){this.url=url;}
	public void setThumb(String thumb){this.thumb=thumb;}
	public void setBrief(String brief){this.brief=brief;}
	public void setSource(String source){this.source=source;}
	public void setLoadtime(long loadtime){this.loadtime=loadtime;}
	public void setDuration(String duration){this.duration=duration;}
	public void setWeb(String web){this.web=web;}
	public void setMVid(String mvid){this.mvid=mvid;}
	public void setmType(String mtype){this.mtype=mtype;}
	public void setClick(int click){this.click=click;}
}
