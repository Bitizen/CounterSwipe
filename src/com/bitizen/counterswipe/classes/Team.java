package com.bitizen.counterswipe.classes;

import java.util.ArrayList;

import android.graphics.Color;

public class Team {
	private ArrayList<User> members;
	private Status status;
	private Color color;
	private Boolean isVictor;
	
	private final Boolean IS_NOT_VICTOR = false;
	private final int MAX_MEMBERS = 3;
	
	public Team() {
		this.members = new ArrayList<User>();
		this.status = Status.IDLE;
		this.isVictor = IS_NOT_VICTOR;
	}
	
	public void addMember(User user) {
		if (members.size() < MAX_MEMBERS
				|| !members.contains(user)) {
			members.add(user);
		}
	}
	
	public void removeMember(User user) {
		if (members.contains(user)) {
			members.remove(user);
		}
	}
	
	public void setStatus(Status s) {
		this.status = s;
	}
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public void setVictor(Boolean b) {
		this.isVictor = b;
	}
	
	public ArrayList<User> getMembers() {
		return this.members;
	}
	
	public Status getStatus() {
		return this.status;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public Boolean isVictor() {
		return this.isVictor;
	}
}
