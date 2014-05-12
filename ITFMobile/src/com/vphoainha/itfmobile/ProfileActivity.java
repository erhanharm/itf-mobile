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
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.vphoainha.itfmobile.jsonparser.JSONParser;
//import com.vphoainha.itfmobile.util.JsonTag;
//import com.vphoainha.itfmobile.util.MySharedPreferences;
//import com.vphoainha.itfmobile.util.Utils;
//import com.vphoainha.itfmobile.util.WsUrl;
//
//public class ProfileActivity extends Activity{
//	
//	EditText txtEmail, txtName, txtOldPass, txtPassword, txtConfirm;
//	CheckBox chkAnonymous;
//	Button btnUpdate;
//	
//	String msg;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_profile);
//		
//		TextView tv_title = (TextView) findViewById(R.id.tv_title);
//		tv_title.setText("User profile");
//		
//		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
//		ln_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		
//		txtEmail=(EditText)findViewById(R.id.txtEmail);
//		txtName=(EditText)findViewById(R.id.txtName);
//		txtOldPass=(EditText)findViewById(R.id.txtOldPass);
//		txtPassword=(EditText)findViewById(R.id.txtPassword);
//		txtConfirm=(EditText)findViewById(R.id.txtConfirm);
//		chkAnonymous=(CheckBox)findViewById(R.id.chkAnonymous);
//		btnUpdate=(Button)findViewById(R.id.btnUpdate);
//		
//		txtEmail.setText(Utils.saveUser.getEmail());
//		txtName.setText(Utils.saveUser.getName());
//		chkAnonymous.setChecked(Utils.saveUser.getIsAnonymous()==1?true:false);
//		
//		btnUpdate.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String p1,p2,p3;
//				p1=txtOldPass.getText().toString();
//				p2=txtPassword.getText().toString();
//				p3=txtConfirm.getText().toString();
//				
//				String password="";
//				
//				if(!p1.equals("") || !p2.equals("") || !p3.equals("")){
//					
//					String msg="";
//					if(!p3.equals(p2)) msg="Confirm password is not same with password!";
//					if(p1.equals(p2)) msg="New Password is must be different with Old Password!";
//					if(p2.equals("")) msg="Password is not be empty!";
//					if(!Utils.md5(p1).equals(Utils.saveUser.getPassword())) msg="Old Password is not correct!";
//					
//					
//					if(!msg.equals("")){
//						Utils.showAlert(ProfileActivity.this, "", msg);
//						return;
//					}
//					
//					password=Utils.md5(p2);
//				}
//				
//				accessWebservice(password, txtName.getText().toString(), chkAnonymous.isChecked()?1:0);
//			}
//		});
//	}
//	
//	public void accessWebservice(String password, String name, int isAnonymous) {
//		(new JsonReadTask()).execute(new String[] { WsUrl.URL_UPDATE_PROFILE,
//				Integer.toString(Utils.saveUser.getId()),
//				Utils.saveUser.getEmail(), password, name, Integer.toString(isAnonymous) });
//	}
//
//	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
//		ProgressDialog pd;
//		
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			pd=new ProgressDialog(ProfileActivity.this);
//			pd.setMessage("Updating your profile...");
//			pd.setCancelable(false);
//			pd.show();
//		}
//		
//		@Override
//		protected Integer doInBackground(String... params) {
//			List<NameValuePair> par = new ArrayList<NameValuePair>();
//			par.add(new BasicNameValuePair("userId", params[1]));
//			par.add(new BasicNameValuePair("email", params[2]));
//			par.add(new BasicNameValuePair("password", params[3]));
//			par.add(new BasicNameValuePair("name", params[4]));
//			par.add(new BasicNameValuePair("isAnonymous", params[5]));
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
//			
//			if(result==1){
//				Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
//				
//				Utils.saveUser.setName(txtName.getText().toString());
//				Utils.saveUser.setIsAnonymous(chkAnonymous.isChecked()?1:0);
//				if(!txtPassword.getText().toString().equals(""))
//					Utils.saveUser.setPassword(Utils.md5(txtPassword.getText().toString()));
//				new MySharedPreferences(ProfileActivity.this).setSaveUserPreferences();
//				
//				finish();
//			}
//			else Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
//		}
//	}
//}
//
