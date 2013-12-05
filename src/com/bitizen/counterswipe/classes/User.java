package com.bitizen.counterswipe.classes;

import android.graphics.Color;

public abstract class User {
	private int userID;
	private String username;
	private int teamID;
	private int memberID;
	private Color color;
	private int health;
	private int ammo;
	private Boolean ready;
	
	private int DEFAULT_HEALTH = 3;
	private int DEFAULT_AMMO = 7;
	private int AMMO_USAGE = 1;
	private Boolean IS_NOT_READY = false;
	
	public User (int uid, String uname) {
		this.userID = uid;
		this.username = uname;
		this.health = DEFAULT_HEALTH;
		this.ammo = DEFAULT_AMMO;
		this.ready = IS_NOT_READY;
	}
	
	/*
	 * ACCESSORS
	 */
	
	public void setUserID(int uid) {
		this.userID = uid;
	}
	
	public void setUsername(String uname) {
		this.username = uname;
	}

	public void setTeamID(int tid) {
		this.teamID = tid;
	}
	
	public void setMemberID(int mid) {
		this.memberID = mid;
	}

	public void setColor(Color c) {
		this.color = c;
	}

	public void setReady(Boolean b) {
		this.ready = b;
	}

	public int getUserID() {
		return this.userID;
	}
	
	public String  getUsername() {
		return this.username;
	}

	public int getTeamID() {
		return this.teamID;
	}
	
	public int getMemberID() {
		return this.memberID;
	}

	public Color getColor() {
		return this.color;
	}
	
	public Boolean isReady() {
		return this.ready;
	}
	
	
	/*
	 * IN-GAME METHODS
	 */
	
	public void fire() {
		this.ammo -= AMMO_USAGE;
	}
	
	public void reload() {
		this.ammo = DEFAULT_AMMO;
	}

	public void pause() {
		
	}
	
	public void resume() {
		
	}

	public void replay() {
		
	}
	
	public void quit() {
		
	}
}
