package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.ReplyAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Reply;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;


public class ThreadActivity extends FatherActivity {
	List<Reply> replies;
	String msg;
	
	Thread curThread;
	
	ListView lvReply;
	public TextView tvTime, tvContent, tvAuthor, tvNumLike, tvNumDisLike;
	public LinearLayout lnLike, lnDisLike;
	public ImageView ivLike, ivDisLike;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		initFather();

		String subtitle="";
		for(int i=0;i<AppData.folders.size();i++)
			subtitle+=" > "+AppData.folders.get(i).getName();
		
		curThread=(Thread)getIntent().getSerializableExtra("thread");
		tvTitle.setText(curThread.getTitle());
		tvSubTitle.setText("ITF"+subtitle);
		
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvContent = (TextView) findViewById(R.id.tvContent);
//		tvNumLike = (TextView) findViewById(R.id.tvNumLike);
//		tvNumDisLike = (TextView) findViewById(R.id.tvNumDisLike);
		tvAuthor = (TextView) findViewById(R.id.tvAuthor);
//		lnLike=(LinearLayout)findViewById(R.id.lnLike);
//		lnDisLike=(LinearLayout)findViewById(R.id.lnDisLike);
//		ivLike=(ImageView)findViewById(R.id.ivLike);
//		ivDisLike=(ImageView)findViewById(R.id.ivDisLike);
		
		tvTime.setText(DateTimeHelper.dateTimeToDateString(curThread.getTime()));
		tvContent.setText(curThread.getContent());
		tvAuthor.setText(curThread.getUserName());
		
//		btn_new.setVisibility(View.VISIBLE);
//		btn_new.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if(!AppData.isLogin){
//					startActivity(new Intent(ThreadActivity.this,LoginActivity.class));
//				}
//				else {
//					startActivity(new Intent(ThreadActivity.this,AddThreadActivity.class));
//				}
//			}
//		});
		
		wsGetReplies();
	}

	public void wsGetReplies() {
		if(!Util.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else{
			int user_id=(AppData.isLogin?AppData.saveUser.getId():-1);
			(new jsGetReplys()).execute(new String[] { WsUrl.URL_GET_REPLIES, Integer.toString(curThread.getId()), Integer.toString(user_id) });
		}
	}

	public class jsGetReplys extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(ThreadActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("thread_id", params[1]));
			par.add(new BasicNameValuePair("user_id", params[2]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					replies = new ArrayList<Reply>();
					JSONArray array = json.getJSONArray(JsonTag.TAG_REPLIES);

					// looping through All Products
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Reply t = new Reply();
						t.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						t.setContent(obj.getString(JsonTag.TAG_CONTENT));
						t.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));
						t.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
						t.setUserName(obj.getString(JsonTag.TAG_USER_NAME));
						t.setThreadId(Integer.parseInt(obj.getString(JsonTag.TAG_THREAD_ID)));
						t.setCountLiked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_LIKED)));
						t.setCountDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_DISLIKED)));
						t.setIsLiked(Integer.parseInt(obj.getString(JsonTag.TAG_ISLIKED)));
						t.setIsDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_ISDISLIKED)));
						
						replies.add(t);
					}
					msg=json.getString("message");
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
			
			if (result ==1) {
				runOnUiThread(new Runnable() {
					public void run() {
						lvReply = (ListView) findViewById(R.id.lvReply);
						ReplyAdapter adapter = new ReplyAdapter(
								ThreadActivity.this,
								R.layout.list_item_thread, R.id.tv_index,
								replies);
						lvReply.setAdapter(adapter);
						Util.setListViewHeightBasedOnChildren(lvReply, adapter);
					}
				});
			} else {
				Toast.makeText(ThreadActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	
}
