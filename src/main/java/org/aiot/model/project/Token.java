package org.aiot.model.project;

import java.util.Date;

public class Token {
	private String user;
	private Date expiry;

	public Token(){

	}

	public Token(String user){
		this.user = user;
		setTimeoutMinute(2*60);
	}

	public Token(String user, int minute){
		this.user = user;
		setTimeoutMinute(minute);
	}

	public void setTimeoutMinute(int minute){
		this.expiry = new Date(System.currentTimeMillis() + (long) minute *60*1000);
	}

	public boolean isTimeout(){
		return new Date().after(expiry);
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
}
