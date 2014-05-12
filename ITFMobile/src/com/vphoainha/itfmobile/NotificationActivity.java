package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.Notification;
import com.vphoainha.itfmobile.util.DateTimeHelper;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class NotificationActivity extends Activity{	
	private LayoutInflater mInflater;
	private List<Notification> listData;
	
	JSONParser jsonParser;
	public Context context;
	ListView listView;
	ArrayList<Map<String, String>> List;
	String message;
	JSONArray array;
	int from;
	boolean isMaximum;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_notification);
		
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("All Notifications");
		LinearLayout ln_back = (LinearLayout) findViewById(R.id.ln_back);
		ln_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		jsonParser = new JSONParser();
		message = "";
		isMaximum = false;
		from = 1;
		listData = new ArrayList<Notification>();
		this.context = this;
		accessWebservice();
	}
	
	public void accessWebservice() {
		(new JsonReadTask()).execute(new String[] {
				WsUrl.URL_GET_NOTIFICATIONS, Integer.toString(Utils.saveUser.getId()),
				Integer.toString(from), Integer.toString(from+=10) });
	}
	
	public void onclickMenu(){
		this.finish();
	}

	/**
	 * @author Yoyo
	 * 
	 */
	public class JsonReadTask extends AsyncTask<String, Void, String> {
		
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd=new ProgressDialog(NotificationActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("userId", params[1]));
			par.add(new BasicNameValuePair("from", params[2]));
			par.add(new BasicNameValuePair("to", params[3]));

			JSONObject json = jsonParser
					.makeHttpRequest(params[0], "POST", par);
			Log.d("Create Response", json.toString());

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				message = json.getString(JsonTag.TAG_MESSAGE);
				if (success == 1) {
					array = json.getJSONArray(JsonTag.TAG_REPLIES);
					if(array.length()== 0){
						isMaximum = true;
					}else{
						from++;
					}

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						
						Notification notification = new Notification();
						notification.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						notification.setUserId(Integer.parseInt(obj.getString(JsonTag.TAG_USER_ID)));
						notification.setContent(obj.getString(JsonTag.TAG_CONTENT));
						notification.setTime(DateTimeHelper.stringToDateTime(obj.getString(JsonTag.TAG_TIME)));
						
						listData.add(notification);
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
			if(pd!=null && pd.isShowing())  pd.dismiss();
			
			if (result != null) {
				if(message.contains("No notifications found")){
					Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
				}
				else{
					runOnUiThread(new Runnable() {
						public void run() {
							mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					
							ListView list = (ListView)findViewById(R.id.lv_notification);
							CustomAdapter adapter = new CustomAdapter(getApplicationContext(),R.layout.list_item_notification, R.id.tv_name_notification, listData);
							list.setAdapter(adapter);
						}
					});
				}
			} 
			else {
				Toast.makeText(context, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class CustomAdapter extends ArrayAdapter<Notification> {
		public CustomAdapter(Context context, int resource,
				int textViewResourceId, List<Notification> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			TextView title,date;
			
			Notification notification = getItem(position);
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.list_item_notification, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			holder = (ViewHolder) convertView.getTag();

			title = holder.getTitle();
			title.setText(notification.getContent());

			date = holder.getDate();
			date.setText(DateTimeHelper.dateTimeToDateStringDMY(notification.getTime()));
			
			if (position >= (listData.size() - 1) && !isMaximum) {
				Log.v("thuy yoyo", "maximum");
				accessWebservice();
			}

			return convertView;
		}

		private class ViewHolder {
			private View mRow;
			private TextView title,date;
			
			public ViewHolder(View row) {
				mRow = row;
			}

			public TextView getTitle() {
				if (null == title) {
					title = (TextView) mRow.findViewById(R.id.tv_name_notification);
				}
				return title;
			}

			public TextView getDate() {
				if (null == date) {
					date = (TextView) mRow.findViewById(R.id.tv_time);
				}
				return date;
			}
		}
	}
	}
