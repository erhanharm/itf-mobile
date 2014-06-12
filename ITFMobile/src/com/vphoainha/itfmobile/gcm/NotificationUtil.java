package com.vphoainha.itfmobile.gcm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.util.Log;

import com.vphoainha.itfmobile.NotificationActivity;
import com.vphoainha.itfmobile.R;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class NotificationUtil {
	
	public void sendNotify(Context cxt, int forUserid, String content) {
		new AsyncTask<String, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... params) {
				List<NameValuePair> par = new ArrayList<NameValuePair>();
				par.add(new BasicNameValuePair("content", params[1]));
				par.add(new BasicNameValuePair("user_id", params[2]));
				par.add(new BasicNameValuePair("for_user_id", params[3]));

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
				Log.i("=======sendNotify======", result+"_");
			}
		}.execute(new String[] { WsUrl.URL_ADD_NOTIFICATION, content, AppData.saveUser.getId() + "", forUserid + "" });
	}

	public static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationManager.notify(Utils.md5(message),0, notification);
    }
}
