package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class AddThreadActivity extends FatherActivity {
	private List<Thread> listData;
	private List<Folder> categories;
	EditText txtContent;
	Spinner sp_category, sp_askFor;
	
	Context context;
	int forUserId;
	int categoryId;
	
	List<String> listAskOption;
	List<String> listCategory;
	ArrayAdapter<String> dataAskOptionAdapter;

	static final int CHOOSE_EXPERT = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_thread);
		initFather();
		
		tvTitle.setText("Post a new thread");
		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				accessAddQuestionWebservice();
			}
		});
		
		txtContent = (EditText) findViewById(R.id.txtContent);
		
		listData = new ArrayList<Thread>();
		
		this.context = this;
	}

	public void accessAddQuestionWebservice() {
		String content = txtContent.getText().toString().trim();
		if (content.equals("")) {
			Toast.makeText(context, "Please fill question content!", Toast.LENGTH_SHORT).show();
		} else {
			if(!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else{
				categoryId=categories.get(sp_category.getSelectedItemPosition()).getId();
				Log.i("====categoryId===", categoryId+"");
				
				(new JsonAddQuestionTask())
					.execute(new String[] { WsUrl.URL_ADD_QUESTION,
							txtContent.getText().toString(),
							Integer.toString(1),
							Integer.toString(forUserId),
							Integer.toString(categoryId),
							Integer.toString(Utils.saveUser.getId())});
			}
		}
	}

	public class JsonAddQuestionTask extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(AddThreadActivity.this);
			pd.setMessage("Posting your new question...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("content", params[1]));
			par.add(new BasicNameValuePair("userId", params[2]));
			par.add(new BasicNameValuePair("forUserId", params[3]));
			par.add(new BasicNameValuePair("categoryId", params[4]));
			par.add(new BasicNameValuePair("saveUserId", params[5]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					return "success";
				} else {
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			if (result != null) {
				Toast.makeText(context, "Your question was posted!", Toast.LENGTH_SHORT).show();
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Posting fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
