package com.vphoainha.itfmobile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.kpbird.chipsedittextlibrary.ChipsAdapter;
import com.kpbird.chipsedittextlibrary.ChipsItem;
import com.kpbird.chipsedittextlibrary.ChipsMultiAutoCompleteTextview;
import com.vphoainha.itfmobile.adapter.AttachPictureAdapter;
import com.vphoainha.itfmobile.adapter.AttachPictureAdapter.OnClickRemove;
import com.vphoainha.itfmobile.gcm.NotificationUtil;
import com.vphoainha.itfmobile.jsonparser.JSONParser;
import com.vphoainha.itfmobile.model.AttachPicture;
import com.vphoainha.itfmobile.model.Folder;
import com.vphoainha.itfmobile.model.Tag;
import com.vphoainha.itfmobile.model.Thread;
import com.vphoainha.itfmobile.model.User;
import com.vphoainha.itfmobile.util.AppData;
import com.vphoainha.itfmobile.util.JsonTag;
import com.vphoainha.itfmobile.util.UploadPicture;
import com.vphoainha.itfmobile.util.Utils;
import com.vphoainha.itfmobile.util.WsUrl;

public class AddThreadActivity extends FatherActivity {
	private static final int ACTIVITY_SELECT_IMAGE = 1889;

	EditText txtContent, txtTitle;
	ChipsMultiAutoCompleteTextview txtKeyword;
	ListView lvAttach;

	private List<Tag> tags;
	private List<AttachPicture> attachPictures;

	List<String> listAskOption;
	ArrayAdapter<String> dataAskOptionAdapter;
	AttachPictureAdapter attachPictureAdapter;

	Context context;
	private int mode = 1;
	private Thread curThread;

	private ProgressDialog dialog = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_thread);
		initFather();

		txtContent = (EditText) findViewById(R.id.txtContent);
		txtTitle = (EditText) findViewById(R.id.txtTitle);
		txtKeyword = (ChipsMultiAutoCompleteTextview) findViewById(R.id.txtKeyword);

		mode = getIntent().getIntExtra("mode", 1);
		if (mode == 1)
			tvTitle.setText("Post a new thread");
		else {
			curThread = (Thread) getIntent().getSerializableExtra("thread");
			tvTitle.setText("Edit thread");
			txtTitle.setEnabled(false);

			txtTitle.setText(curThread.getTitle());
			txtContent.setText(curThread.getContent());
		}

		tvSubTitle.setVisibility(View.GONE);
		btnOk.setVisibility(View.VISIBLE);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = txtContent.getText().toString().trim();
				String title = txtTitle.getText().toString().trim();
				if (title.equals("")) {
					Toast.makeText(context, "Please fill title of this thread!", Toast.LENGTH_SHORT).show();
				} else if (content.equals("")) {
					Toast.makeText(context, "Please fill content of this thread!", Toast.LENGTH_SHORT).show();
				} else if (!Utils.checkInternetConnection(AddThreadActivity.this)) {
					Toast.makeText(AddThreadActivity.this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
				} else {
					String tag = txtKeyword.getText().toString();
					tag = tag.replace(",", ", ");
					tag = tag.replaceAll("\\s+", " ");
					tag = tag.replace(" ,", ",").trim();
					if (tag.length() > 0 && tag.charAt(tag.length() - 1) != ',')
						tag += ',';

					String pictures = "";
					for (AttachPicture attachPicture : attachPictures)
						pictures += attachPicture.getName() + ";";
					if (pictures.length() > 0) {
						pictures += "_";
						pictures = pictures.replace(";_", "");
					}

					if (mode == 1) {
						Folder f = AppData.folders.get(AppData.folders.size() - 1);
						wsAddThread(f.getId(), title, content, tag, pictures);
					} else
						wsEditThread();
				}
			}
		});

		tags = new ArrayList<Tag>();
		this.context = this;

		wsGetKeywords();
		initAttachPicList();
	}

	public void onClickTagInfo(View v) {
		Utils.showAlert(this, "Tag tag", "You can tag your question with some tag split by , character. Type some things and select tag from dropdown or use , for make a new tag.");
	}

	private void initAttachPicList() {
		lvAttach = (ListView) findViewById(R.id.lvAttach);
		attachPictures = new ArrayList<AttachPicture>();
		attachPictureAdapter = new AttachPictureAdapter(AddThreadActivity.this, R.layout.list_item_attachpic, R.id.tvId, attachPictures);
		lvAttach.setAdapter(attachPictureAdapter);
		attachPictureAdapter.setOnClickRemoveListener(new OnClickRemove() {
			@Override
			public void onClickRemove(int pos) {
				attachPictures.remove(pos);
				attachPictureAdapter.notifyDataSetChanged();
			}
		});
	}

	public void onClickAddAttach(View v) {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			File file = new File(picturePath);
			long length = file.length();
			if (length / 1024.0 / 1024 < 2) {
				AttachPicture attachPicture = new AttachPicture();
				attachPicture.setFileName(picturePath);

				int j = picturePath.lastIndexOf(".");
				String extension = picturePath.substring(j + 1);
				attachPicture.setName(new Date().getTime() + "." + extension);
				attachPicture.setBitmap(BitmapFactory.decodeFile(picturePath));

				for (AttachPicture a : attachPictures)
					if (a.getFileName().equals(attachPicture.getFileName())) {
						Toast.makeText(context, "Duplicated!", Toast.LENGTH_SHORT).show();
						return;
					}
				attachPictures.add(attachPicture);
				attachPictureAdapter.notifyDataSetChanged();
				Utils.setListViewHeightBasedOnChildren(lvAttach, attachPictureAdapter);
			} else {
				Utils.showAlert(this, "", "Sorry! The picture size is over 2MB. Please select another picture!");
			}
		}
	}

	public void wsAddThread(final int folderId, final String title, final String content, final String tags, final String pictures) {

		if (attachPictures.size() > 0) {
			dialog = ProgressDialog.show(AddThreadActivity.this, "", "Uploading screenshots...", true);
			// messageText.setText("uploading started.....");
			new java.lang.Thread(new Runnable() {
				int i;

				public void run() {
					for (i = 0; i < attachPictures.size(); i++) {
						runOnUiThread(new Runnable() {
							public void run() {
								dialog.setMessage("Uploading screenshots (" + (i + 1) + "/" + attachPictures.size() + ")...");
							}
						});

						try {
							UploadPicture.uploadFile(WsUrl.URL_UPLOAD_IMAGE, attachPictures.get(i).getFileName(), attachPictures.get(i).getName());
						} catch (Exception e) {
						}
					}
					dialog.dismiss();

					runOnUiThread(new Runnable() {
						public void run() {
							(new jsAddThread()).execute(new String[] { WsUrl.URL_ADD_THREAD, title, content, Integer.toString(folderId), Integer.toString(AppData.saveUser.getId()), tags, pictures });
						}
					});
				}
			}).start();
		} else
			(new jsAddThread()).execute(new String[] { WsUrl.URL_ADD_THREAD, title, content, Integer.toString(folderId), Integer.toString(AppData.saveUser.getId()), tags, pictures });
	}

	public class jsAddThread extends AsyncTask<String, Void, String> {
		ProgressDialog pd;
		String[] params;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(AddThreadActivity.this);
			pd.setMessage("Posting...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			this.params=params;
			
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("title", params[1]));
			par.add(new BasicNameValuePair("content", params[2]));
			par.add(new BasicNameValuePair("folder_id", params[3]));
			par.add(new BasicNameValuePair("user_id", params[4]));
			par.add(new BasicNameValuePair("tags", params[5]));
			par.add(new BasicNameValuePair("pictures", params[6]));

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
				Toast.makeText(context, "Your thread was posted!", Toast.LENGTH_SHORT).show();

				if(AppData.saveUser.getUserType()==User.USER_ADMIN){
					Folder f = AppData.folders.get(AppData.folders.size() - 1);
					String content=AppData.saveUser.getName()+" posted new thread: \""+params[1]+"\" in folder: \""+f.getName()+"\"";
					
					//send to all user by admin
					for(User user:AppData.users)
						if(user.getId()!=AppData.saveUser.getId())
							NotificationUtil.sendNotify(AddThreadActivity.this, user.getId(), content);
				}
				
				setResult(RESULT_OK);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Posted fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void wsEditThread() {
		String content = txtContent.getText().toString().trim();
		String title = txtTitle.getText().toString().trim();
		if (content.equals("")) {
			Toast.makeText(context, "Please fill content of this thread!", Toast.LENGTH_SHORT).show();
		} else {
			if (!Utils.checkInternetConnection(this))
				Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
			else {
				(new jsEditThread()).execute(new String[] { WsUrl.URL_EDIT_THREAD, title, content, Integer.toString(1), Integer.toString(curThread.getId()) });
			}
		}
	}

	public class jsEditThread extends AsyncTask<String, Void, String> {
		ProgressDialog pd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(AddThreadActivity.this);
			pd.setMessage("Saving...");
			pd.setCancelable(false);
			pd.show();
		}

		@Override
		protected String doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();
			par.add(new BasicNameValuePair("title", params[1]));
			par.add(new BasicNameValuePair("content", params[2]));
			par.add(new BasicNameValuePair("status", params[3]));
			par.add(new BasicNameValuePair("thread_id", params[4]));

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
				Toast.makeText(context, "Your thread was saved!", Toast.LENGTH_SHORT).show();

				Intent in = new Intent();
				in.putExtra("content", txtContent.getText().toString());
				setResult(RESULT_OK, in);
				finish();
			} else {
				Toast.makeText(context, "Sorry! Saved fail, try a again later!", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void wsGetKeywords() {
		if (!Utils.checkInternetConnection(this))
			Toast.makeText(this, getString(R.string.cant_connect_internet), Toast.LENGTH_SHORT).show();
		else
			(new jsGetKeywords()).execute(new String[] { WsUrl.URL_GET_TAGS });
	}

	private class jsGetKeywords extends AsyncTask<String, Void, Integer> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Integer doInBackground(String... params) {
			List<NameValuePair> par = new ArrayList<NameValuePair>();

			JSONParser jsonParser = new JSONParser();
			JSONObject json = jsonParser.makeHttpRequest(params[0], "POST", par);

			try {
				int success = json.getInt(JsonTag.TAG_SUCCESS);
				if (success == 1) {
					JSONArray array = json.getJSONArray(JsonTag.TAG_TAGS);

					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);

						Tag tag = new Tag();
						tag.setId(Integer.parseInt(obj.getString(JsonTag.TAG_ID)));
						tag.setName(obj.getString(JsonTag.TAG_NAME));

						tags.add(tag);
					}
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
			// tag suggestion
			ArrayList<ChipsItem> arrCountry = new ArrayList<ChipsItem>();

			for (int i = 0; i < tags.size(); i++) {
				arrCountry.add(new ChipsItem(tags.get(i).getName()));
			}

			ChipsAdapter chipsAdapter = new ChipsAdapter(AddThreadActivity.this, arrCountry);
			txtKeyword.setAdapter(chipsAdapter);
		}
	}
}
