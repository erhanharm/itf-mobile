package com.vphoainha.itfmobile.model;

import java.util.Date;

public class Notification {
	private int id;
	private int userId;
	private String content;
	private Date time;
	
	public Notification(){
		id = -1;
		userId = -1;
		content = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}
