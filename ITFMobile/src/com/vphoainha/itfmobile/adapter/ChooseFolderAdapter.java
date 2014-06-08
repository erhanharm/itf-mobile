package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.Folder;

public class ChooseFolderAdapter extends ArrayAdapter<Folder> {
	Activity act;
	List<Folder> lst;
	
	public ChooseFolderAdapter(Activity act, int resource,
			int textViewResourceId, List<Folder> objects) {
		super(act, resource, textViewResourceId, objects);
		this.act=act;
		lst=objects;
	}

	public static class ViewHolder {
		public TextView tvName, tvNote;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.row_home_group2, null);
	      
			holder.tvName = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvNote = (TextView) convertView.findViewById(R.id.tvNote);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();
		
		final Folder f = getItem(position);
		holder.tvName.setText(f.getName());
		if(f.getNote()!=null && !f.getNote().equals("")){
			holder.tvNote.setText(f.getNote());
			holder.tvNote.setVisibility(View.VISIBLE);
		}
		else holder.tvNote.setVisibility(View.GONE);

		return convertView;
	}
}