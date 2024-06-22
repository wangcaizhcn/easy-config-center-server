package com.github.wangcaizhcn.config.auth;

import java.io.Serializable;

public class User implements Serializable {
	
	private static final long serialVersionUID = 4769394279123862966L;
	
	private String username;
	private String password;
	private String type;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
