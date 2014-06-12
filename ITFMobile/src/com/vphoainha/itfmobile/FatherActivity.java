package com.vphoainha.itfmobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FatherActivity extends Activity{
	protected SharedPreferences sharedPreferences;
	protected SharedPreferences.Editor sharedPreferencesEditor;
	
	protected TextView tvTitle, tvSubTitle;
	protected LinearLayout lnBack;
	protected ImageButton btnOk, btnAddThread, btnReply, btnShare;
		
	protected void initFather() {
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		
		tvTitle=(TextView)findViewById(R.id.tv_title);
		tvSubTitle=(TextView)findViewById(R.id.tv_subtitle);
		lnBack=(LinearLayout)findViewById(R.id.ln_back);
		btnOk = (ImageButton) findViewById(R.id.btn_ok);
		btnAddThread = (ImageButton) findViewById(R.id.btn_add_thread);
		btnReply = (ImageButton) findViewById(R.id.btn_reply);
		btnShare = (ImageButton) findViewById(R.id.btn_share);
		
		lnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tvTitle.setSelected(true);
	}
	
	
}
