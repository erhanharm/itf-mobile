package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.ThreadAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;


public class SubFolderActivity extends FatherActivity {
	List<Thread> threads;
	String msg;
	
	Folder curFolder;
	ListView lvFolder, lvThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subfolder);
		initFather();

		String subtitle="";
		for(int i=0;i<AppData.folders.size()-1;i++)
			subtitle+=" > "+AppData.folders.get(i).getName();
		
		curFolder=AppData.folders.get(AppData.folders.size()-1);
		tvTitle.setText(curFolder.getName());
		tvSubTitle.setText("ITF"+subtitle);
		
		wsGetThreads();
	}

	public void wsGetThreads() {
		if(!Util.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new jsGetThreads()).execute(new String[] { WsUrl.URL_GET_THREADS, Integer.toString(curFolder.getId()) });
	}

	public class jsGetThreads extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(SubFolderActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("folder_id", params[1]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					threads = new ArrayList<Thread>();
					JSONArray array = json.getJSONArray(JsonTag.TAG_THREADS);

					// looping through All Products
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Thread t = new Thread();
						t.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						t.setTitle(obj.getString(JsonTag.TAG_TITLE));
						t.setContent(obj.getString(JsonTag.TAG_CONTENT));
						t.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));
						t.setFolderId(Integer.parseInt(obj.getString(JsonTag.TAG_FOLDER_ID)));
						t.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
						t.setStatus(Integer.parseInt(obj.getString(JsonTag.TAG_STATUS)));
						
						threads.add(t);
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
						lvThread = (ListView) findViewById(R.id.lvThread);
						ThreadAdapter adapter = new ThreadAdapter(
								SubFolderActivity.this,
								R.layout.list_item_thread, R.id.tv_index,
								threads);
						lvThread.setAdapter(adapter);
					}
				});
			} else {
				Toast.makeText(SubFolderActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
