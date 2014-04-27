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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class RegisterActivity extends Activity {
	private EditText nameEdt, emailEdt, passwordEdt, confirmEdt;
	private JSONObject object;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("Register an account");
		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
		ln_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		nameEdt = (EditText) findViewById(R.id.edtRegisterName);
		emailEdt = (EditText) findViewById(R.id.edtRegisterEmail);
		passwordEdt = (EditText) findViewById(R.id.edtRegisterPassword);
		confirmEdt = (EditText) findViewById(R.id.edtRegisterConfirm);
	}

	public void onClick(View view) {
		if (view.getId() == R.id.btnRegister) {
			String msg="";
			String name = nameEdt.getText().toString().trim();
			String email = emailEdt.getText().toString().trim();
			String password = passwordEdt.getText().toString();
			String confirm = confirmEdt.getText().toString();
			if(!confirm.equals(password)) msg="Confirm password is not same with password!";
			if(password.equals("")) msg="Password is not be empty!";
			if(!Utils.validateEmail(email)) msg="Email is not correct!";
			if(name.equals("")) msg="Name is not be empty!";
			if(!msg.equals("")){
				Utils.showAlert(RegisterActivity.this, "", msg);
				return;
			}
			
			if(!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else accessWebserviceREGISTER(name, email, password, Utils.getDeviceID(this));
		}
	}

	/**
	 * get list employ from service
	 */
	public void accessWebserviceREGISTER(String name, String email, String password, String deviceId) {
		(new JsonReadTaskREGISTER()).execute(new String[] { WsUrl.URL_REGISTER, name, email, password, deviceId });
	}

	private class JsonReadTaskREGISTER extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(RegisterActivity.this);
			pd.setMessage("Registering your new account...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("name", params[1]));
			par.add(new BasicNameValuePair("email", params[2]));
			par.add(new BasicNameValuePair("password", params[3]));
			par.add(new BasicNameValuePair("deviceId", params[4]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_USER);
					object = array.getJSONObject(0);

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
				Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
				
				LoginActivity.readAndSaveLoginJsonObject(RegisterActivity.this, object);
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(RegisterActivity.this, "Register fail. Try again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
