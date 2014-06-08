package com.kpbird.chipsedittextlibrary;

public class ChipsItem {

	private String title;
	public ChipsItem(){
		
	}
	public ChipsItem(String title){
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		return getTitle();
	}
}
