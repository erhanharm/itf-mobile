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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.SubFolderActivity;
import com.vphoainha.itfmobile.ThreadActivity;
import com.vphoainha.itfmobile.adapter.HomeFolderAdapter;
import com.vphoainha.itfmobile.adapter.HomeFolderAdapter.IndexPath;
import com.vphoainha.itfmobile.adapter.HomeFolderAdapter.SectionListAdapterAdapterDelegate;
import com.vphoainha.itfmobile.adapter.ThreadAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class HomeFragment extends Fragment implements SectionListAdapterAdapterDelegate {
	static View view;

	private List<Folder> lstFolder1;
	private List<List<Folder>> lstFolder2;
	private List<Thread> latestThreads;

	HomeFolderAdapter folderAdapter;
	ThreadAdapter threadAdapter;
	LayoutInflater inflater;

	ListView lvHome, lvLatestThreads;

	String msg;
	int selectedIndexThread;

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater linflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = linflater.inflate(R.layout.frag_home, container, false);
		} catch (InflateException e) {
		}

		((MainActivity) getActivity()).changeMainTitleBarText(getString(R.string.app_name));

		lvHome = (ListView) view.findViewById(R.id.lvHome);
		lvLatestThreads = (ListView) view.findViewById(R.id.lvLatestThreads);
		inflater = LayoutInflater.from(getActivity());
		
		refreshHome();
		
		return view;
	}
	
	public void refreshHome(){
		lstFolder1 = new ArrayList<Folder>();
		lstFolder2 = new ArrayList<List<Folder>>();

		folderAdapter = new HomeFolderAdapter();
		folderAdapter.delegate = HomeFragment.this;
		
		lvHome.setOnItemClickListener(folderAdapter.itemClickListener);
		lvHome.setAdapter(folderAdapter);

		wsGetFolders();

		//getLatestThreads
		latestThreads = new ArrayList<Thread>();
		threadAdapter = new ThreadAdapter(getActivity(), R.layout.list_item_thread, R.id.tvId, latestThreads);
		lvLatestThreads.setAdapter(threadAdapter);
		lvLatestThreads.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedIndexThread=arg2;
				wsViewThread();
			}
		});
		wsGetLatestThreads();
		
		Utils.setListViewHeightBasedOnChildren(lvHome, folderAdapter);
		Utils.setListViewHeightBasedOnChildren(lvLatestThreads, threadAdapter);
	}

	@Override
	public int sectionCount() {
		try {
			if (lstFolder1 != null) {
				return lstFolder1.size();
			}
		} catch (Exception e) {
		}

		return 0;
	}

	@Override
	public int rowsInSection(int section) {
		try {
			if (lstFolder2 != null) {
				return lstFolder2.get(section).size();
			}
		} catch (Exception e) {
		}
		return 0;
	}

	@Override
	public View viewForRowAtIndexPath(IndexPath path) {
		View view = inflater.inflate(R.layout.row_home_group2, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		TextView tvNote = (TextView) view.findViewById(R.id.tvNote);

		Folder f = lstFolder2.get(path.section).get(path.row);
		tvTitle.setText(f.getName());

		if (f.getNote() != null && !f.getNote().equals("")) {
			tvNote.setText(f.getNote());
			tvNote.setVisibility(View.VISIBLE);
		} else
			tvNote.setVisibility(View.GONE);
		return view;
	}

	@Override
	public View viewForHeaderInSection(int section) {
		View view = inflater.inflate(R.layout.row_home_group1, null);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		TextView tvNote = (TextView) view.findViewById(R.id.tvNote);

		tvTitle.setText(lstFolder1.get(section).getName());
		tvNote.setText(lstFolder1.get(section).getNote());

		if (lstFolder1.get(section).getNote() != null && !lstFolder1.get(section).getNote().equals("")) {
			tvNote.setText(lstFolder1.get(section).getNote());
			tvNote.setVisibility(View.VISIBLE);
		} else
			tvNote.setVisibility(View.GONE);
		return view;
	}

	@Override
	public void itemSelectedAtIndexPath(IndexPath path) {
		AppData.folders = new ArrayList<Folder>();

		Intent intent = new Intent(getActivity(), SubFolderActivity.class);
		Folder f = lstFolder2.get(path.section).get(path.row);
		for (Folder fParrent : lstFolder1)
			if (f.getParrentId() == fParrent.getId()) {
				AppData.folders.add(fParrent);
				AppData.folders.add(f);
				break;
			}
		startActivity(intent);
	}

	@Override
	public void itemSelectedAtSection(int section) {
		AppData.folders = new ArrayList<Folder>();

		Intent intent = new Intent(getActivity(), SubFolderActivity.class);
		AppData.folders.add(lstFolder1.get(section));
		startActivity(intent);
	}

	// ============================================//

	public void wsGetFolders() {
		if (!Utils.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else
			(new jsGetFolders()).execute(new String[] { WsUrl.URL_GET_FOLDERS });
	}

	private class jsGetFolders extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();

			AppData.allFolders = new ArrayList<Folder>();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_FOLDERS);

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Folder f = new Folder();
						f.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						f.setName(obj.getString(JsonTag.TAG_NAME));
						f.setNote(obj.getString(JsonTag.TAG_NOTE));
						f.setParrentId(Integer.parseInt(obj.getString(JsonTag.TAG_PARRENT_ID)));
						f.setFolderIndex(Integer.parseInt(obj.getString(JsonTag.TAG_FOLDER_INDEX)));
						f.setNum_thread(Integer.parseInt(obj.getString(JsonTag.TAG_NUM_THREAD)));

						AppData.allFolders.add(f);
					}
					return 1;
				} else {
					msg = json.getString("message");
					return 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (result == 1) {
				lstFolder1.clear();
				lstFolder2.clear();

				for (Folder f : AppData.allFolders)
					if (f.getParrentId() == -1)
						lstFolder1.add(f);

				List<Folder> lst;
				for (Folder f : lstFolder1) {
					lst = new ArrayList<Folder>();
					for (Folder f2 : AppData.allFolders)
						if (f2.getParrentId() == f.getId())
							lst.add(f2);
					lstFolder2.add(lst);
				}

				folderAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(lvHome, folderAdapter);
			} else {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void wsGetLatestThreads() {
		if (!Utils.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else {
			int user_id = (AppData.isLogin ? AppData.saveUser.getId() : -1);
			(new jsGetLatestThreads()).execute(new String[] { WsUrl.URL_GET_LATEST_THREADS, user_id+""});
		}
	}

	private class jsGetLatestThreads extends AsyncTask<String, Void, Integer> {

		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(getActivity());
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("ntop", "5"));
			par.add(new BasicNameValuePair("user_id", params[1]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
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
						t.setTags(obj.getString(JsonTag.TAG_TAGS));
						t.setPictures(obj.getString(JsonTag.TAG_PICTURES));

						latestThreads.add(t);
					}
					msg = json.getString("message");
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
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (result == 1) {
				threadAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(lvLatestThreads, threadAdapter);
			} else {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	private void wsViewThread() {
		if(!Utils.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new jsViewThread()).execute(new String[] { WsUrl.URL_VIEW_THREAD, Integer.toString(latestThreads.get(selectedIndexThread).getId()) });
	}

	private class jsViewThread extends AsyncTask<String, Void, Integer> {
		
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
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
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
				latestThreads.get(selectedIndexThread).setNum_view(latestThreads.get(selectedIndexThread).getNum_view()+1);
				threadAdapter.notifyDataSetChanged();
			}
			
			Intent intent = new Intent(getActivity(), ThreadActivity.class);
			intent.putExtra("thread", latestThreads.get(selectedIndexThread));
			startActivityForResult(intent, 112);
		}
	}

}
