package com.cafe24.network.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.cafe24.network.chat.ChattingServer;

public class ChattingServerReceiveThread extends Thread  {

	private String name = null;
	private Socket socket;
//	private List<PrintWriter> listWriters;
	
	HashMap<PrintWriter, String> userMap;

	public ChattingServerReceiveThread(Socket socket, HashMap<PrintWriter, String> userMap) {
		this.socket = socket;
		this.userMap = userMap;
	}
	
	

	@Override
	public void run() {
		//클라이언트와 연결하기 
		InetSocketAddress inetRemoteSocketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		String remoteHostAddress = inetRemoteSocketAddress.getAddress().getHostAddress();
		int remotePort = inetRemoteSocketAddress.getPort();
		ChattingServer.log("connected by client[" + remoteHostAddress + ":" + remotePort + "]");

		try {
			// 4. IOStream 생성(받아오기)
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);

			while (true) {
				// 5. 데이터 읽기
				String data = br.readLine();
				if (data == null) {
					ChattingServer.log("closed by client");
					doQuit(pr);
					break;
				}				
				//프로토콜 분석
				String[] tokens = data.split("::");
				if("join".equals(tokens[0])) {
					name = tokens[1];
					doJoin(pr);
					userListSend();
				}else if("msg".equals(tokens[0])) {
					broadcast(name+" : "+tokens[1]);
				}else if("quit".equals(tokens[0])) {
					doQuit(pr);
					userListSend();
				}
				
				if(data.startsWith("/whisfer")) {
					System.out.println("whisfer 작동!!!");
					String[] whisferTokens = data.split("/");
					if (whisferTokens.length>=4) {
						String wh = whisferTokens[1];
						String toName = whisferTokens[2];
						String whMessage = whisferTokens[3];
						String fromName = whisferTokens[4];
						PrintWriter toWhisferPr = (PrintWriter) getKey(userMap,toName);
						PrintWriter toWhisferfrom = (PrintWriter) getKey(userMap,fromName);
						toWhisferPr.println(fromName+"으로부터 귓속말 :"+whMessage);
						toWhisferfrom.println(toName+"에게 귓속말 :"+whMessage);
					}
				}
			}
			

		} catch (SocketException e) {
			System.out.println("[server] closed by client");
		} catch (IOException e) {
			e.printStackTrace();
			
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//map에서 밸류값으로 키값찾기...
	public static Object getKey(HashMap<PrintWriter,String> m, Object value) { 
		for(Object o: m.keySet()) { 
			if(m.get(o).equals(value)) { 
				return o; 
			} 
		} return null; 
	}

	
//	private void doWhisper(String user, String data) {
//		PrintWriter userPr = (PrintWriter) getKey(userMap, user);
//		System.out.println(userPr+"  "+ user);
//		userPr.println("whisper>>"+ name + ": " + data);
//		userPr.flush();
//	}

	
	
	private void userListSend() {
		synchronized (userMap) {
//			Set<PrintWriter> set = userMap.keySet();
//			Iterator<PrintWriter> keySetIterator = userMap.keySet().iterator();
//			while(keySetIterator.hasNext()) {
//				PrintWriter pr = keySetIterator.next();
//				for(PrintWriter key:set) {
//					pr.print(",userName,"+userMap.get(key));
//				}	
//			}
			String sendListStr = "";			
			for(PrintWriter key:userMap.keySet()) {
				sendListStr +=",userName,"+ userMap.get(key);
			}
			broadcast(sendListStr);
		}
	}
	
	//조인 시 메세지 모두에게 전달 
	private void doJoin(PrintWriter pr) throws IOException  {
		addWriter(pr);
		String data = name + " 님이 입장하였습니다.\r\n현재 참여 인원" +userMap.size();
		broadcast(data);
	}
	//printwriter 추가, userList 추가 
	private void addWriter(PrintWriter pr) {
		synchronized(userMap) {
			userMap.put(pr, name);
		}
	}
	
	//브로드 캐스트 - 여러명에게 메시지 전달 
	private void broadcast(String data) {
		synchronized (userMap) {
			Iterator<PrintWriter> keySetIterator = userMap.keySet().iterator();
			while(keySetIterator.hasNext()) {
				PrintWriter pr = keySetIterator.next();
				pr.println(data);				
			}
		}	
	}
	
	//종료 시 이벤트 
	private void doQuit(PrintWriter pr) {
			String data = name + " 님이 퇴장하였습니다.\r\n현재 참여 인원 : " + (userMap.size()-1);
			broadcast(data);
			removeWriter(pr);
	}

	private void removeWriter(PrintWriter pr) {
		userMap.remove(pr);	
	}	
	
}