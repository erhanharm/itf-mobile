package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;


public class ThreadActivity extends FatherActivity {
	List<Reply> replies;
	String msg;
	
	Thread curThread;
	
	ListView lvReply;
	public TextView tvTime, tvContent, tvAuthor, tvNumLike, tvNumDisLike, tvEdit, tvDelete, tvQuote, tvTag;
	public LinearLayout lnLike, lnDisLike;
	public ImageView ivLike, ivDisLike;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread);
		initFather();

		if(AppData.folders!=null){
			String subtitle="";
			for(int i=0;i<AppData.folders.size();i++)
				subtitle+=" > "+AppData.folders.get(i).getName();
			tvSubTitle.setText("ITF"+subtitle);
		}
		else tvSubTitle.setVisibility(View.GONE);
		
		curThread=(Thread)getIntent().getSerializableExtra("thread");
		tvTitle.setText(curThread.getTitle());
		
		tvTime = (TextView) findViewById(R.id.tvTime);
		tvContent = (TextView) findViewById(R.id.tvContent);
		tvAuthor = (TextView) findViewById(R.id.tvAuthor);
		tvEdit = (TextView) findViewById(R.id.tvEdit);
		tvDelete = (TextView) findViewById(R.id.tvDelete);
		tvQuote = (TextView) findViewById(R.id.tvQuote);
		tvTag = (TextView) findViewById(R.id.tvTag);
		
		tvNumLike = (TextView) findViewById(R.id.tvNumLike);
		tvNumDisLike = (TextView) findViewById(R.id.tvNumDisLike);
		lnLike=(LinearLayout)findViewById(R.id.lnLike);
		lnDisLike=(LinearLayout)findViewById(R.id.lnDisLike);
		ivLike=(ImageView)findViewById(R.id.ivLike);
		ivDisLike=(ImageView)findViewById(R.id.ivDisLike);
		
		tvTime.setText(DateTimeHelper.dateTimeToDateString(curThread.getTime()));
		tvContent.setText(curThread.getContent());
		tvAuthor.setText(curThread.getUserName());
		tvTag.setText(curThread.getTags());
		
		if(AppData.isLogin==true && curThread.getUserId()==AppData.saveUser.getId()) tvEdit.setVisibility(View.VISIBLE);
		else tvEdit.setVisibility(View.GONE);
		tvEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent in=new Intent(ThreadActivity.this, AddThreadActivity.class);
				in.putExtra("mode", 2);
				in.putExtra("thread", curThread);
				startActivityForResult(in, 111);
			}
		});
		tvDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Builder ad = new AlertDialog.Builder(ThreadActivity.this);
				ad.setMessage("Do you want to delete this thread?");
				ad.setCancelable(true);
				ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						wsDeleteThread();
					}
				});
				ad.setNegativeButton("No", null);
				ad.show();
			}
		});
		tvQuote.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!AppData.isLogin){
					startActivity(new Intent(ThreadActivity.this,LoginActivity.class));
				}
				else {
					Intent in=new Intent(ThreadActivity.this,AddReplyActivity.class);
					in.putExtra("thread_id", curThread.getId());
					in.putExtra("quote_content", curThread.getUserName()+" told: \" "+curThread.getContent()+" \"");
					startActivityForResult(in, 112);
				}
			}
		});
		
		loadLikeInfo();

		lnLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!AppData.isLogin){
					startActivity(new Intent(ThreadActivity.this,LoginActivity.class));
				}
				else {
					if(curThread.getIsLiked()==0){
						if(curThread.getIsDisliked()==1){
							Toast.makeText(ThreadActivity.this, "Can't like this while disliked!", Toast.LENGTH_SHORT).show();
						}
						else wsLikeDislike(WsUrl.URL_LIKE_THREAD, 0, curThread.getId());
					}
					else wsLikeDislike(WsUrl.URL_UNLIKE_THREAD, 1, curThread.getId());
				}
			}
		});
		lnDisLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!AppData.isLogin){
					startActivity(new Intent(ThreadActivity.this,LoginActivity.class));
				}
				else {
					if(curThread.getIsDisliked()==0){
						if(curThread.getIsLiked()==1){
							Toast.makeText(ThreadActivity.this, "Can't like this while disliked!", Toast.LENGTH_SHORT).show();
						}
						else wsLikeDislike(WsUrl.URL_DISLIKE_THREAD, 2, curThread.getId());
					}
					else wsLikeDislike(WsUrl.URL_UNDISLIKE_THREAD, 3, curThread.getId());
				}
			}
		});
		
		btn_reply.setVisibility(View.VISIBLE);
		btn_reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!AppData.isLogin){
					startActivity(new Intent(ThreadActivity.this,LoginActivity.class));
				}
				else {
					Intent in=new Intent(ThreadActivity.this,AddReplyActivity.class);
					in.putExtra("thread_id", curThread.getId());
					startActivityForResult(in, 112);
				}
			}
		});
		
		LinearLayout lnPictures = (LinearLayout) findViewById(R.id.lnPictures);
		TextView tvPictures = (TextView) findViewById(R.id.tvPictures);
		
		int num_pics=0;
		for(Character c:curThread.getPictures().toCharArray())
			if(c==';') num_pics++;
		if(curThread.getPictures().trim().length()>0) num_pics++;
		tvPictures.setText(num_pics+" pictures attached");
		if(num_pics>0) lnPictures.setVisibility(View.VISIBLE);
		else lnPictures.setVisibility(View.GONE);
		lnPictures.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ThreadActivity.this, AttachedPicturesActivity.class);
				intent.putExtra("pictures", curThread.getPictures());
				startActivity(intent);
			}
		});
		
		wsGetReplies();
	}
	
	private void loadLikeInfo(){
		tvNumLike.setText(curThread.getCountLiked()+"");
		tvNumDisLike.setText(curThread.getCountDisliked()+"");
		int clike,cunlike,cdislike,cundislike;
		clike=getResources().getColor(R.color.text_like);
		cunlike=getResources().getColor(R.color.text_unlike);
		cdislike=getResources().getColor(R.color.text_dislike);
		cundislike=getResources().getColor(R.color.text_undislike);
		if(curThread.getIsLiked()==1){
			ivLike.setImageDrawable(Utils.getDrawable(ThreadActivity.this, "like"));
			tvNumLike.setTextColor(clike);
		}
		else {
			ivLike.setImageDrawable(Utils.getDrawable(ThreadActivity.this, "unlike"));
			tvNumLike.setTextColor(cunlike);
		}
		if(curThread.getIsDisliked()==1){
			ivDisLike.setImageDrawable(Utils.getDrawable(ThreadActivity.this, "dislike"));
			tvNumDisLike.setTextColor(cdislike);
		}
		else{
			ivDisLike.setImageDrawable(Utils.getDrawable(ThreadActivity.this, "undislike"));
			tvNumDisLike.setTextColor(cundislike);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==111 && resultCode==RESULT_OK){
			curThread.setContent(data.getStringExtra("content"));
			tvContent.setText(curThread.getContent());
		}
		else if((requestCode==112 || requestCode==113) && resultCode==RESULT_OK){
			wsGetReplies();
		}
	}

	public void wsGetReplies() {
		if(!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else{
			int user_id=(AppData.isLogin?AppData.saveUser.getId():-1);
			(new jsGetReplies()).execute(new String[] { WsUrl.URL_GET_REPLIES, Integer.toString(curThread.getId()), Integer.toString(user_id) });
		}
	}

	public class jsGetReplies extends AsyncTask<String, Void, Integer> {
		
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
						t.setQuote(obj.getString(JsonTag.TAG_QUOTE));
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
								R.layout.list_item_thread, R.id.tvId,
								replies);
						lvReply.setAdapter(adapter);
						Utils.setListViewHeightBasedOnChildren(lvReply, adapter);
					}
				});
			} else {
				Toast.makeText(ThreadActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void wsDeleteThread() {
			if(!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else{
				(new jsDeleteThread())
					.execute(new String[] { WsUrl.URL_DELETE_THREAD,
							Integer.toString(curThread.getId())});
			}
	}

	public class jsDeleteThread extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(ThreadActivity.this);
			pd.setMessage("Deleting...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("thread_id", params[1]));
			
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
				Toast.makeText(ThreadActivity.this, "Your thread was deleted!", Toast.LENGTH_SHORT).show();
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(ThreadActivity.this, "Sorry! Deleted fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	//=======================================//
		public void wsLikeDislike(String url, int taskIndex, int thread_id) {
			(new jsLikeDislike()).execute(new String[] { url,
					Integer.toString(AppData.saveUser.getId()),
					Integer.toString(thread_id), Integer.toString(taskIndex) });
		}

		public class jsLikeDislike extends AsyncTask<String, Void, Integer> {
			ProgressDialog pd;
			int taskIndex;
			
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd=new ProgressDialog(ThreadActivity.this);
				pd.setMessage("Processing...");
				pd.setCancelable(false);
				pd.show();
			}
			
			@Override
			protected Integer doInBackground(String... params) {
				taskIndex=Integer.parseInt(params[3]);
				
				List<NameValuePair> par = new ArrayList<NameValuePair>();
				par.add(new BasicNameValuePair("user_id", params[1]));
				par.add(new BasicNameValuePair("thread_id", params[2]));
				
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
						curThread.setCountLiked(curThread.getCountLiked()+1);
						curThread.setIsLiked(1);
						break;
					case 1:
						curThread.setCountLiked(curThread.getCountLiked()-1);
						curThread.setIsLiked(0);
						break;
					case 2:
						curThread.setCountDisliked(curThread.getCountDisliked()+1);
						curThread.setIsDisliked(1);
						break;
					case 3:
						curThread.setCountDisliked(curThread.getCountDisliked()-1);
						curThread.setIsDisliked(0);
						break;
					}

					loadLikeInfo();
					setResult(RESULT_OK);
				}
			}
		}	
}
