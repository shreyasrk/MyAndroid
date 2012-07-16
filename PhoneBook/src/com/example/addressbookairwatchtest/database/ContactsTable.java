package com.example.addressbookairwatchtest.database;

public class ContactsTable {
	private long id;
	private String userName;
	private String userNumber;
	private String userGroup;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUser() {
		return userName;
	}
	
	public String getNumber() {
		return userNumber;
	}
	
	public String getGroup(){
		return userGroup;
	}

	public void setUser(String user) {
		this.userName = user;
	}
	
	public void setNumber(String number) {
		this.userNumber = number;
	}
	
	public void setGroup(String group){
		this.userGroup = group;
	}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return userName + " - " + userNumber + " (" + userGroup + ")";
	}

	

}
