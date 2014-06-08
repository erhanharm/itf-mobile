package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vphoainha.itfmobile.NotificationActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.Notification;
import com.vphoainha.itfmobile.util.DateTimeHelper;

public class NotificationAdapter extends ArrayAdapter<Notification> {
	Activity act;
	List<Notification> lst;
	
	public NotificationAdapter(Activity act, int resource,
			int textViewResourceId, List<Notification> objects) {
		super(act, resource, textViewResourceId, objects);
		this.act=act;
		lst=objects;
	}

	public static class ViewHolder {
		public TextView tvTime, tvContent;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_notification, null);
	      
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();
		
		final Notification notify = getItem(position);
		holder.tvTime.setText(DateTimeHelper.dateTimeToDateStringDMY(notify.getTime()));
		holder.tvContent.setText(notify.getContent());
		
		NotificationActivity notificationActivity=((NotificationActivity)act);
		if (position >= (lst.size()-1) && !notificationActivity.isMaximum()) {
			notificationActivity.wsGetNotification();
		}
		
		return convertView;
	}
}