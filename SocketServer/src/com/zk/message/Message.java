package com.zk.message;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Message {
	public static final int ROOM_CREATED_MSG = 9;
	public static final int ROOM_ENTED_MSG = 8;
	public static final int ROOM_EXTED_MSG = 7;
	public static final int NOT_FOUND_MSG = 6;
	
	public void send(DatagramSocket ds, String ip, int udpPort,int roomId);
	public void parse(DataInputStream dis);
}
