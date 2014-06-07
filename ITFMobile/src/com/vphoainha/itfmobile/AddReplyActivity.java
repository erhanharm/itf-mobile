package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Reply;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class AddReplyActivity extends FatherActivity {
	EditText txtContent;
	
	Context context;
	private int mode=1;
	private int thread_id;
	private Reply curReply;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_reply);
		initFather();
		
		txtContent = (EditText) findViewById(R.id.txtContent);
		
		mode=getIntent().getIntExtra("mode", 1);
		if(mode==1){
			thread_id=getIntent().getIntExtra("thread_id", -1);
			tvTitle.setText("Post a new reply");
			if(getIntent().getStringExtra("quote_content")!=null)
				txtContent.setText(getIntent().getStringExtra("quote_content")+"\n\n");
		}
		else{
			curReply=(Reply)getIntent().getSerializableExtra("reply");
			tvTitle.setText("Edit reply");
			txtContent.setText(curReply.getContent());
		}
		
		tvSubTitle.setVisibility(View.GONE);
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mode==1)	wsAddReply();
				else wsEditReply();
			}
		});
		
		this.context = this;
	}

	public void wsAddReply() {
		String content = txtContent.getText().toString().trim();
		if (content.equals("")) {
			Toast.makeText(context, "Please fill content of this reply!", Toast.LENGTH_SHORT).show();
		} else {
			if(!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else{
				(new jsAddReply())
					.execute(new String[] { WsUrl.URL_ADD_REPLY,
							content,
							Integer.toString(thread_id),
							Integer.toString(AppData.saveUser.getId())});
			}
		}
	}

	public class jsAddReply extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(AddReplyActivity.this);
			pd.setMessage("Posting...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("content", params[1]));
			par.add(new BasicNameValuePair("thread_id", params[2]));
			par.add(new BasicNameValuePair("user_id", params[3]));
			
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
				Toast.makeText(context, "Your reply was posted!", Toast.LENGTH_SHORT).show();
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Posted fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void wsEditReply() {
		String content = txtContent.getText().toString().trim();
		if (content.equals("")) {
			Toast.makeText(context, "Please fill content of this reply!", Toast.LENGTH_SHORT).show();
		} else {
			if(!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else{
				(new jsEditReply())
					.execute(new String[] { WsUrl.URL_EDIT_REPLY,
							content,
							Integer.toString(curReply.getId())});
			}
		}
	}

	public class jsEditReply extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(AddReplyActivity.this);
			pd.setMessage("Saving...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("content", params[1]));
			par.add(new BasicNameValuePair("reply_id", params[2]));
			
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
				Toast.makeText(context, "Your reply was saved!", Toast.LENGTH_SHORT).show();
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Saved fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
