package com.vphoainha.itfmobile.frag;

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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;

public class MyAnswersFragment extends Fragment {
	static View view;	
	
	private List<Thread> listData;
	String msg;

	int from;
	boolean isMaximum;
	
	public static MyAnswersFragment newInstance() {
		return new MyAnswersFragment();
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
			view = inflater.inflate(R.layout.frag_home, container, false);
		} catch (InflateException e) {
		}

		((MainActivity) getActivity()).changeMainTitleBarText("My Answers");

		accessWebserviceReset();
		return view;
	}
	
	public void accessWebserviceReset() {
		listData = new ArrayList<Thread>();
		isMaximum = false;
		from = 1;
		
		accessWebservice();
	}
	
	public boolean isMaximum() {
		return isMaximum;
	}
	
	public void accessWebservice() {
		if(!Util.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new JsonReadTask()).execute(new String[] {
				WsUrl.URL_GET_MYANSWERS,
				Integer.toString(AppData.saveUser.getId()), Integer.toString(from),
				Integer.toString(from = +10) });
	}

	private class JsonReadTask extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(getActivity());
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("forUserId", params[1]));
			par.add(new BasicNameValuePair("from", params[2]));
			par.add(new BasicNameValuePair("to", params[3]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_THREADS);
					if(array.length()== 0){
						isMaximum = true;
					}else{
						from++;
					}
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Thread question = new Thread();
//						question.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
//						question.setForUserId(Integer.parseInt(obj.getString(JsonTag.TAG_FOR_USER_Id)));
//						question.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
//						question.setUserName(obj.getString(JsonTag.TAG_USER_NAME));
//						try{
//							question.setForUserName(obj.getString(JsonTag.TAG_FOR_USER_NAME));
//						}catch(JSONException ex){
//							question.setForUserName("");
//						}
//						question.setContent(obj.getString(JsonTag.TAG_CONTENT));
//						question.setCategoryId(Integer.parseInt(obj.getString(JsonTag.TAG_CATEGORY_ID)));
//						question.setCategoryName(obj.getString(JsonTag.TAG_CATEGORY_NAME));
//						question.setIsAnswered(Integer.parseInt(obj.getString(JsonTag.TAG_IS_ANSWERED)));
//						question.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));
//						question.setSaveUserId(Integer.parseInt(obj.getString(JsonTag.TAG_SAVEUSERID)));
						
						listData.add(question);
					}
					return 1;
				} else {
					msg=json.getString("message");
					isMaximum = true;
					return 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(pd!=null && pd.isShowing())  pd.dismiss();

			if (result ==1) {
				((MainActivity) getActivity()).runOnUiThread(new Runnable() {
					public void run() {
//						ListView list = (ListView)view.findViewById(R.id.lv_ques);
//						ThreadAdapter adapter = new ThreadAdapter(getActivity(),R.layout.list_item_question, R.id.tvId, listData, ThreadAdapter.MY_ANSWER);
//						list.setAdapter(adapter);
					}
				});
			} else {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}

