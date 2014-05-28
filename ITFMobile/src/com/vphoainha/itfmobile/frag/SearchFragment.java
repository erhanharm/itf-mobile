package com.vphoainha.itfmobile.frag;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.ThreadActivity;
import com.vphoainha.itfmobile.adapter.ThreadAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;

public class SearchFragment extends Fragment {
	static View view;	
	
	private List<Thread> threads;
	
	private ListView lvThread;
	private EditText txtSearch;
	private ImageButton btnSearch;
	
	private ThreadAdapter threadAdapter;
	private String searchContent="";
	private int selectedIndexThread;
	String msg;

	public static SearchFragment newInstance(String searchContent) {
		SearchFragment searchFragment=new SearchFragment();
		searchFragment.searchContent=searchContent;
		return searchFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.frag_search, container, false);
		} catch (InflateException e) {
		}

		((MainActivity) getActivity()).changeMainTitleBarText("Search Forum");
		lvThread=(ListView)view.findViewById(R.id.lvThread);
		
		txtSearch = (EditText) view.findViewById(R.id.txtSearch);
		btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);
		txtSearch.setText(searchContent);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchContent=txtSearch.getText().toString();
				if(searchContent.equals("")) {
					Toast.makeText(getActivity(), "Please fill search content!", Toast.LENGTH_SHORT).show();
					return;
				}
				wsSearchThread();
			}
		});
		
		wsSearchThread();
		
		return view;
	}
	
	public void wsSearchThread() {
		if(!Util.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else{
			int user_id=(AppData.isLogin?AppData.saveUser.getId():-1);
			(new jsSearchThread()).execute(new String[] { WsUrl.URL_SEARCH_THREAD, 
					searchContent,
					Integer.toString(user_id)});
		}
	}

	public class jsSearchThread extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(getActivity());
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
			
			threads = new ArrayList<Thread>();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("content", params[1]));
			par.add(new BasicNameValuePair("user_id", params[2]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
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
						t.setCountLiked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_LIKED)));
						t.setCountDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_DISLIKED)));
						t.setIsLiked(Integer.parseInt(obj.getString(JsonTag.TAG_ISLIKED)));
						t.setIsDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_ISDISLIKED)));
						
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
			
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						threadAdapter = new ThreadAdapter(
								getActivity(),
								R.layout.list_item_thread, R.id.tv_index,
								threads);
						lvThread.setAdapter(threadAdapter);
						Util.setListViewHeightBasedOnChildren(lvThread, threadAdapter);
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
		}
	}
	
	public void wsViewThread() {
		if(!Util.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new jsViewThread()).execute(new String[] { WsUrl.URL_VIEW_THREAD, Integer.toString(threads.get(selectedIndexThread).getId()) });
	}

	public class jsViewThread extends AsyncTask<String, Void, Integer> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(getActivity());
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
			
			Intent intent = new Intent(getActivity(), ThreadActivity.class);
			intent.putExtra("thread", threads.get(selectedIndexThread));
			startActivityForResult(intent, 112);
		}
	}
}

