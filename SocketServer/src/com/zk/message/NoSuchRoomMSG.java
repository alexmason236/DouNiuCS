package com.zk.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class NoSuchRoomMSG implements Message {
	int msgType=Message.NOT_FOUND_MSG;	
	@Override
	public void send(DatagramSocket ds, String ip, int udpPort,int roomId) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(roomId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		DatagramPacket dpSend = new DatagramPacket(buf, buf.length,
				new InetSocketAddress(ip, udpPort));
		try {
			ds.send(dpSend);
			baos.close();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		
	}

}
