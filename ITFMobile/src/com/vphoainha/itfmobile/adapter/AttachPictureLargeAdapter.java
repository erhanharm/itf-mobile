package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.AttachPicture;

public class AttachPictureLargeAdapter extends ArrayAdapter<AttachPicture> {
	Activity act;
	List<AttachPicture> lst;
	
	public AttachPictureLargeAdapter(Activity act, int resource,
			int textViewResourceId, List<AttachPicture> objects) {
		super(act, resource, textViewResourceId, objects);
		this.act=act;
		lst=objects;
	}

	public static class ViewHolder {
		public TextView tvName;
		public ImageView ivPic;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_attachpic, null);
	      
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();
		
		final AttachPicture attachPicture = getItem(position);
		holder.tvName.setText(attachPicture.getName());
		holder.ivPic.setImageBitmap(attachPicture.getBitmap());
		
		return convertView;
	}
	
}