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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.frag.HomeFragment;
import com.vphoainha.itfmobile.frag.MyAnswersFragment;
import com.vphoainha.itfmobile.frag.MyQuestionsFragment;
import com.vphoainha.itfmobile.frag.MyRatingFragment;
import com.vphoainha.itfmobile.frag.ProfileFragment;
import com.vphoainha.itfmobile.frag.SearchFragment;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Util;
import com.vphoainha.itfmobile.util.WsUrl;
import com.vphoainha.itfmobile.view.CustomSlidingPaneLayout;

public class MainActivity extends FragmentActivity {

	final int FRAG_HOME=0;
	final int FRAG_SEARCH=1;
	final int FRAG_PROFILE=3;
	
	private CustomSlidingPaneLayout spl;
	private MySharedPreferences mySharedPreferences;
	
	private LinearLayout lnMyQuestions, lnMyAnswers, lnMyRating, lnAccount, lnLogin, lnLogout;
	private TextView tvUsername, tvUserEmail, tv_title;
	private EditText txtSearch;
	private ImageButton btnSearch;
	private FrameLayout fl_numnotify;

	private boolean _doubleBackToExitPressedOnce = false;
	
	private JSONObject object;
	int cur_frag=FRAG_HOME;
	
	HomeFragment homeFragment;
	SearchFragment searchFragment;
	ProfileFragment profileFragment;
	MyQuestionsFragment myQuestionsFragment;
	MyAnswersFragment myAnswersFragment;
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		spl = (CustomSlidingPaneLayout) findViewById(R.id.slidingPane);

		if(!Util.checkInternetConnection(this)){
			Util.showAlert(this, "Sorry!", getString(R.string.cant_connect_internet), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			return;
		}
		
		init();

		tvUsername = (TextView) findViewById(R.id.tvUserName);
		tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
		tv_title = (TextView) findViewById(R.id.tv_title);
		fl_numnotify = (FrameLayout) findViewById(R.id.fl_numnotify);

		lnMyQuestions = (LinearLayout) findViewById(R.id.lnMyQuestions);
		lnMyAnswers = (LinearLayout) findViewById(R.id.lnMyAnswers);
		lnMyRating = (LinearLayout) findViewById(R.id.lnMyRating);
		lnAccount = (LinearLayout) findViewById(R.id.lnAccount);
		lnLogin = (LinearLayout) findViewById(R.id.lnLogin);
		lnLogout = (LinearLayout) findViewById(R.id.lnLogout);
		
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch();
			}
		});
//		txtSearch.seton

		initViewMenu();
		cur_frag=FRAG_HOME;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
	}
	
	private void init(){
		
		
		//load user saved
		mySharedPreferences = new MySharedPreferences(this);
		mySharedPreferences.getSaveUserPreferences();
		if(AppData.isLogin){
			Log.i("======", AppData.saveUser.getEmail()+"    "+ AppData.saveUser.getPassword());
			(new JsonReadTaskReLogin()).execute(new String[] { WsUrl.URL_LOGIN, AppData.saveUser.getUsername(), AppData.saveUser.getPassword(), Util.getDeviceID(this)});
		}		
	}
	
	private void startSearch(){
		String searchContent=txtSearch.getText().toString();
		if(searchContent.equals("")) {
			Toast.makeText(MainActivity.this, "Please fill search content!", Toast.LENGTH_SHORT).show();
			return;
		}
		spl.closePane();
		cur_frag=FRAG_SEARCH;
		searchFragment=SearchFragment.newInstance(searchContent);
		setView(searchFragment);
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
//		if(arg1==RESULT_OK){
//			if(cur_frag==0) homeFragment.accessWebserviceReset();
//			if(cur_frag==1) myQuestionsFragment.accessWebserviceReset();
//			if(cur_frag==2) myAnswersFragment.accessWebserviceReset();
//		}
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
		if(!AppData.isLogin){
			hideLoggedFunction();
		}
		else{
			showLoggedFunction();
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
	
	public void onclickHome(View v) {
		spl.closePane();
		cur_frag=FRAG_HOME;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
	}
	
	public void onclickProfile(View v) {
		spl.closePane();
		cur_frag=FRAG_PROFILE;
		profileFragment=ProfileFragment.newInstance();
		setView(profileFragment);
	}

	public void onclickLogin(View v) {
		startActivity(new Intent(MainActivity.this, LoginActivity.class));
	}
	
	public void onclickLogout(View v) {
		hideLoggedFunction();
		
		AppData.isLogin=false;
		AppData.saveUser=null;
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
//		fl_numnotify.setVisibility(View.VISIBLE);
//		lnMyQuestions.setVisibility(View.VISIBLE);
//		lnMyAnswers.setVisibility(View.VISIBLE);
//		lnMyRating.setVisibility(View.VISIBLE);
		lnAccount.setVisibility(View.VISIBLE);
		
		lnLogin.setVisibility(View.GONE);
		lnLogout.setVisibility(View.VISIBLE);
		if(AppData.saveUser!=null){
			if(AppData.saveUser.getName().equals("")) tvUsername.setText(AppData.saveUser.getUsername());
			else tvUsername.setText(AppData.saveUser.getName());
			tvUserEmail.setText(AppData.saveUser.getEmail());
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
				Util.showAlert(MainActivity.this, "", "Your previous user info is incorrect. Please login again!");
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
