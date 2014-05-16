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
import android.content.res.Resources.Theme;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.FolderAdapter;
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
	List<Folder> subFolders;
	
	ListView lvFolder, lvThread;
	int selectedIndexThread;
	ThreadAdapter threadAdapter;
	
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
		
		lvFolder = (ListView) findViewById(R.id.lvFolder);
		subFolders=AppData.getSubFolders(curFolder.getId());
		FolderAdapter adapter = new FolderAdapter(SubFolderActivity.this,R.layout.list_item_folder, R.id.tv_index, subFolders);
		lvFolder.setAdapter(adapter);
		Util.setListViewHeightBasedOnChildren(lvFolder, adapter);
		if(subFolders.size()<=0){
			((LinearLayout)findViewById(R.id.lnFolder)).setVisibility(View.GONE);
		}
		lvFolder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(SubFolderActivity.this, SubFolderActivity.class);
				AppData.folders.add(subFolders.get(arg2));
				startActivity(intent);
			}
		});
		
		TextView tvThreadsinFolder = (TextView) findViewById(R.id.tvThreadsinFolder);
		tvThreadsinFolder.setText(tvThreadsinFolder.getText().toString()+curFolder.getName());
		btn_new.setVisibility(View.VISIBLE);
		btn_new.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(!AppData.isLogin){
					startActivity(new Intent(SubFolderActivity.this,LoginActivity.class));
				}
				else {
					startActivity(new Intent(SubFolderActivity.this,AddThreadActivity.class));
				}
			}
		});
		
		wsGetThreads();
	}
	
	@Override
	public void finish() {
		super.finish();
		AppData.folders.remove(AppData.folders.size()-1);
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
						t.setUserName(obj.getString(JsonTag.TAG_USER_NAME));
						t.setStatus(Integer.parseInt(obj.getString(JsonTag.TAG_STATUS)));
						t.setNum_reply(Integer.parseInt(obj.getString(JsonTag.TAG_NUM_REPLY)));
						t.setNum_view(Integer.parseInt(obj.getString(JsonTag.TAG_NUM_VIEW)));
						
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
						threadAdapter = new ThreadAdapter(
								SubFolderActivity.this,
								R.layout.list_item_thread, R.id.tv_index,
								threads);
						lvThread.setAdapter(threadAdapter);
						Util.setListViewHeightBasedOnChildren(lvThread, threadAdapter);
						if(threads.size()<=0){
							((LinearLayout)findViewById(R.id.lnThread)).setVisibility(View.GONE);
						}
						lvThread.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								selectedIndexThread=arg2;
								wsViewThread();
							}
						});
					}
				});
			} else {
				Toast.makeText(SubFolderActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	public void wsViewThread() {
		if(!Util.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new jsViewThread()).execute(new String[] { WsUrl.URL_VIEW_THREAD, Integer.toString(threads.get(selectedIndexThread).getId()) });
	}

	public class jsViewThread extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(SubFolderActivity.this);
			pd.setMessage("Processing...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("thread_id", params[1]));
			
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
				threads.get(selectedIndexThread).setNum_view(threads.get(selectedIndexThread).getNum_view()+1);
				threadAdapter.notifyDataSetChanged();
			}
			
			Intent intent = new Intent(SubFolderActivity.this, ThreadActivity.class);
			intent.putExtra("thread", threads.get(selectedIndexThread));
			startActivity(intent);
		}
	}
}