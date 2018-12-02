package com.zk.entity;

public class Paticipator {
	
	int id;
	boolean intoRoom;
	boolean ready;
	String name;
	int roomId=0;
	String ip;
	int udpPort;
	public int getUdpPort() {
		return udpPort;
	}

	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setIntoRoom(boolean intoRoom) {
		this.intoRoom = intoRoom;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isIntoRoom() {
		return intoRoom;
	}
	
	public boolean isReady() {
		return ready;
	}

	
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}

}
