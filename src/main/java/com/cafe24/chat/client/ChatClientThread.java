package com.cafe24.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClientThread extends Thread {
	private Socket socket;
	private String name;
	private BufferedReader br;
	private PrintWriter pr;

	public ChatClientThread(Socket socket, String name) {
		this.socket = socket;
		this.name = name;
		try {
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			this.pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ChatWindow chatWindow = new ChatWindow(socket, br, pr, name);
		chatWindow.show();
		while (true) {
			try {
				String data = br.readLine();
				if (data == null) {
					System.out.println("[Client] closed by server");
					break;
				}
				if (data.startsWith(",userName,")) {
					chatWindow.updateUserArea(data);
				} else {
					chatWindow.updateTextArea(data);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}
