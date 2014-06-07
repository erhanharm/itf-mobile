package com.vphoainha.itfmobile.model;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.vphoainha.itfmobile.adapter.AttachPictureLargeAdapter;
import com.vphoainha.itfmobile.util.ImageService;

public class AttachPicture {
	private int id;
	private String name, fileName;
	private Bitmap bitmap;
	
	private AttachPictureLargeAdapter pa;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public void loadImage(AttachPictureLargeAdapter pa) {
        this.pa = pa;
        if (fileName != null && !fileName.equals("")) {
            new ImageLoadTask().execute(fileName);
        }
    }
 
    private class ImageLoadTask extends AsyncTask<String, String, Bitmap> {
        protected Bitmap doInBackground(String... param) {
            try {
                Bitmap b = ImageService.getBitmapFromURL(param[0]);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                bitmap = ret;
                if (pa != null) pa.notifyDataSetChanged();
            }
        }
    }
}
