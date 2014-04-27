package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.Expert;

public class ExpertAdapter extends ArrayAdapter<Expert> {
	Activity act;
	List<Expert> lst;
	
	public ExpertAdapter(Activity act, int resource,
			int textViewResourceId, List<Expert> objects) {
		super(act, resource, textViewResourceId, objects);
		this.act=act;
		lst=objects;
	}

	public static class ViewHolder {
		public TextView tvId,tvAsked, tvName, tvLikedAnswer;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_expert, null);
	      
			holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
			holder.tvAsked = (TextView) convertView.findViewById(R.id.tvAsked);
			holder.tvLikedAnswer = (TextView) convertView.findViewById(R.id.tvLikedAnswer);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();
		
		final Expert expert = getItem(position);
		holder.tvId.setText("#" + expert.getIndex());
		holder.tvName.setText(expert.getUserName());
		holder.tvAsked.setText(expert.getCounterAsked()+" asked");
		holder.tvLikedAnswer.setText(expert.getCounterLiked()+" liked");
		
		return convertView;
	}
}