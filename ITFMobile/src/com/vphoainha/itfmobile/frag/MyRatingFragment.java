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
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;


public class MyRatingFragment extends Fragment {
	static View view;
	
	int rank, counterLiked, counterAnswer, counterQuestion;
	
	TextView tv_rank, tv_liked, tv_answer, tv_question;
	
	String msg;
	
	public static MyRatingFragment newInstance() {
		return new MyRatingFragment();
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
			view = inflater.inflate(R.layout.frag_myrating, container, false);
		} catch (InflateException e) {
		}
		
		tv_rank = (TextView) view.findViewById(R.id.tv_myexpert_rank);
		tv_liked = (TextView) view.findViewById(R.id.tv_myexpert_counteranswerliked);
		tv_answer = (TextView) view.findViewById(R.id.tv_myexpert_counterAnswer);
		tv_question= (TextView) view.findViewById(R.id.tv_myexpert_counterquestion);

		((MainActivity) getActivity()).changeMainTitleBarText("My Rating");

		counterAnswer = 0;
		counterQuestion = 0;
		accessGetMyExpertWebservice();
		return view;
	}
	
	public void accessGetMyExpertWebservice() {
		if(!Util.checkInternetConnection(getActivity()))
			Toast.makeText(getActivity(), getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else	
			(new JsonReadTask()).execute(new String[] { WsUrl.URL_GET_MYEXPERT, Integer.toString(AppData.saveUser.getId())});
	}

	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(getActivity());
			pd.setMessage("Loading your information...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("userId", params[1]));
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());
			
			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_MYEXPERT);

					// looping through All Products
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						rank = Integer.parseInt(obj.getString(JsonTag.TAG_RANK));
						counterAnswer = Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_ANSWER));
						counterQuestion = Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_QUESTION));
//						counterLiked = Integer.parseInt(obj.getString(JsonTag.TAG_COUNTER_LIKED));
					}
					return 1;
				} else {
					msg=json.getString("message");
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
			
			if (result==1) {
				tv_rank.setText("#" + rank);
				tv_liked.setText(""+ counterLiked);
				tv_answer.setText("" + counterAnswer);
				tv_question.setText("" + counterQuestion);
			} else {
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
