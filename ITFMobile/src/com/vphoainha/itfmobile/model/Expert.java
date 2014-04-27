package com.vphoainha.itfmobile.model;

public class Expert {
	private int index;
	private int id;
	private String userName;
	private String userEmail;
	private int counterLiked;
	private int counterDisLiked;
	private int counterAsked;
	
	public Expert(){
		index = -1;
		id = -1;
		userName = "";
		userEmail = "";
		counterLiked = 0;
		counterDisLiked=0;
		counterAsked = 0;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public int getCounterLiked() {
		return counterLiked;
	}

	public void setCounterLiked(int counterLiked) {
		this.counterLiked = counterLiked;
	}

	public int getCounterAsked() {
		return counterAsked;
	}

	public void setCounterAsked(int counterAsked) {
		this.counterAsked = counterAsked;
	}

	public int getCounterDisLiked() {
		return counterDisLiked;
	}

	public void setCounterDisLiked(int counterDisLiked) {
		this.counterDisLiked = counterDisLiked;
	}
	
	
}
