package com.vphoainha.itfmobile;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FatherActivity extends Activity{
	protected SharedPreferences sharedPreferences;
	protected SharedPreferences.Editor sharedPreferencesEditor;
	
	protected TextView tvTitle;
	protected LinearLayout lnBack;
		
	protected void initFather() {
		sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
		
		tvTitle=(TextView)findViewById(R.id.tv_title);
		lnBack=(LinearLayout)findViewById(R.id.ln_back);
		
		lnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	
}
