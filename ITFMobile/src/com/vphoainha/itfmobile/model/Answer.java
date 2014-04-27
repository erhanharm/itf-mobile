package com.vphoainha.itfmobile.model;

import java.util.Date;

public class Answer {
	private int id;
	private int questionId;
	private String content;
	private int userId;
	private String userName;
	private int countLiked;
	private int countDisliked;
	private Date time;
	private int isLiked;
	private int isDisliked;
	
	public Answer(){
		id = -1;
		questionId = -1;
		content = "";
		userId = -1;
		userName = "";
		countLiked = 0;
		countDisliked = 0;
		isLiked = 0;
		isDisliked = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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

	public void setIsDisliked(int isUnliked) {
		this.isDisliked = isUnliked;
	}
	
	
}
