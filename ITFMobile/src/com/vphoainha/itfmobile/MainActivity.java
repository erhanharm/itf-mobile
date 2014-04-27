package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.frag.HomeFragment;
import com.vphoainha.itfmobile.frag.MyAnswersFragment;
import com.vphoainha.itfmobile.frag.MyQuestionsFragment;
import com.vphoainha.itfmobile.frag.MyRatingFragment;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;
import com.vphoainha.itfmobile.view.CustomSlidingPaneLayout;

public class MainActivity extends FragmentActivity {

	private CustomSlidingPaneLayout spl;
	private MySharedPreferences mySharedPreferences;
	
	private ImageButton btn_new;
	private LinearLayout lnMyQuestions, lnMyAnswers, lnMyRating, lnAccount, lnLogin, lnLogout;
	private TextView tvUsername, tvUserEmail, tv_title;
	private FrameLayout fl_numnotify;

	private boolean _doubleBackToExitPressedOnce = false;
	
	private JSONObject object;
	int cur_frag=0;
	
	HomeFragment homeFragment;
	MyQuestionsFragment myQuestionsFragment;
	MyAnswersFragment myAnswersFragment;
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		spl = (CustomSlidingPaneLayout) findViewById(R.id.slidingPane);

		if(!Utils.checkInternetConnection(this)){
			Utils.showAlert(this, "Sorry!", getString(R.string.cant_connect_internet), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			return;
		}
		
		//load user saved
		mySharedPreferences = new MySharedPreferences(this);
		mySharedPreferences.getSaveUserPreferences();
		if(Utils.isLogin){
			Log.i("======", Utils.saveUser.getEmail()+"    "+ Utils.saveUser.getPassword());
			(new JsonReadTaskReLogin()).execute(new String[] { WsUrl.URL_LOGIN, Utils.saveUser.getEmail(), Utils.saveUser.getPassword(), Utils.getDeviceID(this)});
		}

		tvUsername = (TextView) findViewById(R.id.tvUserName);
		tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
		btn_new = (ImageButton) findViewById(R.id.btn_new);
		tv_title = (TextView) findViewById(R.id.tv_title);
		fl_numnotify = (FrameLayout) findViewById(R.id.fl_numnotify);

		lnMyQuestions = (LinearLayout) findViewById(R.id.lnMyQuestions);
		lnMyAnswers = (LinearLayout) findViewById(R.id.lnMyAnswers);
		lnMyRating = (LinearLayout) findViewById(R.id.lnMyRating);
		lnAccount = (LinearLayout) findViewById(R.id.lnAccount);
		lnLogin = (LinearLayout) findViewById(R.id.lnLogin);
		lnLogout = (LinearLayout) findViewById(R.id.lnLogout);

		btn_new.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!Utils.isLogin){
					startActivity(new Intent(getApplicationContext(),LoginActivity.class));
				}
				else 		
					startActivityForResult(new Intent(getApplicationContext(),AddQuestionActivity.class), 0);
			}
		});
		
		initViewMenu();
		cur_frag=0;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
	}
	
	@Override
	protected void onResume() {
		spl.closePane();
		try{
			initViewMenu();
		}catch(Exception e){}
		super.onResume();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1==RESULT_OK){
			if(cur_frag==0) homeFragment.accessWebserviceReset();
			if(cur_frag==1) myQuestionsFragment.accessWebserviceReset();
			if(cur_frag==2) myAnswersFragment.accessWebserviceReset();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(spl.isOpen()) spl.closePane();
		else{
			if (_doubleBackToExitPressedOnce) {
			    finish();
			    return;
			}
			this._doubleBackToExitPressedOnce = true;
			Toast.makeText(this, "Press again to exit!", Toast.LENGTH_SHORT).show();
			new Handler().postDelayed(new Runnable() {
			    @Override
			    public void run() {
			        _doubleBackToExitPressedOnce = false;
			    }
			}, 2000);
		}
	}

	private void initViewMenu() {
		TextView tv_register=(TextView)findViewById(R.id.btnRegister);
		if(!Utils.isLogin){
			hideLoggedFunction();
			
			tv_register.setVisibility(View.VISIBLE);
			tv_register.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this,RegisterActivity.class));
				}
			});
		}
		else{
			showLoggedFunction();
			
			tv_register.setVisibility(View.GONE);
		}
	}

	private void setView(final Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
	}

	public void onclickMenu(View v) {
		if (spl.isOpen()) {
			spl.closePane();
		} else {
			spl.openPane();
		}
	}

	public void onclickSetting(View v) {
		spl.closePane();
		startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
	}

	public void onclickTopExpert(View v) {
		startActivity(new Intent(getApplicationContext(), TopExpertActivity.class));
	}

	public void onclickHome(View v) {
		spl.closePane();
		cur_frag=0;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
	}

	public void onclickMyQuestions(View v) {
		spl.closePane();
		cur_frag=1;
		myQuestionsFragment=MyQuestionsFragment.newInstance();
		setView(myQuestionsFragment);
	}

	public void onclickMyAnswers(View v) {
		spl.closePane();
		cur_frag=2;
		myAnswersFragment=MyAnswersFragment.newInstance();
		setView(myAnswersFragment);
	}

	public void onclickMyRating(View v) {
		spl.closePane();
		cur_frag=3;
		setView(MyRatingFragment.newInstance());
	}

	public void onclickLogin(View v) {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}
	
	public void onclickLogout(View v) {
		hideLoggedFunction();
		
		Utils.isLogin=false;
		Utils.saveUser=null;
		mySharedPreferences.setSaveUserPreferences();	
		
		try{
			new LoginActivity().logoutFacebook();
			Log.i("========", "facebook logout ok");
		}catch(Exception ex){}
	}

	public void onclickNotification(View v) {
		startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
	}

	public void hideLoggedFunction() {
		fl_numnotify.setVisibility(View.GONE);
		lnMyQuestions.setVisibility(View.GONE);
		lnMyAnswers.setVisibility(View.GONE);
		lnMyRating.setVisibility(View.GONE);
		lnAccount.setVisibility(View.GONE);
		
		lnLogin.setVisibility(View.VISIBLE);
		lnLogout.setVisibility(View.GONE);
	}
	
	public void showLoggedFunction() {
		fl_numnotify.setVisibility(View.VISIBLE);
		lnMyQuestions.setVisibility(View.VISIBLE);
		lnMyAnswers.setVisibility(View.VISIBLE);
		lnMyRating.setVisibility(View.VISIBLE);
		lnAccount.setVisibility(View.VISIBLE);
		
		lnLogin.setVisibility(View.GONE);
		lnLogout.setVisibility(View.VISIBLE);
		if(Utils.saveUser!=null){
			tvUsername.setText(Utils.saveUser.getName());
			tvUserEmail.setText(Utils.saveUser.getEmail());
		}
	}
	
	public void changeMainTitleBarText(String text) {
		tv_title.setText(text);
		tv_title.setSelected(true);
	}

	
	//recheck login
	public class JsonReadTaskReLogin extends AsyncTask<String, Void, Integer> {
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(MainActivity.this);
			pd.setMessage("Checking user info...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("email", params[1]));
			par.add(new BasicNameValuePair("password", params[2]));
			par.add(new BasicNameValuePair("deviceId", params[3]));
			
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "GET", par);
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
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			if (result==1) {	
				if(object!=null){
					LoginActivity.readAndSaveLoginJsonObject(MainActivity.this, object);
					
					showLoggedFunction();
				}
			} else {
				Utils.showAlert(MainActivity.this, "", "Your previous user info is incorrect. Please login again!");
				onclickLogout(null);
			}
		}
	}


	public HomeFragment getHomeFragment() {
		return homeFragment;
	}

	public MyQuestionsFragment getMyQuestionsFragment() {
		return myQuestionsFragment;
	}

	public MyAnswersFragment getMyAnswersFragment() {
		return myAnswersFragment;
	}

	public int getCur_frag() {
		return cur_frag;
	}
	
	
}
