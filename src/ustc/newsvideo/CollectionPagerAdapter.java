package ustc.newsvideo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ustc.newsvideo.data.Constant;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Toast;

public class CollectionPagerAdapter extends FragmentStatePagerAdapter {
	private ArrayList<String> custom_category_values=new ArrayList<String>();
	private ArrayList<String> custom_category_entries=new ArrayList<String>();

	public CollectionPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}
	public CollectionPagerAdapter(FragmentManager fm,Context context){
		super(fm);
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String[] entries=context.getResources().getStringArray(R.array.category_entry);
		String[] values=context.getResources().getStringArray(R.array.category_value);
    	Set<String> defValues=new HashSet<String>(Arrays.asList(values));
		//get the selected categories
		Set<String> valueSet=sharedPref.getStringSet("pref_category",defValues);
		if(valueSet.size()==0){
			Toast.makeText(context, context.getResources().getString(R.string.category_empty_warning),
					Toast.LENGTH_LONG).show();
		}
		for(int i=0;i<values.length;i++){
			//to keep the order in show
			if(valueSet.contains(values[i])){
				custom_category_values.add(values[i]);
				custom_category_entries.add(entries[i]);
			}
		}
	}
	@Override
    public Fragment getItem(int i) {
//        Fragment fragment = new ObjectFragment();
        Fragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
//        args.putInt(ContentFragment.ARG_CONTENT, i + 1);
//        args.putString(ContentFragment.ARG_CONTENT, Constant.category[i]);
        args.putString(ContentFragment.ARG_CONTENT, custom_category_values.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
//        return Constant.category.length;
    	return custom_category_values.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        return "OBJECT " + (position + 1);
//    	return Constant.category_ch[position];
    	return custom_category_entries.get(position);
    }
}

