package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.ExpertAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Expert;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class TopExpertActivity extends Activity {
	List<Expert> listData;

	String msg;
	
	boolean choose_expert=false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_expert);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
		ln_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		choose_expert=getIntent().getBooleanExtra("choose_expert", false);
		if(!choose_expert) tv_title.setText("Top Expert");
		else tv_title.setText("Choose an Expert");
		
		accessWebservice();
	}

	public void accessWebservice() {
		if(!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new JsonReadTask()).execute(new String[] { WsUrl.URL_GET_TOP_EXPERT, "10" });
	}

	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(TopExpertActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("nTop", params[1]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					listData = new ArrayList<Expert>();
					JSONArray array = json.getJSONArray(JsonTag.TAG_TOP_EXPERT);

					// looping through All Products
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Expert expert = new Expert();
						expert.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						expert.setIndex(i + 1);
						expert.setUserName(obj.getString(JsonTag.TAG_NAME));
						expert.setUserEmail(obj.getString(JsonTag.TAG_EMAIL));
						expert.setCounterLiked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_LIKED)));
						expert.setCounterDisLiked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_DISLIKED)));
						expert.setCounterAsked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_ASKED)));
						listData.add(expert);
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
						ListView list = (ListView) findViewById(R.id.lv);
						if(choose_expert){
							list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
									Intent intent = new Intent();
									intent.putExtra("forUserId", listData.get(position).getId());
									intent.putExtra("forUserName", listData.get(position).getUserName());
									setResult(Activity.RESULT_OK, intent);
									finish();
								}
							});
						}
						ExpertAdapter adapter = new ExpertAdapter(
								TopExpertActivity.this,
								R.layout.list_item_expert, R.id.tv_index,
								listData);
						list.setAdapter(adapter);
					}
				});
			} else {
				Toast.makeText(TopExpertActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
