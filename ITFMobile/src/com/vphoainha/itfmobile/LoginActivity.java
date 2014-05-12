package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.OnErrorListener;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.User;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class LoginActivity extends FatherActivity {
	final String SECURITY_CODE = "123454321";
	final String DEFAULT_PASS = "123456";

	private EditText txtUsername, txtPassword;
	
	private JSONObject object;
	GraphUser faceUser;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initFather();

		tvTitle.setText("Login");

		txtUsername = (EditText) findViewById(R.id.edtLoginEmail);
		txtPassword = (EditText) findViewById(R.id.edtLoginPassword);

		LoginButton btnLoginFacebook = (LoginButton) findViewById(R.id.btnLoginFacebook);
		btnLoginFacebook.setOnErrorListener(new OnErrorListener() {
			@Override
			public void onError(FacebookException error) {
				Log.i("", "Error " + error.getMessage());
			}
		});
		// set permission list, Don't foeget to add email
		btnLoginFacebook.setReadPermissions(Arrays.asList("basic_info",
				"email", "user_friends"));
		// session state call back event
		btnLoginFacebook.setSessionStatusCallback(new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				if (session.isOpened()) {
					Log.i("", "Access Token" + session.getAccessToken());
					Request request = Request.newMeRequest(session,
							new Request.GraphUserCallback() {
								@Override
								public void onCompleted(GraphUser user,
										Response response) {
									if (user != null) {
										faceUser=user;
//										Log.i("========", "facebook login ok"+user.getFirstName());
										
										accessWebserviceCHECKEMAIL(user.asMap().get("email").toString());
									}
								}
							});
					request.executeAsync();
				}
			}
		});
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnLogin:
			accessWebserviceLOGIN();
			break;
		case R.id.btnRegister:
			startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	public void logoutFacebook() {
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {
			session = new Session(this);
			Session.setActiveSession(session);
			session.closeAndClearTokenInformation();
			// clear your preferences if saved
		}
	}

	private void accessWebserviceLOGIN() {
		String msg = "";
		String userName = txtUsername.getText().toString();
		String password = txtPassword.getText().toString();
		if (password.equals(""))
			msg = "Password is not be empty!";
		if (userName.equals(""))
			msg = "Username is not be empty!";
		if (!msg.equals("")) {
			Utils.showAlert(LoginActivity.this, "", msg);
			return;
		}

		if (!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet),
					Toast.LENGTH_SHORT).show();
		else
			(new JsonReadTaskLOGIN()).execute(new String[] { WsUrl.URL_LOGIN,
					userName, Utils.md5(password), Utils.getDeviceID(this) });
	}

	public class JsonReadTaskLOGIN extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(LoginActivity.this);
			pd.setMessage("Logging in...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("username", params[1]));
			par.add(new BasicNameValuePair("password", params[2]));
			par.add(new BasicNameValuePair("device_id", params[3]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
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
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (result != null) {
				readAndSaveLoginJsonObject(LoginActivity.this, object);

				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(LoginActivity.this,
						"Login fail! Try a again later.", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void accessWebserviceCHECKEMAIL(String email) {
		(new JsonReadTaskCHECKEMAIL()).execute(new String[] { WsUrl.URL_CHECK_FACEBOOK_EMAIL,
				email, SECURITY_CODE, Utils.getDeviceID(this) });
	}

	public class JsonReadTaskCHECKEMAIL extends
			AsyncTask<String, Void, Integer> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(LoginActivity.this);
			pd.setMessage("Checking user info...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("email", params[1]));
			par.add(new BasicNameValuePair("security", params[2]));
			par.add(new BasicNameValuePair("device_id", params[3]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_USER);
					object = array.getJSONObject(0);

					return 1;
				} else {
					return 0;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (result == 1) {
				readAndSaveLoginJsonObject(LoginActivity.this, object);

				setResult(RESULT_OK);
				finish();
			} else {
				(new JsonReadTaskREGISTER()).execute(new String[] { WsUrl.URL_REGISTER, faceUser.getUsername(), faceUser.asMap().get("email").toString(), Utils.md5(DEFAULT_PASS), Utils.getDeviceID(LoginActivity.this)});
			}
		}
	}
	
	private class JsonReadTaskREGISTER extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(LoginActivity.this);
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
				readAndSaveLoginJsonObject(LoginActivity.this, object);
				
				Utils.showAlert(LoginActivity.this, "", "Register successfully! Your new account password is 123456, please go to your profile to change the password!", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
				
			} else {
				Toast.makeText(LoginActivity.this, "Register fail. Try again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static void readAndSaveLoginJsonObject(Context ctx, JSONObject object) {
		if (object != null) {
			try {
				Utils.isLogin = true;
				Utils.saveUser = new User();
				Utils.saveUser.setId(Integer.parseInt(object.getString(JsonTag.TAG_ID)));
				Utils.saveUser.setUsername(object.getString(JsonTag.TAG_USER_NAME));
				Utils.saveUser.setPassword(object.getString(JsonTag.TAG_PASSWORD));
				Utils.saveUser.setName(object.getString(JsonTag.TAG_NAME));	
				Utils.saveUser.setEmail(object.getString(JsonTag.TAG_EMAIL));	
				Utils.saveUser.setUserType(Integer.parseInt(object.getString(JsonTag.TAG_USER_TYPE)));
				Utils.saveUser.setUserClass(object.getString(JsonTag.TAG_CLASS));	
				Utils.saveUser.setBirthday(DateTimeHelper.stringToDateTime(object.getString(JsonTag.TAG_BIRTHDAY)));	
				Utils.saveUser.setJoinDate(DateTimeHelper.stringToDateTime(object.getString(JsonTag.TAG_JOINDATE)));	
				Utils.saveUser.setAddress(object.getString(JsonTag.TAG_ADDRESS));	
				Utils.saveUser.setInterest(object.getString(JsonTag.TAG_INTEREST));	
				Utils.saveUser.setSignature(object.getString(JsonTag.TAG_SIGNATURE));	
				Utils.saveUser.setName(object.getString(JsonTag.TAG_NAME));	
				Utils.saveUser.setDeviceId(object.getString(JsonTag.TAG_DEVICE_ID));

				new MySharedPreferences(ctx)
						.setSaveUserPreferences();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
