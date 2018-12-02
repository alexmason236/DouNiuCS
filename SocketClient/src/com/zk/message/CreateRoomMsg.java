package com.zk.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.zk.entity.Paticipator;

public class CreateRoomMsg implements Message{
	
	int msgType=Message.CREATE_ROOM_MSG;
	Paticipator paticipator;
	
	public CreateRoomMsg(Paticipator paticipator) {
		super();
		this.paticipator = paticipator;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(paticipator.getMyId());
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf = baos.toByteArray();
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, new InetSocketAddress(ip, udpPort));
			ds.send(dp);
		}catch (SocketException e) {
			e.printStackTrace(); 
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void parse(DataInputStream dis) {
		
	}

}
