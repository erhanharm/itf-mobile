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
		Utils.isLogin=myShare.getBoolean("isLogin", false);
		
		if(Utils.isLogin){
			Utils.saveUser=new User();
			Utils.saveUser.setId(myShare.getInt("id", -1));
			Utils.saveUser.setUsername(myShare.getString("username", ""));
			Utils.saveUser.setName(myShare.getString("name", ""));
			Utils.saveUser.setEmail(myShare.getString("email", ""));
			Utils.saveUser.setDeviceId(myShare.getString("device_id", ""));
			Utils.saveUser.setPassword(myShare.getString("password", ""));
			Utils.saveUser.setBirthday(new Date(myShare.getLong("birthday", 0)));
			Utils.saveUser.setJoinDate(new Date(myShare.getLong("join_date", 0)));
			Utils.saveUser.setUserClass(myShare.getString("class", ""));
			Utils.saveUser.setAddress(myShare.getString("address", ""));
			Utils.saveUser.setInterest(myShare.getString("interest", ""));
			Utils.saveUser.setSignature(myShare.getString("signature", ""));
			Utils.saveUser.setUserType(myShare.getInt("user_type", 0));
		}
		else Utils.saveUser=null;
	}
	
	public void setSaveUserPreferences() {
		editor.putBoolean("isLogin", Utils.isLogin);
	
		if(Utils.isLogin) {
			editor.putInt("id", Utils.saveUser.getId());
			editor.putString("username", Utils.saveUser.getUsername());
			editor.putString("name", Utils.saveUser.getName());
			editor.putString("email", Utils.saveUser.getEmail());
			editor.putString("device_id", Utils.saveUser.getDeviceId());
			editor.putString("password", Utils.saveUser.getPassword());
			editor.putLong("birthday", Utils.saveUser.getBirthday().getTime());
			editor.putLong("join_date", Utils.saveUser.getJoinDate().getTime());
			editor.putString("class", Utils.saveUser.getUserClass());
			editor.putString("address", Utils.saveUser.getAddress());
			editor.putString("interest", Utils.saveUser.getInterest());
			editor.putString("signature", Utils.saveUser.getSignature());
			editor.putInt("user_type", Utils.saveUser.getUserType());
		}
		
		editor.commit();
	}
}
