package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.AttachPicture;

public class AttachPictureAdapter extends ArrayAdapter<AttachPicture> {
	Activity act;
	List<AttachPicture> lst;
	private OnClickRemove onCRemove;
	
	public AttachPictureAdapter(Activity act, int resource,
			int textViewResourceId, List<AttachPicture> objects) {
		super(act, resource, textViewResourceId, objects);
		this.act=act;
		lst=objects;
	}

	public static class ViewHolder {
		public TextView tvName;
		public ImageView ivPic;
		public Button btnRemove;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_attachpic, null);
	      
			holder.ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
			holder.btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();
		
		final AttachPicture attachPicture = getItem(position);
		holder.tvName.setText(attachPicture.getName());
		holder.ivPic.setImageBitmap(attachPicture.getBitmap());
		
		holder.btnRemove.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onCRemove.onClickRemove(position);
			}
		});
		
		return convertView;
	}
	
	public void setOnClickRemoveListener(OnClickRemove onCRemove){
		this.onCRemove = onCRemove;
	}
	
	public interface OnClickRemove{
		public void onClickRemove(int pos);
	}
}