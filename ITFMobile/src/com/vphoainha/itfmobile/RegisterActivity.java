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
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;

public class RegisterActivity extends FatherActivity {
	private EditText txtUsername, txtEmail, txtPassword, txtConfirm;
	private JSONObject object;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initFather();
		
		tvTitle.setText("Register an account");
		
		txtUsername = (EditText) findViewById(R.id.edtRegisterName);
		txtEmail = (EditText) findViewById(R.id.edtRegisterEmail);
		txtPassword = (EditText) findViewById(R.id.edtRegisterPassword);
		txtConfirm = (EditText) findViewById(R.id.edtRegisterConfirm);
	}

	public void onClick(View view) {
		if (view.getId() == R.id.btnRegister) {
			String msg="";
			String username = txtUsername.getText().toString().trim();
			String email = txtEmail.getText().toString().trim();
			String password = txtPassword.getText().toString();
			String confirm = txtConfirm.getText().toString();
			if(!confirm.equals(password)) msg="Confirm password is not same with password!";
			if(password.equals("")) msg="Password is not be empty!";
			if(!Util.validateEmail(email)) msg="Email is not correct!";
			if(username.equals("")) msg="User name is not be empty!";
			if(!msg.equals("")){
				Util.showAlert(RegisterActivity.this, "", msg);
				return;
			}
			
			if(!Util.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else accessWebserviceREGISTER(username, email, Util.md5(password), Util.getDeviceID(this));
		}
	}

	/**
	 * get list employ from service
	 */
	public void accessWebserviceREGISTER(String userName, String email, String password, String deviceId) {
		(new JsonReadTaskREGISTER()).execute(new String[] { WsUrl.URL_REGISTER, userName, email, password, deviceId });
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
			par.add(new BasicNameValuePair("username", params[1]));
			par.add(new BasicNameValuePair("email", params[2]));
			par.add(new BasicNameValuePair("password", params[3]));
			par.add(new BasicNameValuePair("device_id", params[4]));
			
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
