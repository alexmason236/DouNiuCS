package com.zk.entity;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.zk.message.APlayerEntedMessage;
import com.zk.message.APlayerExitMessage;
import com.zk.message.CreateRoomMsg;
import com.zk.message.EnterRoomMessage;
import com.zk.message.ExitRoomMessage;
import com.zk.message.Message;
import com.zk.message.PlayerReadyMessage;

public class NetManager implements Manager {

	Paticipator paticipator;

	public NetManager(Paticipator paticipator) {
		this.paticipator = paticipator;
	}

	final int Server_MESSAGE_PORT = 8881;
	final int Server_HANDLER_PORT = 8882;
	int udpHandlerPort = 2229;
	Socket s = null;
	DatagramSocket ds = null;
	DataInputStream dis = null;
	DataOutputStream dos = null;
	List<Paticipator> players = new ArrayList<>();

	@Override
	public int connect(String ip, int port) {
		int clientId = 0;
		try {
			ds = new DatagramSocket(udpHandlerPort);
			s = new Socket(ip, port);
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			dos.writeInt(udpHandlerPort);
			dos.flush();
			clientId = dis.readInt();
			paticipator.setMyId(clientId);
			paticipator.setIntoRoom(false);
			paticipator.setReady(false);
			String noticeStr = dis.readUTF();
			System.out.println(noticeStr);
			System.out.println("AT FIRST ,PLEASE INPUT YOUR NAME!");
			new Thread(new PrintListener()).start();
			new Thread(new UDPListener()).start();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dos.close();
				dis.close();
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return clientId;
	}

	class PrintListener implements Runnable {

		@Override
		public void run() {
			@SuppressWarnings("resource")
			Scanner dis = new Scanner(System.in);
			while (true) {
				String infoStr = dis.nextLine();
				if (paticipator.getName()==null) {
					paticipator.setName(infoStr);
				} else if (infoStr.equals("CREATE") && !paticipator.isIntoRoom()) {
					Message message = new CreateRoomMsg(paticipator);
					sendHandlerPort(message);
					paticipator.setIntoRoom(true);
				} else if (infoStr.equals("ENTER") && !paticipator.isIntoRoom()) {
					System.out.println("please type a room number");
					String roomNo = dis.nextLine();
					Message message = new EnterRoomMessage(paticipator, Integer.parseInt(roomNo));
					sendHandlerPort(message);
					Message message2=new APlayerEntedMessage(paticipator, Integer.parseInt(roomNo));
					sendMessagePort(message2);
				}else if (infoStr.equals("QUIT") && paticipator.isIntoRoom()) {
					Message message2=new APlayerExitMessage(paticipator,paticipator.getRoomId());
					sendMessagePort(message2);
					Message message=new ExitRoomMessage(paticipator);
					sendHandlerPort(message);
					
				}else if (infoStr.equals("READY") && paticipator.isIntoRoom()) {
					Message message=new PlayerReadyMessage(paticipator);
					sendMessagePort(message);
				}else {
					if (infoStr.equals(""))
						;
					else {
						System.out.println("check your input");
					}
				}
			}
		}

		 private void sendMessagePort(Message message) {
		 message.send(ds, "127.0.0.1", Server_MESSAGE_PORT);
		 }
		private void sendHandlerPort(Message message) {
			message.send(ds, "127.0.0.1", Server_HANDLER_PORT);
		}

	}

	class UDPListener implements Runnable {

		byte[] buf = new byte[1024];

		@Override
		public void run() {
			try {
				while (ds != null) {
					DatagramPacket dp = new DatagramPacket(buf, buf.length);
					ds.receive(dp);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
					DataInputStream dis = new DataInputStream(bais);
					int msgType = dis.readInt();
					switch (msgType) {
					case 11:
						parse(dp, msgType);
						break;
					case 9:
						int roomId=dis.readInt();
System.out.println("create room : "+roomId);
						paticipator.setRoomId(roomId);
						break;
					case 8:
						int roomId1=dis.readInt();
System.out.println("enter room : "+roomId1);
						paticipator.setRoomId(roomId1);
						paticipator.setIntoRoom(true);
						break;
					case 7:
						int roomId2=dis.readInt();
System.out.println("exit room : "+roomId2);
						paticipator.setRoomId(0);
						paticipator.setIntoRoom(false);
						paticipator.setReady(false);
						break;
					case 6:
						int roomId3=dis.readInt();
System.out.println("no such room : "+roomId3);
						paticipator.setRoomId(0);
						break;
					case 5:
						parse(dp, msgType);
						break;
					case 4:
						parse(dp, msgType);
						break;
					case 3:
						
						break;
					case 2:
						
						break;
					case 1:
						
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

		void parse(DatagramPacket dp,int msgType) {
			ByteArrayInputStream bais = new ByteArrayInputStream(buf, 0, dp.getLength());
			DataInputStream dis = new DataInputStream(bais);
			Message message=null;
			switch (msgType) {
			case 11:
				message=new APlayerExitMessage(paticipator);
				message.parse(dis);
				break;
			case 5:
				message=new APlayerEntedMessage(paticipator);
				message.parse(dis);
				break;
			case 4:
				message=new PlayerReadyMessage(paticipator);
				message.parse(dis);
				break;
			default:
				break;
			}
		}
		
	}
	
	

}
