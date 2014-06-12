package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.google.android.gcm.GCMRegistrar;
import com.vphoainha.itfmobile.frag.HomeFragment;
import com.vphoainha.itfmobile.frag.ProfileFragment;
import com.vphoainha.itfmobile.frag.SearchFragment;
import com.vphoainha.itfmobile.frag.TopMemberFragment;
import com.vphoainha.itfmobile.gcm.Config;
import com.vphoainha.itfmobile.gcm.Controller;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.User;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;
import com.vphoainha.itfmobile.view.CustomSlidingPaneLayout;

public class MainActivity extends FragmentActivity {

	private static final int ADD_FOLDER = 0;
	
	final int FRAG_HOME=0;
	final int FRAG_SEARCH=1;
	final int FRAG_PROFILE=3;
	final int FRAG_TOPMEMBER=4;
	
	private CustomSlidingPaneLayout spl;
	private MySharedPreferences mySharedPreferences;
	
	private LinearLayout lnAccount, lnLogin, lnLogout;
	private TextView tvUsername, tvUserEmail, tv_title, tv_numnotify;
	private EditText txtSearch;
	private ImageButton btnSearch, btnAddFolder, btnRefresh;
	private FrameLayout fl_numnotify, fl_numunread;

	private boolean _doubleBackToExitPressedOnce = false;
	
	private JSONObject object;
	int cur_frag=FRAG_HOME;
	
	HomeFragment homeFragment;
	SearchFragment searchFragment;
	ProfileFragment profileFragment;
	TopMemberFragment topMemberFragment;
	
	private Timer _timer;
	TickClass timer_tick;
	
	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		spl = (CustomSlidingPaneLayout) findViewById(R.id.slidingPane);

		if (!Utils.checkInternetConnection(this)) {
			Utils.showAlert(this, "Sorry!", getString(R.string.cant_connect_internet), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			return;
		}
		
		// init GCM and save regId to Utils
		init();
		initGCM();

		tvUsername = (TextView) findViewById(R.id.tvUserName);
		tvUserEmail = (TextView) findViewById(R.id.tvUserEmail);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_numnotify = (TextView) findViewById(R.id.tv_numnotify);
		fl_numnotify = (FrameLayout) findViewById(R.id.fl_numnotify);
		fl_numunread = (FrameLayout) findViewById(R.id.fl_numunread);
		btnAddFolder = (ImageButton) findViewById(R.id.btn_add_folder);
		btnRefresh = (ImageButton) findViewById(R.id.btn_refresh);

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
		btnAddFolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MainActivity.this, AddFolderActivity.class), ADD_FOLDER);
			}
		});
		btnRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				homeFragment.refreshHome();
			}
		});

		initViewMenu();
		cur_frag=FRAG_HOME;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
		
		_timer = new Timer();
		timer_tick = new TickClass();
		_timer.scheduleAtFixedRate(timer_tick, 0, 1000);
	}
	
	private void init(){
		
		
		//load user saved
		mySharedPreferences = new MySharedPreferences(this);
		mySharedPreferences.getSaveUserPreferences();
	}
	
	private void startSearch(){
		String searchContent=txtSearch.getText().toString();
		txtSearch.setText("");
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
		if(arg0==ADD_FOLDER && arg1==RESULT_OK){
			homeFragment.wsGetFolders();
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
	
	public void onclickForum(View v) {
		spl.closePane();
		cur_frag=FRAG_HOME;
		homeFragment=HomeFragment.newInstance();
		setView(homeFragment);
	}
	
	public void onclickTopMember(View v) {
		spl.closePane();
		cur_frag=FRAG_TOPMEMBER;
		topMemberFragment=TopMemberFragment.newInstance();
		setView(topMemberFragment);
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
		Utils.numUnreadNotifications = 0;
		startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
	}

	public void hideLoggedFunction() {
		btnAddFolder.setVisibility(View.GONE);
		fl_numnotify.setVisibility(View.GONE);
		lnAccount.setVisibility(View.GONE);
		
		lnLogin.setVisibility(View.VISIBLE);
		lnLogout.setVisibility(View.GONE);
	}
	
	public void showLoggedFunction() {
		if(AppData.saveUser.getUserType()==User.USER_ADMIN){
			btnAddFolder.setVisibility(View.VISIBLE);
		}
		
		fl_numnotify.setVisibility(View.VISIBLE);
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
	public class jsReLogin extends AsyncTask<String, Void, Integer> {
//		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			pd=new ProgressDialog(MainActivity.this);
//			pd.setMessage("Checking user info...");
//			pd.setCancelable(false);
//			pd.show();
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
//			if(pd!=null && pd.isShowing())  pd.dismiss();
			
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

	public int getCur_frag() {
		return cur_frag;
	}
	
	class TickClass extends TimerTask {
		@Override
		public void run() {
			if (Integer.parseInt(tv_numnotify.getText().toString())==Utils.numUnreadNotifications)
				return;

			runOnUiThread(new Runnable() {
				public void run() {
					if (Utils.numUnreadNotifications == 0) {
						fl_numunread.setVisibility(View.GONE);
					} else {
						tv_numnotify.setText(Utils.numUnreadNotifications+"");
						fl_numunread.setVisibility(View.VISIBLE);
					}
				}
			});
			
		}
	}
	
	private void initGCM() {
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest permissions was properly set
		GCMRegistrar.checkManifest(this);

		// Get GCM registration id
		Utils.regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (Utils.regId.equals("")) {
			GCMRegistrar.register(this, Config.GOOGLE_SENDER_ID); // Register
																	// with GCM
			// Get GCM registration id again
			Utils.regId = GCMRegistrar.getRegistrationId(this);
		}

		Controller.register(MainActivity.this, Utils.regId, 0);
		
		if(AppData.isLogin){
			Log.i("======", AppData.saveUser.getEmail()+"    "+ AppData.saveUser.getPassword());
			Log.i("===GCM device ID===", Utils.regId);
//			Toast.makeText(this, "===GCM device ID==="+Utils.regId, Toast.LENGTH_SHORT).show();
			(new jsReLogin()).execute(new String[] { WsUrl.URL_LOGIN, AppData.saveUser.getUsername(), AppData.saveUser.getPassword(), Utils.getDeviceID(this)});
			
			AppData.users=new ArrayList<User>();
			if(AppData.saveUser.getUserType()==User.USER_ADMIN){
				(new jsGetAllUsers()).execute(new String[] { WsUrl.URL_GET_USERS});
			}
		}
	}
	
	public class jsGetAllUsers extends AsyncTask<String, Void, Integer> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_USERS);

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						User user = new User();
						user.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						user.setUsername(obj.getString(JsonTag.TAG_USER_NAME));
						user.setPassword(obj.getString(JsonTag.TAG_PASSWORD));
						user.setName(obj.getString(JsonTag.TAG_NAME));
						user.setEmail(obj.getString(JsonTag.TAG_EMAIL));
						user.setUserType(Integer.parseInt(obj.getString(JsonTag.TAG_USER_TYPE)));
						user.setUserClass(obj.getString(JsonTag.TAG_CLASS));
						user.setBirthday(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_BIRTHDAY)));
						user.setJoinDate(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_JOINDATE)));
						user.setAddress(obj.getString(JsonTag.TAG_ADDRESS));
						user.setInterest(obj.getString(JsonTag.TAG_INTEREST));
						user.setSignature(obj.getString(JsonTag.TAG_SIGNATURE));
						user.setName(obj.getString(JsonTag.TAG_NAME));
						user.setDeviceId(obj.getString(JsonTag.TAG_DEVICE_ID));
						
						AppData.users.add(user);
					}
					Log.i("======num of users=====", AppData.users.size()+" user(s)");
					
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
		protected void onPostExecute(Integer result) { }
	}
}
