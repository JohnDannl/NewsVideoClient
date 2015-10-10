package ustc.utils.update;

public class ServerVersionInfo {
		private int versionCode;
		private String versionName;
		private String apkUrl;
		private String description;
		public ServerVersionInfo(){}
		public void setVersionCode(int verCode){versionCode=verCode;}
		public void setVersionName(String verName){this.versionName=verName;}
		public void setApkUrl(String url){this.apkUrl=url;}
		public void setDescription(String des){this.description=des;}
		public int getVersionCode(){return this.versionCode;}
		public String getVersionName(){return this.versionName;}
		public String getApkUrl(){return this.apkUrl;}
		public String getDescription(){return this.description;}
		public String toString(){
			return versionCode+","+versionName+","+apkUrl+","+description;
		}	
}
