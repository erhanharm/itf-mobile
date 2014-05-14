package com.vphoainha.itfmobile.util;


import java.util.Date;

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
		AppData.isLogin=myShare.getBoolean("isLogin", false);
		
		if(AppData.isLogin){
			AppData.saveUser=new User();
			AppData.saveUser.setId(myShare.getInt("id", -1));
			AppData.saveUser.setUsername(myShare.getString("username", ""));
			AppData.saveUser.setName(myShare.getString("name", ""));
			AppData.saveUser.setEmail(myShare.getString("email", ""));
			AppData.saveUser.setDeviceId(myShare.getString("device_id", ""));
			AppData.saveUser.setPassword(myShare.getString("password", ""));
			AppData.saveUser.setBirthday(new Date(myShare.getLong("birthday", 0)));
			AppData.saveUser.setJoinDate(new Date(myShare.getLong("join_date", 0)));
			AppData.saveUser.setUserClass(myShare.getString("class", ""));
			AppData.saveUser.setAddress(myShare.getString("address", ""));
			AppData.saveUser.setInterest(myShare.getString("interest", ""));
			AppData.saveUser.setSignature(myShare.getString("signature", ""));
			AppData.saveUser.setUserType(myShare.getInt("user_type", 0));
		}
		else AppData.saveUser=null;
	}
	
	public void setSaveUserPreferences() {
		editor.putBoolean("isLogin", AppData.isLogin);
	
		if(AppData.isLogin) {
			editor.putInt("id", AppData.saveUser.getId());
			editor.putString("username", AppData.saveUser.getUsername());
			editor.putString("name", AppData.saveUser.getName());
			editor.putString("email", AppData.saveUser.getEmail());
			editor.putString("device_id", AppData.saveUser.getDeviceId());
			editor.putString("password", AppData.saveUser.getPassword());
			editor.putLong("birthday", AppData.saveUser.getBirthday().getTime());
			editor.putLong("join_date", AppData.saveUser.getJoinDate().getTime());
			editor.putString("class", AppData.saveUser.getUserClass());
			editor.putString("address", AppData.saveUser.getAddress());
			editor.putString("interest", AppData.saveUser.getInterest());
			editor.putString("signature", AppData.saveUser.getSignature());
			editor.putInt("user_type", AppData.saveUser.getUserType());
		}
		
		editor.commit();
	}
}
