package com.cafe24.chat.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class ChatClientApp {
	private static final String SERVER_IP = "192.168.1.8";
	private static final int SERVER_PORT = 7000;
	
	public static void main(String[] args) {
		String name = null;
		Scanner sc = new Scanner(System.in);
		Socket socket = null;

		//대화명 입력받음, 입력이 제대로 될 때까지 반복하기 
		while( true ) {
			
			System.out.println("대화명을 입력하세요.");
			System.out.print(">>> ");
			name = sc.nextLine();
			if (name.isEmpty() == false ) {
				break;
			}
			System.out.println("대화명은 한글자 이상 입력해야 합니다.\n");
		}
		//이름 입력받을 때 사용한 스캐너 닫음
		sc.close();
		
		try {	
			//1. 소켓 생성
			socket = new Socket();
			//3. 서버 연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			//blocking 완료되면 소켓 생성 
			
			Thread thread = new ChatClientThread(socket, name);
			thread.start();

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}