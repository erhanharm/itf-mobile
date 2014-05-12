//package com.vphoainha.itfmobile.adapter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.vphoainha.itfmobile.AddAnswerActivity;
//import com.vphoainha.itfmobile.LoginActivity;
//import com.vphoainha.itfmobile.MainActivity;
//import com.vphoainha.itfmobile.QuestionActivity;
//import com.vphoainha.itfmobile.R;
//import com.vphoainha.itfmobile.frag.HomeFragment;
//import com.vphoainha.itfmobile.frag.MyAnswersFragment;
//import com.vphoainha.itfmobile.frag.MyQuestionsFragment;
//import com.vphoainha.itfmobile.jsonparser.JSONParser;
//import com.vphoainha.itfmobile.model.Thread;
//import com.vphoainha.itfmobile.util.DateTimeHelper;
//import com.vphoainha.itfmobile.util.JsonTag;
//import com.vphoainha.itfmobile.util.Utils;
//import com.vphoainha.itfmobile.util.WsUrl;
//
//public class ThreadAdapter extends ArrayAdapter<Thread> {
//	public static final int ALL_QUESTION=1;
//	public static final int MY_QUESTION=2;
//	public static final int MY_ANSWER=3;
//	
//	Activity act;
//	List<Thread> lst;
//	int type;
//	String msg;
//	
//	Thread save_q;
//	int save_pos;
//	
//	public ThreadAdapter(Activity context, int resource, int textViewResourceId, List<Thread> objects, int type) {
//		super(context, resource, textViewResourceId, objects);
//		act=context;
//		this.type=type;
//		lst=objects;
//	}
//	
//	public static class ViewHolder {
//		public TextView tvId,tvCategory,tvDate, tvTitle, tvAuthor, tvAnswered, tvForUser;
//		public Button btnAnswer, btnDeny, btnSolved;
//		public LinearLayout lnRow;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//
//		ViewHolder holder = null;
//		if (convertView == null) {
//			holder = new ViewHolder();
//			LayoutInflater inflater = act.getLayoutInflater();
//			convertView = inflater.inflate(R.layout.list_item_question, null);
//	      
//			holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
//			holder.tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);
//			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
//			holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
//			holder.tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
//			holder.tvAnswered = (TextView) convertView.findViewById(R.id.tvAnswered);
//			holder.tvForUser = (TextView) convertView.findViewById(R.id.tvForUser);
//			holder.btnAnswer=(Button)convertView.findViewById(R.id.btnAnswer);
//			holder.btnDeny=(Button)convertView.findViewById(R.id.btnDeny);
//			holder.btnSolved=(Button)convertView.findViewById(R.id.btnSolved);
//			holder.lnRow=(LinearLayout)convertView.findViewById(R.id.lnRow);
//			convertView.setTag(holder);
//	    }
//		holder = (ViewHolder) convertView.getTag();
//
//		Thread q = lst.get(position);
//
//		holder.tvId.setText("#" + q.getId());
//		if(q.getCategoryId()==Utils.DEFAUT_VALUE) holder.tvCategory.setText("All");
//		else holder.tvCategory.setText(q.getCategoryName());
//		holder.tvDate.setText(DateTimeHelper.dateTimeToDateStringDMY(q.getTime()));
//		holder.tvTitle.setText(q.getContent());
//		
//		if(q.getUserId()==Utils.DEFAUT_VALUE) holder.tvAuthor.setText("by Anonymous");
//		else holder.tvAuthor.setText("by "+q.getUserName());
//		if(q.getForUserId()==-1) holder.tvForUser.setText("for Community");
//		else holder.tvForUser.setText("for "+q.getForUserName());
//		
//		holder.tvAnswered.setVisibility(View.GONE);
//		holder.btnAnswer.setVisibility(View.GONE);
//		holder.btnSolved.setVisibility(View.GONE);
//		holder.btnDeny.setVisibility(View.GONE);
//		holder.tvForUser.setVisibility(View.VISIBLE);
//		holder.tvAuthor.setVisibility(View.VISIBLE);
//		
//		switch(type){
//		case ALL_QUESTION:
//			holder.tvForUser.setVisibility(View.GONE);
//			break; 
//		case MY_QUESTION:
//			holder.tvAuthor.setVisibility(View.GONE);
//			break; 
//		case MY_ANSWER:
//			holder.tvForUser.setVisibility(View.GONE);
//		}
//		
//		if(q.getIsAnswered()==1)
//			holder.tvAnswered.setVisibility(View.VISIBLE);
//		else{
//			holder.btnAnswer.setVisibility(View.VISIBLE);
//			switch(type){
//			case ALL_QUESTION:
//				break; 
//			case MY_QUESTION:
//				holder.btnSolved.setVisibility(View.VISIBLE);
//				break; 
//			case MY_ANSWER:
//				holder.btnDeny.setVisibility(View.VISIBLE);
//			}
//			
//			holder.btnAnswer.setTag(q);
//			holder.btnAnswer.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if(!Utils.isLogin){
//						act.startActivity(new Intent(act,LoginActivity.class));
//					}
//					else {
//						Intent intent = new Intent(act, AddAnswerActivity.class);
//						intent.putExtra("question", (Thread)v.getTag());
//						act.startActivityForResult(intent, QuestionActivity.PICK_CONTACT_REQUEST);
//					}
//				}
//			});
//			
//			holder.btnSolved.setTag(R.string.TAG_QUESTION, q);
//			holder.btnSolved.setTag(R.string.TAG_POS, position);
//			holder.btnSolved.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					save_q=(Thread)v.getTag(R.string.TAG_QUESTION);
//					save_pos=(Integer)v.getTag(R.string.TAG_POS);
//					accessSolvedWebservice(save_q.getId());
//				}
//			});
//			
//			holder.btnDeny.setTag(R.string.TAG_QUESTION, q);
//			holder.btnDeny.setTag(R.string.TAG_POS, position);
//			holder.btnDeny.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					save_q=(Thread)v.getTag(R.string.TAG_QUESTION);
//					save_pos=(Integer)v.getTag(R.string.TAG_POS);
//					accessDenyWebservice(save_q.getForUserId(), save_q.getId());
//				}
//			});
//		}
//		
//		holder.lnRow.setTag(q);
//		holder.lnRow.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(act,QuestionActivity.class);
//				intent.putExtra("question", (Thread)v.getTag());
//				act.startActivity(intent);
//			}
//		});
//
//		MainActivity main=((MainActivity)act);
//		if(main.getCur_frag()==0){
//			HomeFragment frag=main.getHomeFragment();
//			if (position >= (lst.size() - 1) && !frag.isMaximum()) {
//				frag.accessWebservice();
//			}
//		}
//		if(main.getCur_frag()==1){
//			MyQuestionsFragment frag=main.getMyQuestionsFragment();
//			if (position >= (lst.size() - 1) && !frag.isMaximum()) {
//				frag.accessWebservice();
//			}
//		}
//		if(main.getCur_frag()==2){
//			MyAnswersFragment frag=main.getMyAnswersFragment();
//			if (position >= (lst.size() - 1) && !frag.isMaximum()) {
//				frag.accessWebservice();
//			}
//		}
//
//		return convertView;
//	}
//
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
//}
