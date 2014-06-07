package com.vphoainha.itfmobile.util;

import android.graphics.Bitmap;

public class ImageProcessor {
	public static final int MAX_SIZE = 80;  //5000pxs
	public static final int MIN_SIZE = 80;  //5000pxs
	
	public static Bitmap checkAndResizeBitmap(Bitmap bm){
		int w=bm.getWidth();
		if(w>MAX_SIZE)
			w=MAX_SIZE;
		if(w<MIN_SIZE)
			w=MIN_SIZE;
		
		int h=w*bm.getHeight()/bm.getWidth();
		if(h>MAX_SIZE){
			w=w*MAX_SIZE/h;
			h=MAX_SIZE;
		}
		if(h<MIN_SIZE){
			w=w*MIN_SIZE/h;
			h=MIN_SIZE;
		}
		
		return Bitmap.createScaledBitmap(bm, w, h, false);
	}
}
