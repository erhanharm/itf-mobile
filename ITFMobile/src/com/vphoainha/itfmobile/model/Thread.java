package com.vphoainha.itfmobile.model;

import java.io.Serializable;
import java.util.Date;

public class Thread implements Serializable{
	private int id;
	private String title;
	private String content;
	private Date time;
	private int folderId, num_reply, num_view;
	private String folderName;
	private int userId;
	private String userName;
	private int status;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
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
	public int getFolderId() {
		return folderId;
	}
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
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
	public int getNum_reply() {
		return num_reply;
	}
	public void setNum_reply(int num_reply) {
		this.num_reply = num_reply;
	}
	public int getNum_view() {
		return num_view;
	}
	public void setNum_view(int num_view) {
		this.num_view = num_view;
	}

	
	
}
