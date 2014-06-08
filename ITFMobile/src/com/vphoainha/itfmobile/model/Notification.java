package com.vphoainha.itfmobile.model;

import java.util.Date;

public class Notification {
	private int id;
	private int userId, forUserId;
	private String content;
	private Date time;
	
	public Notification(){
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

	public int getForUserId() {
		return forUserId;
	}

	public void setForUserId(int forUserId) {
		this.forUserId = forUserId;
	}
	
	
}
