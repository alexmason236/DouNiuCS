package com.zk.message;

import java.io.DataInputStream;
import java.net.DatagramSocket;

public interface Message {
	public static final int CREATE_ROOM_MSG = 1;
	public static final int INTO_ROOM_MSG = 2;
	public static final int EXT_ROOM_MSG = 3;
	public static final int PLAYER_READY_MSG = 4;
	public static final int A_PLAYER_ENTED_MSG = 5;
	public static final int A_PLAYER_EXIT_MSG = 11;
	
	public void send(DatagramSocket ds, String ip, int udpPort);
	public void parse(DataInputStream dis);
}
