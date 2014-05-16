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
import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;

public class AddThreadActivity extends FatherActivity {
	EditText txtContent, txtTitle;
	
	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_thread);
		initFather();
		
		tvTitle.setText("Post a new thread");
		tvSubTitle.setVisibility(View.GONE);
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wsAddThread();
			}
		});
		
		txtContent = (EditText) findViewById(R.id.txtContent);
		txtTitle = (EditText) findViewById(R.id.txtTitle);
		
		this.context = this;
	}

	public void wsAddThread() {
		String content = txtContent.getText().toString().trim();
		String title = txtTitle.getText().toString().trim();
		if (title.equals("")) {
			Toast.makeText(context, "Please fill title of this thread!", Toast.LENGTH_SHORT).show();
		}else if (content.equals("")) {
			Toast.makeText(context, "Please fill content of this thread!", Toast.LENGTH_SHORT).show();
		} else {
			if(!Util.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else{
				Folder f=AppData.folders.get(AppData.folders.size()-1);
				
				(new jsAddThread())
					.execute(new String[] { WsUrl.URL_ADD_THREAD,
							title,
							content,
							Integer.toString(f.getId()),
							Integer.toString(AppData.saveUser.getId())});
			}
		}
	}

	public class jsAddThread extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(AddThreadActivity.this);
			pd.setMessage("Posting your new thread...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("title", params[1]));
			par.add(new BasicNameValuePair("content", params[2]));
			par.add(new BasicNameValuePair("folder_id", params[3]));
			par.add(new BasicNameValuePair("user_id", params[4]));
			
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
				Toast.makeText(context, "Your thread was posted!", Toast.LENGTH_SHORT).show();
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Posted fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
