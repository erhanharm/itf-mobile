package com.vphoainha.itfmobile.adapter;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.DateTimeHelper;

public class ThreadAdapter extends ArrayAdapter<Thread> {
	
	Activity act;
	List<Thread> lst;
	String msg;
	
	Thread save_q;
	int save_pos;
	
	public ThreadAdapter(Activity context, int resource, int textViewResourceId, List<Thread> objects) {
		super(context, resource, textViewResourceId, objects);
		act=context;
		lst=objects;
	}
	
	public static class ViewHolder {
		public TextView tvId,tvCategory,tvDate, tvTitle, tvAuthor, tvAnswered, tvForUser;
		public Button btnAnswer, btnDeny, btnSolved;
		public LinearLayout lnRow;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = act.getLayoutInflater();
			convertView = inflater.inflate(R.layout.list_item_thread, null);
	      
			holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
			holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
			holder.btnAnswer=(Button)convertView.findViewById(R.id.btnAnswer);
			holder.lnRow=(LinearLayout)convertView.findViewById(R.id.lnRow);
			convertView.setTag(holder);
	    }
		holder = (ViewHolder) convertView.getTag();

		Thread q = lst.get(position);

		holder.tvId.setText("#" + q.getId());
		holder.tvDate.setText(DateTimeHelper.dateTimeToDateStringDMY(q.getTime()));
		holder.tvTitle.setText(q.getContent());
		
		holder.tvAuthor.setText("by "+q.getUserName());
		

		return convertView;
	}

//	
//	//=======================================//
//	public void accessSolvedWebservice(int ques_id) {
//		(new JsonReadTaskSolved()).execute(new String[] { WsUrl.URL_set_Question_Is_Answered,
//				Integer.toString(Utils.saveUser.getId()),
//				Integer.toString(ques_id) });
//	}
//
//	public class JsonReadTaskSolved extends AsyncTask<String, Void, Integer> {
//		ProgressDialog pd;
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			pd=new ProgressDialog(act);
//			pd.setMessage("Processing...");
//			pd.setCancelable(false);
//			pd.show();
//		}
//		
//		@Override
//		protected Integer doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			par.add(new BasicNameValuePair("userId", params[1]));
//			par.add(new BasicNameValuePair("questionId", params[2]));
//			
//			JSONParser jsonParser = new JSONParser();
//			JSONObject json = jsonParser
//					.makeHttpRequest(params[0], "POST", par);
//			Log.d("Create Response", json.toString());
//			try {
//				int success = json.getInt(JsonTag.TAG_SUCCESS);
//				msg=json.getString(JsonTag.TAG_MESSAGE);
//				if (success == 1) {
//					return 1;
//				} else {
//					return 0;
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			return 0;
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			if(pd!=null && pd.isShowing())  pd.dismiss();
//			
//			Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
//			if(result==1){
//				save_q.setIsAnswered(1);
//				lst.set(save_pos, save_q);
//				notifyDataSetChanged();
//			}
//		}
//	}
//	
//	//=======================================//
//		public void accessDenyWebservice(int forUserId, int ques_id) {
//			(new JsonReadTaskDeny()).execute(new String[] { WsUrl.URL_DENY_QUESTION,
//					Integer.toString(forUserId),
//					Integer.toString(ques_id) });
//		}
//
//		public class JsonReadTaskDeny extends AsyncTask<String, Void, Integer> {
//			ProgressDialog pd;
//			
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//				pd=new ProgressDialog(act);
//				pd.setMessage("Processing...");
//				pd.setCancelable(false);
//				pd.show();
//			}
//			
//			@Override
//			protected Integer doInBackground(String... params) {
//				List<NameValuePair> par = new ArrayList<NameValuePair>();
//				par.add(new BasicNameValuePair("userId", params[1]));
//				par.add(new BasicNameValuePair("questionId", params[2]));
//				
//				JSONParser jsonParser = new JSONParser();
//				JSONObject json = jsonParser
//						.makeHttpRequest(params[0], "POST", par);
//				Log.d("Create Response", json.toString());
//				try {
//					int success = json.getInt(JsonTag.TAG_SUCCESS);
//					msg=json.getString(JsonTag.TAG_MESSAGE);
//					if (success == 1) {
//						return 1;
//					} else {
//						return 0;
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//
//				return 0;
//			}
//
//			@Override
//			protected void onPostExecute(Integer result) {
//				if(pd!=null && pd.isShowing())  pd.dismiss();
//				
//				Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
//				if(result==1){
//					lst.remove(save_pos);
//					notifyDataSetChanged();
//				}
//			}
//		}
}
