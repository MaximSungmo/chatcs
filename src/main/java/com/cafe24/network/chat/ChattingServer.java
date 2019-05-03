package com.cafe24.network.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cafe24.chat.client.ChatClientApp;

public class ChattingServer {
	
	private static final int PORT = 7000;
	
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
//		List<PrintWriter> listWriters = new ArrayList<PrintWriter>();
//		List<String> userList = new ArrayList<String>(); 
		HashMap<PrintWriter, String> userMap = new HashMap<PrintWriter, String>();
		
		
		try {
			//1. 서버소켓 생성
			serverSocket = new ServerSocket();
			//2. 바인딩(binding)
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));
			log("Server start. Wait connection....[port:" + PORT + "]");
			
			while(true) {
				//3. accept
				Socket socket = serverSocket.accept();

				Thread thread = new ChattingServerReceiveThread(socket, userMap);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String log) {
		System.out.println("[server#" + Thread.currentThread().getId() + "] " + log);
	}
}