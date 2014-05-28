package com.vphoainha.itfmobile.model;

import java.io.Serializable;
import java.util.Date;

public class Reply implements Serializable{
	private int id;
	private int threadId;
	private String content, quote;
	private int userId;
	private String userName;
	private Date time;
	private int countLiked;
	private int countDisliked;
	private int isLiked;
	private int isDisliked;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public int getCountLiked() {
		return countLiked;
	}
	public void setCountLiked(int countLiked) {
		this.countLiked = countLiked;
	}
	public int getCountDisliked() {
		return countDisliked;
	}
	public void setCountDisliked(int countDisliked) {
		this.countDisliked = countDisliked;
	}
	public int getIsLiked() {
		return isLiked;
	}
	public void setIsLiked(int isLiked) {
		this.isLiked = isLiked;
	}
	public int getIsDisliked() {
		return isDisliked;
	}
	public void setIsDisliked(int isDisliked) {
		this.isDisliked = isDisliked;
	}
	public String getQuote() {
		return quote;
	}
	public void setQuote(String quote) {
		this.quote = quote;
	}
	
	
}
