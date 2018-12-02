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

public class APlayerExitMessage implements Message {
	
	int msgType=Message.A_PLAYER_EXIT_MSG;
	Paticipator paticipator;
	int roomId;
	
	public APlayerExitMessage(Paticipator paticipator) {
		this.paticipator=paticipator;
	}
	
	public APlayerExitMessage(Paticipator paticipator, int roomId) {
		super();
		this.paticipator = paticipator;
		this.roomId = roomId;
	}

	public int getMsgType() {
		return msgType;
	}

	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(roomId);
			dos.writeInt(paticipator.getMyId());
			dos.writeUTF(paticipator.getName());
			dos.writeBoolean(paticipator.isReady());
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
			boolean ready=dis.readBoolean();

			if(paticipator.getMyId()!=myId) {
				System.out.println("player "+name+" exit this room");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
