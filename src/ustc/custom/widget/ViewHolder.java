package ustc.custom.widget;

import android.util.SparseArray;
import android.view.View;

/**
 * A ViewHolder for general use when the view just contains a sort of layout
 * @author dannl
 *
 */
public class ViewHolder {
	private SparseArray<View> views;
	private View convertView;
	
	private ViewHolder(View convertView){
		this.views=new SparseArray<View>();
		this.convertView=convertView;
		convertView.setTag(this);
	}
	public static ViewHolder get(View convertView){
		if(convertView==null){
			return null;
		}else{
			if(convertView.getTag()==null){
				return new ViewHolder(convertView);
			}else{
				return (ViewHolder)convertView.getTag();
			}
		}		
	}
	public <T extends View> T getView(int viewId){
		View view=views.get(viewId);
		if(view==null){
			view=convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}
}
