package com.vphoainha.itfmobile.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vphoainha.itfmobile.model.User;

public class MySharedPreferences {

	SharedPreferences.Editor editor;
	SharedPreferences myShare;

	public MySharedPreferences(Context context) {
		myShare=PreferenceManager.getDefaultSharedPreferences(context);
		editor = myShare.edit();
	}
	
	public void getSaveUserPreferences() {
		Utils.isLogin=myShare.getBoolean("isLogin", false);
		
		if(Utils.isLogin){
			Utils.saveUser=new User();
			Utils.saveUser.setId(myShare.getInt("userId", -1));
			Utils.saveUser.setName(myShare.getString("userName", ""));
			Utils.saveUser.setEmail(myShare.getString("userEmail", ""));
			Utils.saveUser.setDeviceId(myShare.getString("deviceId", ""));
			Utils.saveUser.setPassword(myShare.getString("userPassword", ""));
			Utils.saveUser.setIsAnonymous( myShare.getInt("userIsAnonymous", 0));
		}
		else Utils.saveUser=null;
	}
	
	public void setSaveUserPreferences() {
		editor.putBoolean("isLogin", Utils.isLogin);
	
		if(Utils.isLogin) {
			editor.putInt("userId", Utils.saveUser.getId());
			editor.putString("userName", Utils.saveUser.getName());
			editor.putString("userEmail", Utils.saveUser.getEmail());
			editor.putString("userPassword", Utils.saveUser.getPassword());
			editor.putString("deviceId", Utils.saveUser.getDeviceId());
			editor.putInt("userIsAnonymous", Utils.saveUser.getIsAnonymous());
		}
		
		editor.commit();
	}
}
