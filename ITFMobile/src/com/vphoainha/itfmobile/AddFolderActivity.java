package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class AddFolderActivity extends FatherActivity {
	private static final int CHOOSE_PARRENT_FOLDER = 0;

	EditText txtTitle, txtNote;
	Spinner spParrentFolder;

	Context context;
	int parrentFolderId;

	List<String> listParrentFolderOption;
	ArrayAdapter<String> dataParrentFolderOptionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_folder);

		initFather();
		tvTitle.setText("Add folder");
		tvSubTitle.setVisibility(View.GONE);

		txtTitle = (EditText) findViewById(R.id.txtTitle);
		txtNote = (EditText) findViewById(R.id.txtNote);
		spParrentFolder = (Spinner) findViewById(R.id.spParrentFolder);

		btn_ok.setVisibility(View.VISIBLE);
		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = txtTitle.getText().toString().trim();
				if (content.equals("")) {
					Toast.makeText(context, "Please fill question content!", Toast.LENGTH_SHORT).show();
				} else {
					if (!Utils.checkInternetConnection(AddFolderActivity.this))
						Toast.makeText(AddFolderActivity.this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
					else {
						wsAddFolder();
					}
				}
			}
		});

		listParrentFolderOption = new ArrayList<String>();
		listParrentFolderOption.add("Root folder");
		listParrentFolderOption.add("Child folder");
		dataParrentFolderOptionAdapter = new ArrayAdapter<String>(AddFolderActivity.this, android.R.layout.simple_spinner_item, listParrentFolderOption);
		dataParrentFolderOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spParrentFolder.setAdapter(dataParrentFolderOptionAdapter);
		spParrentFolder.setSelection(0);
		spParrentFolder.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
				if (pos == 1 && parrentFolderId == -1) {
					Intent in = new Intent(getApplicationContext(), ChooseFolderActivity.class);
					startActivityForResult(in, CHOOSE_PARRENT_FOLDER);
				} else
					parrentFolderId = -1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		this.context = this;
		parrentFolderId = -1;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CHOOSE_PARRENT_FOLDER) {
			if (resultCode == RESULT_OK) {
				parrentFolderId = data.getExtras().getInt("parrentFolderId");
				listParrentFolderOption.set(1, "Ask an Expert: " + data.getExtras().getString("parrentFolderName"));
				dataParrentFolderOptionAdapter.notifyDataSetChanged();
			} else {
				spParrentFolder.setSelection(0);
				Log.i("==", "123");
			}
		}
	}

	private void wsAddFolder() {
		(new jsAddFolder()).execute(new String[] { WsUrl.URL_ADD_FOLDER, txtTitle.getText().toString(), txtNote.getText().toString(), Integer.toString(parrentFolderId) });
	}

	private class jsAddFolder extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(AddFolderActivity.this);
			pd.setMessage("Adding new folder...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("name", params[1]));
			par.add(new BasicNameValuePair("note", params[2]));
			par.add(new BasicNameValuePair("parrent_id", params[3]));

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
			if (pd != null && pd.isShowing())
				pd.dismiss();

			if (result != null) {
				Toast.makeText(context, "New folder was created!", Toast.LENGTH_SHORT).show();

				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Creating fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
