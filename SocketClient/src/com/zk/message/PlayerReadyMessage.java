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

public class PlayerReadyMessage implements Message {
	
	int msgType = Message.PLAYER_READY_MSG;
	Paticipator paticipator;
	
	public PlayerReadyMessage(Paticipator paticipator) {
		super();
		this.paticipator = paticipator;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
System.out.println("send ready msg");
			dos.writeInt(msgType);
			dos.writeInt(paticipator.getRoomId());
			dos.writeInt(paticipator.getMyId());
			dos.writeUTF(paticipator.getName());
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
		try {
			int msgType=dis.readInt();
			int roomId=dis.readInt();
			int myId=dis.readInt();
			String name=dis.readUTF();
			if(paticipator.getMyId()!=myId) {
				System.out.println("player "+name+" ready");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
