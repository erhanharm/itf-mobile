//package com.vphoainha.itfmobile;
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
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.vphoainha.itfmobile.jsonparser.JSONParser;
//import com.vphoainha.itfmobile.model.Thread;
//import com.vphoainha.itfmobile.util.DateTimeHelper;
//import com.vphoainha.itfmobile.util.JsonTag;
//import com.vphoainha.itfmobile.util.Utils;
//import com.vphoainha.itfmobile.util.WsUrl;
//
//public class AddAnswerActivity extends Activity {
//	TextView tv_index, tv_category, tv_time, tv_content, tv_username, tvForUser;
//	EditText edtContent;
//
//	Context context;
//
//	Thread question=null;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_add_answer);
//
//		TextView tv_title = (TextView) findViewById(R.id.tv_title);
//		ImageButton btn_ok = (ImageButton) findViewById(R.id.btn_ok);
//		tv_title.setText("Post an answer");
//		btn_ok.setVisibility(View.VISIBLE);
//		btn_ok.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				accessWebservice();
//			}
//		});
//		
//		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
//		ln_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		
//		edtContent = (EditText) findViewById(R.id.edtPostAnswer);
//		tv_index = (TextView) findViewById(R.id.tv_postanswer_index);
//		tv_category = (TextView) findViewById(R.id.tv_postanswer_category);
//		tv_time = (TextView) findViewById(R.id.tv_postanswer_time);
//		tv_content = (TextView) findViewById(R.id.tv_postanswer_content);
//		tv_username = (TextView) findViewById(R.id.tv_postanswer_user);
//		tvForUser = (TextView) findViewById(R.id.tvForUser);
//		
//		Intent bundle = getIntent();
//		question=(Thread)bundle.getSerializableExtra("question");
//
//		tv_index.setText("#"+question.getId());
//		tv_category.setText(question.getCategoryId()==Utils.DEFAUT_VALUE?"All":question.getCategoryName());
//		tv_time.setText( DateTimeHelper.dateTimeToDateStringDMY(question.getTime()));
//		tv_content.setText(question.getContent());
//		
//		if(question.getUserId()==Utils.DEFAUT_VALUE) tv_username.setText("by Anonymous");
//		else tv_username.setText("by "+question.getUserName());
//		if(question.getForUserId()==Utils.DEFAUT_VALUE) tvForUser.setText("for Community");
//		else tvForUser.setText("for "+question.getForUserName());
//		
//		context = this;
//	}
//
//	/**
//	 * get list employ from service
//	 */
//	public void accessWebservice() {
//		String content = edtContent.getText().toString().trim();
//		if (content.equals("")) {
//			Toast.makeText(context, "Content is empty!", Toast.LENGTH_SHORT).show();
//		} else {
//			if(!Utils.checkInternetConnection(this))
//				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
//			else	
//				(new JsonReadTask()).execute(new String[] { WsUrl.URL_ADD_ANSWER,
//					content, Integer.toString(Utils.getUserId()),
//					Integer.toString(question.getId()) });
//		}
//	}
//
//	public class JsonReadTask extends AsyncTask<String, Void, String> {
//		ProgressDialog pd;
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			pd=new ProgressDialog(AddAnswerActivity.this);
//			pd.setMessage("Posting your answer...");
//			pd.setCancelable(false);
//			pd.show();
//		}
//		
//		@Override
//		protected String doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			par.add(new BasicNameValuePair("content", params[1]));
//			par.add(new BasicNameValuePair("userId", params[2]));
//			par.add(new BasicNameValuePair("questionId", params[3]));
//			
//			JSONParser jsonParser = new JSONParser();
//			JSONObject json = jsonParser
//					.makeHttpRequest(params[0], "POST", par);
//			Log.d("Create Response", json.toString());
//			try {
//				int success = json.getInt(JsonTag.TAG_SUCCESS);
//				if (success == 1) {
//					return "success";
//				} else {
//					return null;
//				}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			if(pd!=null && pd.isShowing())  pd.dismiss();
//			
//			if (result != null) {
//				Toast.makeText(context, "Your answer was posted in this question!", Toast.LENGTH_SHORT).show();
//				setResult(Activity.RESULT_OK);
//				finish();
//			} else {
//				Toast.makeText(context, "Sorry! Posting fail, try a again later!", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//}
