package com.vphoainha.itfmobile;

import android.app.Activity;
import android.os.Bundle;

import com.vphoainha.itfmobile.view.TouchImageView;

public class PictureViewerActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_viewer);
		
		TouchImageView img_logo=(TouchImageView)findViewById(R.id.img_logo);
		img_logo.setImageBitmap(AttachedPicturesActivity.bm);
		img_logo.setMaxZoom(5f);
	}
	
	@Override
	public void finish() {
		super.finish();
		AttachedPicturesActivity.bm=null;
	}
}
