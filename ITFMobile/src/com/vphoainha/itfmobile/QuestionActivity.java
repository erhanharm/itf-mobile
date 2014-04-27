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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.AnswerAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Answer;
import com.vphoainha.itfmobile.model.Question;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class QuestionActivity extends Activity {
	public static final int PICK_CONTACT_REQUEST = 0;
	
	private List<Answer> lstAnswer;
	
	Button btnAnswer;
	TextView tvAnswered, tv_index, tv_category, tv_time, tv_content, tv_username, tvForUser;

	Question question;
	String msg;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_question);
		
		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
		ln_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		btnAnswer = (Button) findViewById(R.id.btnAnswer);
		tvAnswered = (TextView) findViewById(R.id.tvAnswered);
		tv_index = (TextView) findViewById(R.id.tv_index);
		tv_category = (TextView) findViewById(R.id.tv_category);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_username = (TextView) findViewById(R.id.tv_username);
		tvForUser = (TextView) findViewById(R.id.tvForUser);
		
		Intent bundle = getIntent();
		question=(Question)bundle.getSerializableExtra("question");
		
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("Question #"+question.getId());
		
		tv_index.setText("#"+question.getId());
		tv_category.setText(question.getCategoryId()==Utils.DEFAUT_VALUE?"All":question.getCategoryName());
		tv_time.setText( DateTimeHelper.dateTimeToDateStringDMY(question.getTime()));
		tv_content.setText(question.getContent());
		
		if(question.getUserId()==Utils.DEFAUT_VALUE) tv_username.setText("by Anonymous");
		else tv_username.setText("by "+question.getUserName());
		if(question.getForUserId()==Utils.DEFAUT_VALUE) tvForUser.setText("for Community");
		else tvForUser.setText("for "+question.getForUserName());
		
		if(question.getIsAnswered()==1){
			tvAnswered.setVisibility(View.VISIBLE);
			btnAnswer.setVisibility(View.GONE);
		}
		else{
			tvAnswered.setVisibility(View.GONE);
			btnAnswer.setVisibility(View.VISIBLE);
		}
		
		accessWebservice();
	}

	public void onClickAnswer(View v) {
		if(!Utils.isLogin){
			startActivity(new Intent(getApplicationContext(),LoginActivity.class));
		}
		else {
			Intent intent = new Intent(getApplicationContext(), AddAnswerActivity.class);
			intent.putExtra("question", question);
			startActivityForResult(intent, PICK_CONTACT_REQUEST);
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		Toast.makeText(context, RESULT_OK + "result code: " + resultCode,Toast.LENGTH_SHORT).show();
		
//		if (requestCode == PICK_CONTACT_REQUEST) {
			if (resultCode ==  RESULT_OK) {
				accessWebservice();
//				Toast.makeText(context, "forUserId: " + requestCode,
//						Toast.LENGTH_SHORT).show();
				
			}
//		}
	}

	public void accessWebservice() {
		if(!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else{
			int userId=Utils.isLogin?Utils.saveUser.getId():-1;
			
			(new JsonReadTask()).execute(new String[] { WsUrl.URL_GET_ANSWER,
				Integer.toString(question.getId()), Integer.toString(userId) });
		}
	}

	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(QuestionActivity.this);
			pd.setMessage("Loading answers...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("questionId", params[1]));
			par.add(new BasicNameValuePair("userId", params[2]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response answers", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					lstAnswer = new ArrayList<Answer>();
					JSONArray array = json.getJSONArray(JsonTag.TAG_ANSWER);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Answer answer = new Answer();
						answer.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						answer.setQuestionId(Integer.parseInt(obj.getString(JsonTag.TAG_QUESTION_ID)));
						answer.setContent(obj.getString(JsonTag.TAG_CONTENT));
						answer.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
						answer.setUserName(obj.getString(JsonTag.TAG_USER_NAME));
						answer.setCountLiked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_LIKED)));
						answer.setCountDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_COUNT_DISLIKED)));
						answer.setIsLiked(Integer.parseInt(obj.getString(JsonTag.TAG_ISLIKED)));
						answer.setIsDisliked(Integer.parseInt(obj.getString(JsonTag.TAG_ISDISLIKED)));
						answer.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));

						lstAnswer.add(answer);
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
			
			if (result ==1) {
				runOnUiThread(new Runnable() {
					public void run() {
						for(int i=0;i<lstAnswer.size()/2;i++){
							Answer a=lstAnswer.get(i);
							lstAnswer.set(i, lstAnswer.get(lstAnswer.size()-i-1));
							lstAnswer.set(lstAnswer.size()-i-1, a);
						}
						
						ListView list = (ListView) findViewById(R.id.lv);
						AnswerAdapter adapter = new AnswerAdapter(QuestionActivity.this,R.layout.list_item_answer, -1,lstAnswer, AnswerAdapter.ALL_ANSWER);
						list.setAdapter(((ListAdapter) adapter));
					}
				});
			} else {
				Toast.makeText(QuestionActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		}
	}
}
