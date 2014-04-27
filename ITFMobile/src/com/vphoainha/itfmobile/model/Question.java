package com.vphoainha.itfmobile.model;

import java.io.Serializable;
import java.util.Date;

public class Question implements Serializable{
	private int id;
	private int forUserId;
	private String forUserName;
	private String content;
	private Date time;
	private int categoryId;
	private String categoryName;
	private int userId;
	private int isAnswered;
	private String userName;
	private int saveUserId;
	
	public Question(){
		id = -1;
		forUserId = -1;
		forUserName = "";
		content = "";
		categoryId = -1;
		categoryName = "Anonymous";
		userId = -1;
		isAnswered = 0;
		userName = "";
		saveUserId=-1;
	}
	
	public Question(int id, int forUserId, String forUserName, String content, int categoryId, String categoryName, int userId, String userName, int isAnswered, Date time){
		this.id = id;
		this.forUserId = forUserId;
		this.forUserName = forUserName;
		this.content = content;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.userId = userId;
		this.userName = userName;
		this.isAnswered = isAnswered;
		this.time = time;
	}

	public String getForUserName() {
		return forUserName;
	}

	public void setForUserName(String forUserName) {
		this.forUserName = forUserName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getForUserId() {
		return forUserId;
	}

	public void setForUserId(int forUserId) {
		this.forUserId = forUserId;
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

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getIsAnswered() {
		return isAnswered;
	}

	public void setIsAnswered(int isAnswered) {
		this.isAnswered = isAnswered;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getSaveUserId() {
		return saveUserId;
	}

	public void setSaveUserId(int saveUserId) {
		this.saveUserId = saveUserId;
	}	
	
	
}
