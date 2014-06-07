package com.vphoainha.itfmobile;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vphoainha.itfmobile.adapter.AttachPictureLargeAdapter;
import com.vphoainha.itfmobile.model.AttachPicture;
import com.vphoainha.itfmobile.util.WsUrl;

public class AttachedPicturesActivity extends FatherActivity {
	private List<AttachPicture> attachPictures;
	public static Bitmap bm;
	ListView lvAttach;

	AttachPictureLargeAdapter attachPictureLargeAdapter;
	
	String picture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_attached_pictures);

		initFather();
		tvTitle.setText("Attached pictures");

		picture=getIntent().getStringExtra("pictures");
		
		initAttachPicList();
	}

	private void initAttachPicList() {
		lvAttach = (ListView) findViewById(R.id.lvAttach);
		
		String[] pictures=picture.split(";");
		
		attachPictures=new ArrayList<AttachPicture>();
		for(int i=0;i<pictures.length;i++){
			AttachPicture attachPicture=new AttachPicture();
			String path=WsUrl.URL+"uploads/"+pictures[i];
			attachPicture.setFileName(path);
			attachPicture.setName(pictures[i]);
			attachPictures.add(attachPicture);
		}
		attachPictureLargeAdapter = new AttachPictureLargeAdapter(AttachedPicturesActivity.this, R.layout.list_item_attachpic, R.id.tvId, attachPictures);
		lvAttach.setAdapter(attachPictureLargeAdapter);
		lvAttach.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				bm=attachPictures.get(arg2).getBitmap();
				Intent i=new Intent(AttachedPicturesActivity.this, PictureViewerActivity.class);
				startActivity(i);
			}
		});
		
		for (AttachPicture p : attachPictures) {
            p.loadImage(attachPictureLargeAdapter);
		}
	}

}
