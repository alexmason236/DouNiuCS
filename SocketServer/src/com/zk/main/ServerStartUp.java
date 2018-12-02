package com.zk.main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zk.entity.Paticipator;
import com.zk.entity.PlayGround;
import com.zk.message.Message;
import com.zk.message.NoSuchRoomMSG;
import com.zk.message.RoomCreatedMSG;
import com.zk.message.RoomEnteredMSG;
import com.zk.message.RoomExtedMSG;

public class ServerStartUp {

	final int UDP_MESSAGE_PORT = 8881;
	final int UDP_HANDLER_PORT = 8882;
	final int TCP_PORT = 8888;
	int pgIndex = 0;
	List<Paticipator> waitList = new ArrayList<>();
	Map<Integer, PlayGround> pgLists = new HashMap<>();

	public static void main(String[] args) {
		ServerStartUp ssu = new ServerStartUp();
		ssu.tcpStart();
		ssu.udpStart();
		ssu.serverHandlerStartUp();
	}

	class TCPThread implements Runnable {
		ServerSocket ss = null;
		DataOutputStream dos = null;
		DataInputStream dis = null;
		Socket s = null;

		@Override
		public void run() {

			int clientId = 0;
			try {
				ss = new ServerSocket(TCP_PORT);
				while (true) {
					s = ss.accept();
					clientId++;
					dos = new DataOutputStream(s.getOutputStream());
					dis = new DataInputStream(s.getInputStream());
					int udpPort = dis.readInt();
System.out.println(
							"received client udp port:" + udpPort + " ip " + s.getInetAddress().getHostAddress());
					Paticipator gamePlayer = new Paticipator();
					gamePlayer.setIp(s.getInetAddress().getHostAddress());
					gamePlayer.setUdpPort(udpPort);
					gamePlayer.setIntoRoom(false);
					gamePlayer.setReady(false);
					gamePlayer.setId(clientId);
					waitList.add(gamePlayer);
					dos.writeInt(clientId);
					dos.writeUTF("type CREATE to create a new room; type ENTER to enter a room;TYPE QUIT to quit from this room");
					dos.flush();
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					dos.close();
					dis.close();
					s.close();
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class UDPThread implements Runnable {
		byte[] buf = new byte[1024];
		@Override
		public void run() {
			DatagramSocket ds = null;
			int roomId=2;
			try {
				ds = new DatagramSocket(UDP_MESSAGE_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			while(ds != null){
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					for(int i=0; i<pgLists.get(roomId).getPlayers().size(); i++) {
						Paticipator p = pgLists.get(roomId).getPlayers().get(i);
						dp.setSocketAddress(new InetSocketAddress(p.getIp(), p.getUdpPort()));
						ds.send(dp);
					}
System.out.println("a packet received by message_port!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class ServerHandler implements Runnable {

		byte[] buf = new byte[1024];

		@Override
		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_HANDLER_PORT);
System.out.println("UDP thread started at port :" + UDP_HANDLER_PORT);
				while (ds != null) {
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					ds.receive(dp);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
					DataInputStream dis = new DataInputStream(bais);
					int msgType = dis.readInt();
					bais.close();
					dis.close();
//System.out.println(msgType);
					switch (msgType) {
					case 1:
						handleCreate(dis, ds);
System.out.println("create 后房间数量: "+pgLists.size()+" WAIT 人数 "+waitList.size());
						break;
					case 2:
						handleEnter(dis, ds);
						break;
					case 3:
						handleExit(dis, ds);
System.out.println("exit后房间数量: "+pgLists.size()+" WAIT 人数 "+waitList.size());
						break;
					default:
						break;
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	void tcpStart() {
		new Thread(new TCPThread()).start();
	}

	void udpStart() {
		new Thread(new UDPThread()).start();
	}
	
	void serverHandlerStartUp() {
		new Thread(new ServerHandler()).start();
	}
	
	private void handleExit(DataInputStream dis, DatagramSocket ds) {
System.out.println("exit handler");
		int roomId = 0;
		int myId=0;
		try {
			roomId=dis.readInt();
			myId=dis.readInt();
System.out.println("myid: "+myId);			
			PlayGround pg=pgLists.get(roomId);
			Paticipator paticipator=null;
			for(int i=0;i<pg.getPlayers().size();i++) {
				if(pg.getPlayers().get(i).getId()==myId) {
					paticipator=pg.getPlayers().get(i);
					pg.getPlayers().remove(i);
					paticipator.setIntoRoom(false);
					paticipator.setReady(false);
					paticipator.setRoomId(0);
					waitList.add(paticipator);
				}
			}
			if(pg.getPlayers().size()==0) pgLists.remove(roomId);
			
			Message message=new RoomExtedMSG();
			message.send(ds, paticipator.getIp(), paticipator.getUdpPort(), roomId);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void handleEnter(DataInputStream dis, DatagramSocket ds) {
		int myId = 0;
		int roomId = 0;
		try {
			myId = dis.readInt();
			roomId = dis.readInt();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Paticipator paticipator = null;
		for (int i = 0; i < waitList.size(); i++) {
			if (waitList.get(i).getId() == myId) {
				paticipator = waitList.get(i);
				waitList.remove(i);
				break;
			}
		}
		paticipator.setIntoRoom(true);
		paticipator.setReady(false);
		paticipator.setRoomId(roomId);
		if(pgLists.containsKey(roomId)) {
			pgLists.get(roomId).getPlayers().add(paticipator);
System.out.println("此房间人数: "+pgLists.get(roomId).getPlayers().size());
			Message message=new RoomEnteredMSG();
			message.send(ds, paticipator.getIp(), paticipator.getUdpPort(), roomId);
		}else {
			Message message=new NoSuchRoomMSG();
			message.send(ds, paticipator.getIp(), paticipator.getUdpPort(), 0);
		}
	}

	void handleCreate(DataInputStream dis, DatagramSocket ds) {
		int myId = 0;
		try {
			myId = dis.readInt();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pgIndex++;
		Paticipator paticipator = null;
		PlayGround pg = new PlayGround();
		pg.setPgId(pgIndex);
		for (int i = 0; i < waitList.size(); i++) {
			if (waitList.get(i).getId() == myId) {
				paticipator = waitList.get(i);
				waitList.remove(i);
				break;
			}
		}
		paticipator.setIntoRoom(true);
		paticipator.setReady(false);
		paticipator.setRoomId(pgIndex);
		pg.getPlayers().add(paticipator);
		pgLists.put(pgIndex, pg);
		Message message=new RoomCreatedMSG();
		message.send(ds, paticipator.getIp(), paticipator.getUdpPort(), pgIndex);

	}

}
