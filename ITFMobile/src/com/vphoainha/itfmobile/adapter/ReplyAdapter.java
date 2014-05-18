package com.vphoainha.itfmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.AddReplyActivity;
import com.vphoainha.itfmobile.LoginActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Reply;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;

public class ReplyAdapter extends ArrayAdapter<Reply> {
	
	Activity act;
	List<Reply> lst;
	String msg;
	
	Reply save_reply;
	int save_pos;
	
	public ReplyAdapter(Activity context, int resource, int textViewResourceId, List<Reply> objects) {
		super(context, resource, textViewResourceId, objects);
		act=context;
		lst=objects;
	}
	
	public static class ViewHolder {
		public TextView tvTime, tvIndex, tvContent, tvAuthor, tvNumLike, tvNumDisLike, tvEdit, tvDelete, tvQuote;
		public LinearLayout lnLike, lnDisLike;
		public ImageView ivLike, ivDisLike;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_reply, null);
	      
			holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			holder.tvIndex = (TextView) convertView.findViewById(R.id.tvIndex);
			holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
			holder.tvEdit = (TextView) convertView.findViewById(R.id.tvEdit);
			holder.tvDelete = (TextView) convertView.findViewById(R.id.tvDelete);
			holder.tvQuote = (TextView) convertView.findViewById(R.id.tvQuote);
			holder.tvNumLike = (TextView) convertView.findViewById(R.id.tvNumLike);
			holder.tvNumDisLike = (TextView) convertView.findViewById(R.id.tvNumDisLike);
			holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
			holder.lnLike=(LinearLayout)convertView.findViewById(R.id.lnLike);
			holder.lnDisLike=(LinearLayout)convertView.findViewById(R.id.lnDisLike);
			holder.ivLike=(ImageView)convertView.findViewById(R.id.ivLike);
			holder.ivDisLike=(ImageView)convertView.findViewById(R.id.ivDisLike);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();

		final Reply reply = lst.get(position);

		holder.tvIndex.setText("#"+(position+2));
		holder.tvTime.setText(DateTimeHelper.dateTimeToDateString(reply.getTime()));
		holder.tvContent.setText(reply.getContent());
		holder.tvAuthor.setText("by "+reply.getUserName());
		holder.tvNumLike.setText(reply.getCountLiked()+"");
		holder.tvNumDisLike.setText(reply.getCountDisliked()+"");
		
		int clike,cunlike;
		clike=act.getResources().getColor(R.color.text_like);
		cunlike=act.getResources().getColor(R.color.text_unlike);
		if(reply.getIsLiked()==1){
			holder.ivLike.setImageDrawable(Util.getDrawable(act, "like"));
			holder.tvNumLike.setTextColor(clike);
		}
		else {
			holder.ivLike.setImageDrawable(Util.getDrawable(act, "unlike"));
			holder.tvNumLike.setTextColor(cunlike);
		}
		if(reply.getIsDisliked()==1){
			holder.ivDisLike.setImageDrawable(Util.getDrawable(act, "dislike"));
			holder.tvNumDisLike.setTextColor(clike);
		}
		else{
			holder.ivDisLike.setImageDrawable(Util.getDrawable(act, "undislike"));
			holder.tvNumDisLike.setTextColor(cunlike);
		}

		holder.lnLike.setTag(R.string.TAG_REPLY, reply);
		holder.lnLike.setTag(R.string.TAG_POS, position);
		holder.lnDisLike.setTag(R.string.TAG_REPLY, reply);
		holder.lnDisLike.setTag(R.string.TAG_POS, position);
		
		holder.lnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!AppData.isLogin){
					act.startActivity(new Intent(act,LoginActivity.class));
				}
				else {
					save_reply=(Reply)v.getTag(R.string.TAG_REPLY);
					save_pos=(Integer)v.getTag(R.string.TAG_POS);
					if(save_reply.getIsLiked()==0)
						wsLikeDislike(WsUrl.URL_LIKE_REPLY, 0, save_reply.getId());
					else wsLikeDislike(WsUrl.URL_UNLIKE_REPLY, 1, save_reply.getId());
				}
			}
		});
		holder.lnDisLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!AppData.isLogin){
					act.startActivity(new Intent(act,LoginActivity.class));
				}
				else {
					save_reply=(Reply)v.getTag(R.string.TAG_REPLY);
					save_pos=(Integer)v.getTag(R.string.TAG_POS);
					if(save_reply.getIsDisliked()==0)
						wsLikeDislike(WsUrl.URL_DISLIKE_REPLY, 2, save_reply.getId());
					else wsLikeDislike(WsUrl.URL_UNDISLIKE_REPLY, 3, save_reply.getId());
				}
			}
		});
		
		if(reply.getUserId()==AppData.saveUser.getId()) holder.tvEdit.setVisibility(View.VISIBLE);
		else holder.tvEdit.setVisibility(View.GONE);
		
		holder.tvEdit.setTag(R.string.TAG_REPLY, reply);
		holder.tvDelete.setTag(R.string.TAG_REPLY, reply);
		holder.tvQuote.setTag(R.string.TAG_REPLY, reply);
		holder.tvDelete.setTag(R.string.TAG_POS, position);
		holder.tvEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save_reply=(Reply)v.getTag(R.string.TAG_REPLY);
				
				Intent in=new Intent(act, AddReplyActivity.class);
				in.putExtra("mode", 2);
				in.putExtra("reply", save_reply);
				act.startActivityForResult(in, 113);
			}
		});
		holder.tvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				save_reply=(Reply)v.getTag(R.string.TAG_REPLY);
				save_pos=(Integer)v.getTag(R.string.TAG_POS);
				
				Builder ad = new AlertDialog.Builder(act);
				ad.setMessage("Do you want to reply this thread?");
				ad.setCancelable(true);
				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						wsDeleteReply();
					}
				});
				ad.setNegativeButton("No", null);
				ad.show();
			}
		});
		holder.tvQuote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!AppData.isLogin){
					act.startActivity(new Intent(act,LoginActivity.class));
				}
				else {
					save_reply=(Reply)v.getTag(R.string.TAG_REPLY);
					
					Intent in=new Intent(act,AddReplyActivity.class);
					in.putExtra("thread_id", save_reply.getThreadId());
					in.putExtra("quote_content", save_reply.getUserName()+" told: \" "+save_reply.getContent()+" \"");
					act.startActivityForResult(in, 113);
				}
			}
		});
		
		return convertView;
	}

	@Override
	public int getCount() {
		return lst.size();
	}
	
	//=======================================//
	public void wsLikeDislike(String url, int taskIndex, int reply_id) {
		(new jsLikeDislike()).execute(new String[] { url,
				Integer.toString(AppData.saveUser.getId()),
				Integer.toString(reply_id), Integer.toString(taskIndex) });
	}

	public class jsLikeDislike extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		int taskIndex;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(act);
			pd.setMessage("Processing...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			taskIndex=Integer.parseInt(params[3]);
			
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("user_id", params[1]));
			par.add(new BasicNameValuePair("reply_id", params[2]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());
			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				msg=json.getString(JsonTag.TAG_MESSAGE);
				if (success == 1) {
					return 1;
				} else {
					return 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			if(result==1){
				switch(taskIndex){
				case 0: 
					save_reply.setCountLiked(save_reply.getCountLiked()+1);
					save_reply.setIsLiked(1);
					break;
				case 1:
					save_reply.setCountLiked(save_reply.getCountLiked()-1);
					save_reply.setIsLiked(0);
					break;
				case 2:
					save_reply.setCountDisliked(save_reply.getCountDisliked()+1);
					save_reply.setIsDisliked(1);
					break;
				case 3:
					save_reply.setCountDisliked(save_reply.getCountDisliked()-1);
					save_reply.setIsDisliked(0);
					break;
				}
				lst.set(save_pos, save_reply);
				notifyDataSetChanged();
			}
		}
	}	
	
	//=======================================================
	
	public void wsDeleteReply() {
		if(!Util.checkInternetConnection(act))
			Toast.makeText(act, act.getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else{
			(new jsDeleteReply())
				.execute(new String[] { WsUrl.URL_DELETE_REPLY,
						Integer.toString(save_reply.getId())});
		}
	}
	
	public class jsDeleteReply extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(act);
			pd.setMessage("Deleting...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("reply_id", params[1]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());
	
			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					return "success";
				} else {
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	
			return null;
		}
	
		@Override
		protected void onPostExecute(String result) {
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			if (result != null) {
				Toast.makeText(act, "Your reply was deleted!", Toast.LENGTH_SHORT).show();
				
				lst.remove(save_pos);
				notifyDataSetChanged();
			} else {
				Toast.makeText(act, "Sorry! Deleted fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
