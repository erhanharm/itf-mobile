package com.vphoainha.itfmobile.util;

public class FormatFilter {

	private final static String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	
	public static boolean isEmail(String email){
		return email.matches(EMAIL_REGEX);
	}
}
