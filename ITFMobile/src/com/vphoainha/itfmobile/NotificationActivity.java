package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.vphoainha.itfmobile.adapter.NotificationAdapter;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Notification;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.MySharedPreferences;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class NotificationActivity extends FatherActivity {
	private List<Notification> notifications;

	ListView lvNotification;
	NotificationAdapter adapter;

	String message;
	int from;
	boolean isMaximum;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notification);

		initFather();
		tvTitle.setText("All Notifications");
		tvSubTitle.setVisibility(View.GONE);

		MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
		mySharedPreferences.getSaveUserPreferences();
		if (AppData.isLogin == false) {
			finish();
		}

		lvNotification = (ListView) findViewById(R.id.lv_notification);
		notifications = new ArrayList<Notification>();
		adapter = new NotificationAdapter(NotificationActivity.this, R.layout.list_item_notification, R.id.tvId, notifications);
		lvNotification.setAdapter(adapter);

		message = "";
		isMaximum = false;
		from = 1;

		wsGetNotification();
	}

	public boolean isMaximum() {
		return isMaximum;
	}

	public void wsGetNotification() {
		if (!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else
			(new jsGetNotification()).execute(new String[] { WsUrl.URL_GET_NOTIFICATIONS, Integer.toString(AppData.saveUser.getId()), Integer.toString(from), Integer.toString(from += 10) });
	}

	private class jsGetNotification extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(NotificationActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("user_id", params[1]));
			par.add(new BasicNameValuePair("from", params[2]));
			par.add(new BasicNameValuePair("to", params[3]));

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				message = json.getString(JsonTag.TAG_MESSAGE);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_NOTIFICATION);
					if (array.length() == 0) {
						isMaximum = true;
					} else {
						from++;
					}

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Notification notification = new Notification();
						notification.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						notification.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
						notification.setForUserId(Integer.parseInt(obj.getString(JsonTag.TAG_FOR_USER_Id)));
						notification.setContent(obj.getString(JsonTag.TAG_CONTENT));
						notification.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));

						notifications.add(notification);
					}
					return "success";
				} else {
					isMaximum = true;
					return message;
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
				if (message.contains("No notifications found")) {
					Toast.makeText(NotificationActivity.this, message, Toast.LENGTH_SHORT).show();
				} else {
					adapter.notifyDataSetChanged();
				}
			} else {
				Toast.makeText(NotificationActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
			}
		}
	}

}
