package com.vphoainha.itfmobile.model;

import java.io.Serializable;

public class Folder implements Serializable{
	private int id;
	private int parrentId;
	private int folderIndex;
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
	
}
