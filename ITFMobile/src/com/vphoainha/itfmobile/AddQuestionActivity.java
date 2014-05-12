//package com.vphoainha.itfmobile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemSelectedListener;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.vphoainha.itfmobile.adapter.QuestionAdapter;
//import com.vphoainha.itfmobile.jsonparser.JSONParser;
//import com.vphoainha.itfmobile.model.Folder;
//import com.vphoainha.itfmobile.model.Thread;
//import com.vphoainha.itfmobile.util.DateTimeHelper;
//import com.vphoainha.itfmobile.util.JsonTag;
//import com.vphoainha.itfmobile.util.Utils;
//import com.vphoainha.itfmobile.util.WsUrl;
//
//public class AddQuestionActivity extends Activity {
//	private List<Thread> listData;
//	private List<Folder> categories;
//	EditText txtContent;
//	Spinner sp_category, sp_askFor;
//	
//	Context context;
//	int forUserId;
//	int categoryId;
//	
//	List<String> listAskOption;
//	List<String> listCategory;
//	ArrayAdapter<String> dataAskOptionAdapter;
//
//	static final int CHOOSE_EXPERT = 0;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_add_question);
//
//		TextView tv_title = (TextView) findViewById(R.id.tv_title);
//		ImageButton btn_ok = (ImageButton) findViewById(R.id.btn_ok);
//		tv_title.setText("Post a new question");
//		btn_ok.setVisibility(View.VISIBLE);
//		btn_ok.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				accessAddQuestionWebservice();
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
//		txtContent = (EditText) findViewById(R.id.txtContent);
//		sp_category = (Spinner) findViewById(R.id.sp_category);
//		
//		sp_askFor = (Spinner) findViewById(R.id.sp_askFor);
//		listAskOption = new ArrayList<String>();
//		listAskOption.add("Ask the Community");
//		listAskOption.add("Ask an Expert");
//		dataAskOptionAdapter = new ArrayAdapter<String>(AddQuestionActivity.this,android.R.layout.simple_spinner_item, listAskOption);
//		dataAskOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		sp_askFor.setAdapter(dataAskOptionAdapter);
//		sp_askFor.setSelection(0);
//		sp_askFor.setOnItemSelectedListener(new OnItemSelectedListener() {
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int pos, long arg3) {
//				if (pos==1 && forUserId==-1) {
//					Intent in=new Intent(getApplicationContext(), TopExpertActivity.class);
//					in.putExtra("choose_expert", true);
//					startActivityForResult(in, CHOOSE_EXPERT);
//				}else forUserId = -1;
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {}
//		});
//		
//		listData = new ArrayList<Thread>();
//		
//		this.context = this;
//		forUserId = -1;
//		categoryId = -1;
//		
//		txtContent.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(s.toString().equals("")){
//					ListView lv_exist = (ListView) findViewById(R.id.lv_exist);
//					QuestionAdapter adapter = new QuestionAdapter(
//							AddQuestionActivity.this,
//							R.layout.list_item_question, R.id.tv_index,
//							new ArrayList<Thread>(), QuestionAdapter.ALL_QUESTION);
//					lv_exist.setAdapter(adapter);
//				}
//				else accessGetExistQuestionsWebservice(s.toString());
//			}
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
//			@Override
//			public void afterTextChanged(Editable s) {}
//		});
//		
//		accessGetCategoriesWebservice();
//	}
//
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (requestCode == CHOOSE_EXPERT) {
//			if (resultCode == RESULT_OK) {
//				forUserId = data.getExtras().getInt("forUserId");
//				listAskOption.set(1, "Ask an Expert: "+data.getExtras().getString("forUserName"));
//				dataAskOptionAdapter.notifyDataSetChanged();
//			}
//			else{
//				sp_askFor.setSelection(0);
//				Log.i("==", "123");
//			}
//		}
//	}
//
//	public void accessGetExistQuestionsWebservice(String content) {
//		if(!Utils.checkInternetConnection(this))
//			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
//		else	
//			(new JsonReadTask()).execute(new String[] { WsUrl.URL_search_Question_by_Content, content });
//	}
//
//	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//
//		@Override
//		protected Integer doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			par.add(new BasicNameValuePair("content", params[1]));
//
//			JSONParser jsonParser = new JSONParser();
//			JSONObject json = jsonParser
//					.makeHttpRequest(params[0], "POST", par);
//
//			try {
//				listData=new ArrayList<Thread>();
//				
//				int success = json.getInt(JsonTag.TAG_SUCCESS);
//				if (success == 1) {
//					JSONArray array = json.getJSONArray(JsonTag.TAG_THREADS);
//					
//					for (int i = 0; i < array.length(); i++) {
//						JSONObject obj = array.getJSONObject(i);
//
//						Thread question = new Thread();
//						question.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
//						question.setForUserId(Integer.parseInt(obj.getString(JsonTag.TAG_FOR_USER_Id)));
//						question.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
//						question.setUserName(obj.getString(JsonTag.TAG_USER_NAME));
//						question.setContent(obj.getString(JsonTag.TAG_CONTENT));
//						question.setCategoryId(Integer.parseInt(obj.getString(JsonTag.TAG_CATEGORY_ID)));
//						question.setCategoryName(obj.getString(JsonTag.TAG_CATEGORY_NAME));
//						question.setIsAnswered(Integer.parseInt(obj.getString(JsonTag.TAG_IS_ANSWERED)));
//						question.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));
//						question.setSaveUserId(Integer.parseInt(obj.getString(JsonTag.TAG_SAVEUSERID)));
//						
//						listData.add(question);
//					}
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
//			((AddQuestionActivity) context).runOnUiThread(new Runnable() {
//				public void run() {
//					ListView lv_exist = (ListView) findViewById(R.id.lv_exist);
//					QuestionAdapter adapter = new QuestionAdapter(
//							AddQuestionActivity.this,
//							R.layout.list_item_question, R.id.tv_index,
//							listData, QuestionAdapter.ALL_QUESTION);
//					lv_exist.setAdapter(adapter);
//				}
//			});
//		}
//	}
//
//	public void accessAddQuestionWebservice() {
//		String content = txtContent.getText().toString().trim();
//		if (content.equals("")) {
//			Toast.makeText(context, "Please fill question content!", Toast.LENGTH_SHORT).show();
//		} else {
//			if(!Utils.checkInternetConnection(this))
//				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
//			else{
//				categoryId=categories.get(sp_category.getSelectedItemPosition()).getId();
//				Log.i("====categoryId===", categoryId+"");
//				
//				(new JsonAddQuestionTask())
//					.execute(new String[] { WsUrl.URL_ADD_QUESTION,
//							txtContent.getText().toString(),
//							Integer.toString(Utils.getUserId()),
//							Integer.toString(forUserId),
//							Integer.toString(categoryId),
//							Integer.toString(Utils.saveUser.getId())});
//			}
//		}
//	}
//
//	public class JsonAddQuestionTask extends AsyncTask<String, Void, String> {
//		ProgressDialog pd;
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			pd=new ProgressDialog(AddQuestionActivity.this);
//			pd.setMessage("Posting your new question...");
//			pd.setCancelable(false);
//			pd.show();
//		}
//		
//		@Override
//		protected String doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			par.add(new BasicNameValuePair("content", params[1]));
//			par.add(new BasicNameValuePair("userId", params[2]));
//			par.add(new BasicNameValuePair("forUserId", params[3]));
//			par.add(new BasicNameValuePair("categoryId", params[4]));
//			par.add(new BasicNameValuePair("saveUserId", params[5]));
//			
//			JSONParser jsonParser = new JSONParser();
//			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
//			Log.d("Create Response", json.toString());
//
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
//				Toast.makeText(context, "Your question was posted!", Toast.LENGTH_SHORT).show();
//				
//				setResult(RESULT_OK);
//				finish();
//			} else {
//				Toast.makeText(context, "Sorry! Posting fail, try a again later!", Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//	
//	public void accessGetCategoriesWebservice() {
//		(new JsonReadGetCategoriesTask()).execute(new String[] { WsUrl.URL_GET_CATEGORIES});
//	}
//	
//	public class JsonReadGetCategoriesTask extends AsyncTask<String, Void, Integer> {
//		String message = "";
//		ProgressDialog pd;
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			categories=new ArrayList<Folder>();
//			categories.add(new Folder(-1, "All"));
//			
//			pd=new ProgressDialog(AddQuestionActivity.this);
//			pd.setMessage("Loading...");
//			pd.setCancelable(false);
//			pd.show();
//		}
//		
//		@Override
//		protected Integer doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			JSONParser jsonParser = new JSONParser();
//			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
//			Log.d("Create Response", json.toString());
//			
//			try {
//				int success = json.getInt(JsonTag.TAG_SUCCESS);
//				message = json.getString(JsonTag.TAG_MESSAGE);
//				if (success == 1) {
//					JSONArray array = json.getJSONArray(JsonTag.TAG_CATEGORY);
//	
//					// looping through All Products
//					
//					for (int i = 0; i < array.length(); i++) {
//						JSONObject obj = array.getJSONObject(i);
//	
//						int id = Integer.parseInt(obj.getString(JsonTag.TAG_ID));
//						String name = obj.getString(JsonTag.TAG_CATEGORY_NAME);
//						categories.add(new Folder(id, name));
//					}
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
//			if (result ==1) {
//				listCategory = new ArrayList<String>();
//				for(int i=0;i<categories.size();i++)
//					listCategory.add(categories.get(i).getName());
//				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddQuestionActivity.this,android.R.layout.simple_spinner_item, listCategory);
//				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//				sp_category.setAdapter(dataAdapter);
//				sp_category.setSelection(0);
//				
//			} else {
//				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
//}
