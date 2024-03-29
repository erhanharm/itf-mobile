package com.vphoainha.itfmobile.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.provider.Settings.Secure;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class Utils {
	public static String regId="";
	public static int numUnreadNotifications=0;
	
	public static boolean checkInternetConnection(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// test for connection
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {

			return false;
		}
	}
	
	public static String getDeviceID(Context cxt){
		return regId;
//		return Secure.getString(cxt.getContentResolver(),Secure.ANDROID_ID); 
	}
	
	public static void showAlert(Context cxt, String title, String message) {
		showAlert(cxt,title,message,null);
	}
	
    public static void showAlert(Context cxt, String title, String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder bld = new AlertDialog.Builder(cxt);
        if(!title.equals("")) bld.setTitle(title);
        bld.setMessage(message);
        bld.setNeutralButton("OK", onClick);
        bld.create().show();
    }
    
    public static boolean validateEmail(String email) {
    	Pattern pattern;
    	Matcher matcher;
    	String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    	pattern = Pattern.compile(EMAIL_PATTERN);
    	matcher = pattern.matcher(email);
    	return matcher.matches();

    }
    
    public static Drawable getDrawable(Context cxt, String res){
		int id;
		try{
			id=cxt.getResources().getIdentifier(res, "drawable", cxt.getPackageName());
			return cxt.getResources().getDrawable(id);
		}catch (Exception e) {
			return null;
		}
	}
    
    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    public static void setListViewHeightBasedOnChildren(ListView lv, BaseAdapter adapter) {
        int totalHeight = lv.getPaddingTop() + lv.getPaddingBottom();
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, lv);
            if (listItem instanceof ViewGroup)
                listItem.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.height = totalHeight + (lv.getDividerHeight() * (adapter.getCount() - 1));
        lv.setLayoutParams(params);
    }
}
