package com.vphoainha.itfmobile.frag;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.vphoainha.itfmobile.MainActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class ProfileFragment extends Fragment {
	static View view;	
	
	EditText txtEmail, txtName, txtOldPass, txtPassword, txtConfirm, txtClass, txtAddress, txtInterest, txtSignature;
	Button btnUpdate;
	
	String msg;

	public static ProfileFragment newInstance() {
		return new ProfileFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.frag_profile, container, false);
		} catch (InflateException e) {
		}

		((MainActivity) getActivity()).changeMainTitleBarText("My Profile");
		
		txtEmail=(EditText)view.findViewById(R.id.txtEmail);
		txtName=(EditText)view.findViewById(R.id.txtName);
		txtOldPass=(EditText)view.findViewById(R.id.txtOldPass);
		txtPassword=(EditText)view.findViewById(R.id.txtPassword);
		txtConfirm=(EditText)view.findViewById(R.id.txtConfirm);
		txtClass=(EditText)view.findViewById(R.id.txtClass);
		txtAddress=(EditText)view.findViewById(R.id.txtAddress);
		txtInterest=(EditText)view.findViewById(R.id.txtInterest);
		txtSignature=(EditText)view.findViewById(R.id.txtSignature);
		btnUpdate=(Button)view.findViewById(R.id.btnUpdate);
		
		txtEmail.setText(AppData.saveUser.getEmail());
		txtName.setText(AppData.saveUser.getName());
		txtClass.setText(AppData.saveUser.getUserClass());
		txtAddress.setText(AppData.saveUser.getAddress());
		txtInterest.setText(AppData.saveUser.getInterest());
		txtSignature.setText(AppData.saveUser.getSignature());
		
		btnUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String p1,p2,p3;
				p1=txtOldPass.getText().toString();
				p2=txtPassword.getText().toString();
				p3=txtConfirm.getText().toString();
				
				String password="";
				
				if(!p1.equals("") || !p2.equals("") || !p3.equals("")){
					
					String msg="";
					if(!p3.equals(p2)) msg="Confirm password is not same with password!";
					if(p1.equals(p2)) msg="New Password is must be different with Old Password!";
					if(p2.equals("")) msg="Password is not be empty!";
					if(!Utils.md5(p1).equals(AppData.saveUser.getPassword())) msg="Old Password is not correct!";
					
					
					if(!msg.equals("")){
						Utils.showAlert(getActivity(), "", msg);
						return;
					}
					
					password=Utils.md5(p2);
				}
				
				(new JsonReadTask()).execute(new String[] { WsUrl.URL_UPDATE_PROFILE,
						AppData.saveUser.getId()+"",
						password, txtName.getText().toString(), 
						txtClass.getText().toString(),txtAddress.getText().toString(),
						txtInterest.getText().toString(),txtSignature.getText().toString()});
			}
		});
		
		return view;
	}
	
	public class JsonReadTask extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(getActivity());
			pd.setMessage("Updating...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("user_id", params[1]));
			par.add(new BasicNameValuePair("password", params[2]));
			par.add(new BasicNameValuePair("name", params[3]));
			par.add(new BasicNameValuePair("class", params[4]));
			par.add(new BasicNameValuePair("address", params[5]));
			par.add(new BasicNameValuePair("interest", params[6]));
			par.add(new BasicNameValuePair("signature", params[7]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());
			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				msg=json.getString(JsonTag.TAG_MESSAGE);
				if (success == 1) {
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
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			
			if(result==1){
				Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				
				AppData.saveUser.setName(txtName.getText().toString());
				if(!txtPassword.getText().toString().equals(""))
					AppData.saveUser.setPassword(Utils.md5(txtPassword.getText().toString()));
				AppData.saveUser.setUserClass(txtClass.getText().toString());
				AppData.saveUser.setAddress(txtAddress.getText().toString());
				AppData.saveUser.setInterest(txtInterest.getText().toString());
				AppData.saveUser.setSignature(txtSignature.getText().toString());
				new MySharedPreferences(getActivity()).setSaveUserPreferences();
			}
			else Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
	}
}

