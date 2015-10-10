package ustc.newsvideo;


import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import ustc.bitmap.imagecache.ImageCache;
import ustc.bitmap.imagecache.ImageFetcher;
import ustc.custom.widget.ViewHolder;
import ustc.newsvideo.data.Constant;
import ustc.newsvideo.data.parser.NewsInfo;
import ustc.newsvideo.data.parser.NewsInfoParser;
import ustc.utils.AndroidDeviceId;
import ustc.utils.Network;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ContentFragment extends Fragment {
	public static final String ARG_CONTENT = "content";
	private String mtype=null;
	private List<NewsInfo> newsInfoList=new ArrayList<NewsInfo>();
	private XListView mListView=null;
	private ImageFetcher mImageFetcher;
	private int screenHeight=0;
	private NewsAdapter mNewsAdapter=null;	
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mtype=args.getString(ARG_CONTENT, Constant.category[0]);
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        final int height = displayMetrics.heightPixels;
        screenHeight = displayMetrics.heightPixels;
        
        GetNewsInfo newsInfoTask=new GetNewsInfo();
        String reqUrl=Constant.newsHost+Constant.op_top+Constant.pa_webEq
        		+Constant.web[7]+Constant.pa_numEq+"10"+Constant.pa_mtypeEq+mtype;
		newsInfoTask.execute(reqUrl,Constant.op_top);
	}
	 @Override
	 public View onCreateView(LayoutInflater inflater,
	         ViewGroup container, Bundle savedInstanceState) {
	     // The last two arguments ensure LayoutParams are inflated
	     // properly.
		 View rootView = inflater.inflate(R.layout.fragment_content, container, false);		    
	     mListView=(XListView)rootView.findViewById(R.id.c_listview);
	     mListView.setPullLoadEnable(true);
	     mListView.setPullRefreshEnable(true);
	     mNewsAdapter=new NewsAdapter(newsInfoList);
	     mListView.setAdapter(mNewsAdapter);
	   /*  mListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), "Selected:"+Integer.toString(position), Toast.LENGTH_SHORT).show();
				//note:this position is started from 1 but not 0
				int selectedIndex=position-1;
				playCurrent(selectedIndex,Constant.mode_click);				
			}
	     });*/
	     mListView.setXListViewListener(new IXListViewListener(){

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				GetNewsInfo newsInfoTask=new GetNewsInfo();
				if(newsInfoList!=null&&newsInfoList.size()>0){
					NewsInfo nInfo=newsInfoList.get(0);
			        String reqUrl="";
					try {
						reqUrl = Constant.newsHost+Constant.op_refresh
								+Constant.pa_webEq+Constant.web[7]+Constant.pa_numEq+"10"
								+Constant.pa_ldtimeEq+String.valueOf(nInfo.getLoadtime())
								//+Constant.pa_ldtimeEq+URLEncoder.encode(nInfo.getLoadtime(),"utf-8")
								+Constant.pa_mvidEq+URLEncoder.encode(nInfo.getMVid(),"utf-8")
								+Constant.pa_mtypeEq+mtype
								+Constant.pa_clickEq+nInfo.getClick();
	//					Log.d("XXXXXXXXXXXX", reqUrl);
						newsInfoTask.execute(reqUrl,Constant.op_refresh);
					}catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}		
				}
				else{
					String reqUrl=Constant.newsHost+Constant.op_top+Constant.pa_webEq
							+Constant.web[7]+Constant.pa_numEq+"10"+Constant.pa_mtypeEq+mtype;
					try{
						newsInfoTask.execute(reqUrl,Constant.op_top);
					}catch(IllegalStateException e){
						e.printStackTrace();
					}					
				}
			}

			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				GetNewsInfo newsInfoTask=new GetNewsInfo();
				if(newsInfoList!=null&&newsInfoList.size()>0){
					NewsInfo nInfo=newsInfoList.get(newsInfoList.size()-1);
					String reqUrl="";
					try {					
						reqUrl = Constant.newsHost+Constant.op_more
								+Constant.pa_webEq+Constant.web[7]+Constant.pa_numEq+"10"
								+Constant.pa_ldtimeEq+String.valueOf(nInfo.getLoadtime())
								+Constant.pa_mvidEq+URLEncoder.encode(nInfo.getMVid(),"utf-8")
								+Constant.pa_mtypeEq+mtype
								+Constant.pa_clickEq+nInfo.getClick();
//						Log.d("XXXXXXXXXXXX", reqUrl);
						newsInfoTask.execute(reqUrl,Constant.op_more);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch(IllegalStateException e){
						e.printStackTrace();
					}
				}
				else{
					String reqUrl=Constant.newsHost+Constant.op_top+Constant.pa_webEq
							+Constant.web[7]+Constant.pa_numEq+"10"+Constant.pa_mtypeEq+mtype;
					try{
						newsInfoTask.execute(reqUrl,Constant.op_top);
					}catch(IllegalStateException e){
						e.printStackTrace();
					}					
				}
					
			}});	     
	     return rootView;
	 }	
	 @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Execute after onCreateView()
        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (MainActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((MainActivity) getActivity()).getImageFetcher();
        }
	 }
	 /**
	  * params[0]:web,params[1]:num
	  *
	  * @author JohnDannl
	  *
	  */
	 private class GetNewsInfo extends AsyncTask<String,Void,List<NewsInfo>>{
		 	private String operation=null;
//		 	private Dialog loadingDialog=null;
		 	
			@Override
			protected void onPreExecute(){	
//				loadingDialog = MyProgressBar.createLoadingDialog(getActivity(), "");
//				loadingDialog.show();
			}
			@Override
			protected List<NewsInfo> doInBackground(String... params) {
				// TODO Auto-generated method stub
				List<NewsInfo> infos=null;
				try{
					operation=params[1];
					URL url=new URL(params[0]); 
		        	HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		        	conn.setConnectTimeout(8*1000);
					conn.setDoInput(true);
					conn.connect();
					InputStream is=conn.getInputStream();  	  
		             
		            infos = NewsInfoParser.parse(is);
			        is.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				return infos;			
			}
			@Override
			protected void onPostExecute(List<NewsInfo> list){
//				if(loadingDialog.isShowing())loadingDialog.dismiss();			
				if(list!=null){
//					for(NewsInfo item:list){
//						Log.d("XXXXXXXX", item.getTitle()+"->"+item.getWeb());					
//					}
					if(operation.equals(Constant.op_refresh)){
						if(list.size()>0){
							for(NewsInfo item:list){
//								Because the refresh operation returns an inverse order list 
								newsInfoList.add(0, item);
							}
						}
						String timeStr="";
						Date date = new Date(); 
						DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						timeStr=sdf.format(date);
						mListView.setRefreshTime(timeStr);
						mListView.stopRefresh();
					}
					else if(operation.equals(Constant.op_more)){
						if(list.size()>0)newsInfoList.addAll(list);	
						mListView.stopLoadMore();
					}					
					else{
						if(list.size()>0)newsInfoList.addAll(list);
						//if size of newsInfoList is zero,triple operations are possible
						mListView.stopRefresh();
						mListView.stopLoadMore();
					}
					mNewsAdapter.notifyDataSetChanged();
				}
				else{
					if(Network.checkConnection(getActivity()))
					Toast.makeText(getActivity(), R.string.server_connection_timeout, Toast.LENGTH_SHORT).show();
					//if size of newsInfoList is zero,triple operations are possible
					if(mListView!=null){
						mListView.stopRefresh();
						mListView.stopLoadMore();
					}
				}
			}
		}
	 /**
	  * If currentIndex is set to -1,play the currently selected one,
	  * otherwise play the specified currentIndex one.<br>
	  * mode specify the user click mode:auto or click
	  * @param currentIndex
	  * @param mode
	  */
	 private void playCurrent(int currentIndex,String mode){
		 if(mNewsAdapter!=null){
			 if(currentIndex==-1)currentIndex=mNewsAdapter.getSelectedIndex();
			 if(currentIndex>mNewsAdapter.getCount()||currentIndex<0)return;
		 	mNewsAdapter.setSelectedIndex(currentIndex);
			NewsInfo nInfo=newsInfoList.get(currentIndex);
			playNewsVideo(nInfo,mode);
		 }		 
	 }
	 private void playNewsVideo(NewsInfo nInfo,String mode){
		 Intent playerIntent=new Intent(getActivity(),PlayerActivity.class);
		 Bundle bundle=new Bundle();
		 bundle.putString("title", nInfo.getTitle());
		 bundle.putString("web", nInfo.getWeb());
		 bundle.putString("vid", nInfo.getVid());
		 bundle.putString("mode", mode);
		 bundle.putString("brief", nInfo.getBrief());
		 bundle.putLong("loadtime", nInfo.getLoadtime());
		 bundle.putString("source",nInfo.getSource());
		 bundle.putString("mvid", nInfo.getMVid());
		 playerIntent.putExtra("newsInfo", bundle);
		 startActivity(playerIntent);
	 }
	 private class NewsAdapter extends BaseAdapter{
		private List<NewsInfo> newsArray=null;
		private int selectedIndex;
		public NewsAdapter(List<NewsInfo> newsInfoList){
			this.newsArray=newsInfoList;
			selectedIndex = -1;
		}		
		
		public void setSelectedIndex(int index)
	    {
	        selectedIndex = index;
	        notifyDataSetChanged();
	    }
		public int getSelectedIndex(){
			return selectedIndex;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return newsArray.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return newsArray.get(position).getTitle();
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView==null){
				convertView = LayoutInflater.from(getActivity())  
	                    .inflate(R.layout.news_list_item, null); 
			}
			ViewHolder holder=ViewHolder.get(convertView);
			LinearLayout newsItem=(LinearLayout)holder.getView(R.id.news_item);
		    newsItem.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenHeight*11/60));
//		    Log.d("XXXXXXXXXXXXX", String.format("h:%d,w:%d", newsItem.getLayoutParams().height,newsItem.getLayoutParams().width));
			ImageView thumb=(ImageView)holder.getView(R.id.news_thumb);
			mImageFetcher.loadImage(newsArray.get(position).getThumb(), thumb);			
			TextView title=(TextView)holder.getView(R.id.news_title);
			title.setText(newsArray.get(position).getTitle());
			TextView loadtime=(TextView)holder.getView(R.id.news_ldtime);
			Date date = new Date(newsArray.get(position).getLoadtime()*1000); 
			DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			loadtime.setText(sdf.format(date));
			Button play_btn=(Button)holder.getView(R.id.news_play_btn);
			play_btn.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					playCurrent(position,Constant.mode_click);
					//Toast.makeText(getActivity(), "Clicke item:"+position, Toast.LENGTH_SHORT).show();	
					}
				
			});
			if(selectedIndex!= -1 && position == selectedIndex){
				title.setTextColor(getResources().getColor(R.color.dark_tangerine));
			}
			else{
				title.setTextColor(getResources().getColor(R.color.white));
			}
			return convertView;
		}
		 
	 }
}
