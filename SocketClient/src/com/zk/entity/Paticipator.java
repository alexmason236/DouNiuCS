package com.zk.entity;

public class Paticipator {
	
	Manager manager=new NetManager(this);
	int myId;
	boolean intoRoom;
	boolean ready;
	String name;
	int roomId=0;
	
	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int connect(String ip,int port) {
		return manager.connect(ip, port);
	}
	
	public Manager getManager() {
		return manager;
	}
	
	public int getMyId() {
		return myId;
	}
	
	public boolean isIntoRoom() {
		return intoRoom;
	}
	
	public boolean isReady() {
		return ready;
	}

	public void play() {
		connect("127.0.0.1", 8888);
		System.out.println(myId);
	}
	public void setIntoRoom(boolean intoRoom) {
		this.intoRoom = intoRoom;
	}
	public void setManager(Manager manager) {
		this.manager = manager;
	}

	public void setMyId(int myId) {
		this.myId = myId;
	}
	
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}

}
