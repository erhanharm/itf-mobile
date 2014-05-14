package com.vphoainha.itfmobile.model;

import java.io.Serializable;

public class Folder implements Serializable{
	private int id;
	private int parrentId;
	private int folderIndex, num_thread;
	private String name;
	private String note;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParrentId() {
		return parrentId;
	}
	public void setParrentId(int parrentId) {
		this.parrentId = parrentId;
	}
	public int getFolderIndex() {
		return folderIndex;
	}
	public void setFolderIndex(int folderIndex) {
		this.folderIndex = folderIndex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public int getNum_thread() {
		return num_thread;
	}
	public void setNum_thread(int num_thread) {
		this.num_thread = num_thread;
	}
	
}
