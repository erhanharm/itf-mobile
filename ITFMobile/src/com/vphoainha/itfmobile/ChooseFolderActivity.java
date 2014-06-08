package com.vphoainha.itfmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.vphoainha.itfmobile.adapter.ChooseFolderAdapter;
import com.vphoainha.itfmobile.util.AppData;

public class ChooseFolderActivity extends FatherActivity {
	String msg;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_folder);

		initFather();
		tvTitle.setText("Choose an Folder");
		tvSubTitle.setVisibility(View.GONE);

		ListView list = (ListView) findViewById(R.id.lv_notification);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				Intent intent = new Intent();
				intent.putExtra("parrentFolderId", AppData.allFolders.get(position).getId());
				intent.putExtra("parrentFolderName", AppData.allFolders.get(position).getName());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});

		ChooseFolderAdapter adapter = new ChooseFolderAdapter(ChooseFolderActivity.this, R.layout.list_item_expert, R.id.tvId, AppData.allFolders);
		list.setAdapter(adapter);
	}
}
