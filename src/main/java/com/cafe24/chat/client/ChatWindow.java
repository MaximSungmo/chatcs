package com.cafe24.chat.client;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import com.cafe24.network.chat.ChattingServer;

public class ChatWindow {

	private Frame frame;
	private Panel pannel;
	private Button buttonSend;
	private TextField textField;
	private TextArea textArea;
	private TextArea textArea2;
	
	private BufferedReader br;
	private PrintWriter pr;
	private String name;	
	private String userList;
	private Socket socket;
	
	public ChatWindow(Socket socket, BufferedReader br, PrintWriter pr, String name) {
		frame = new Frame(name);
		pannel = new Panel();
		buttonSend = new Button("Send");
		textField = new TextField();
		textArea = new TextArea(30, 80);
		textArea2 = new TextArea(30, 30);
		this.name = name;
		this.br = br;
		this.pr = pr;
		this.socket = socket;
		pr.println("join::"+name);
	}
	

	public void show() {
		// Button
		buttonSend.setBackground(Color.GRAY);
		buttonSend.setForeground(Color.WHITE);
		buttonSend.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed( ActionEvent actionEvent ) {
				sendMessage();
			}
		});

		// Textfield
		textField.setColumns(80);
		textField.addKeyListener( new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				char keyCode = e.getKeyChar();
				if(keyCode == KeyEvent.VK_ENTER) {
					sendMessage();
				}
			}
		});

		// Pannel
		pannel.setBackground(Color.LIGHT_GRAY);
		pannel.add(textField);
		pannel.add(buttonSend);
		frame.add(BorderLayout.SOUTH, pannel);

		// TextArea
		textArea.setEditable(false);
		textArea2.setEditable(false);
		textArea2.setText("현재 참여자 목록\r\n");

		frame.add(BorderLayout.CENTER, textArea);
		frame.add(BorderLayout.EAST, textArea2);


		// Frame
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				finish();
			}
		});
		frame.setVisible(true);
		frame.pack();
	}
	
	public void finish() {
			pr.println("quit::");
			System.out.println("채팅이 종료 되었습니다..........");
			System.exit(0);
	}
	
	
	public void updateTextArea(String message) {
		textArea.append(message);
		textArea.append("\r\n");
	}
	
		public void updateUserArea(String userList) {
			textArea2.setText("");
			textArea2.setText("현재 참여자 목록\r\n귓속말방법 : /whisfer/대화명/메세지\r\n");
			String[] users = userList.split(",userName,");
			for(int i=1; i<users.length; i=i+1) {
				textArea2.append(users[i]);
				textArea2.append("\r\n");
			}
	}
	
	public void sendMessage() {	
		String message = textField.getText();
		if("/quit".equals(message)) {
			finish();
		}else if(message.startsWith("/whisfer")) {
			String whisfer = textField.getText();
			String myName = name;
			String[] id = textField.getText().split("/");
			if(id.length>=4) {
				pr.println(whisfer+"/"+myName);
				textField.setText("/whisfer/"+id[2]+"/");
			}else {
				textField.setText("/whisfer/대화명/메세지");
			}
		}else {
			pr.println("msg:: "+message);		
			textField.setText("");
			textField.requestFocus();
			System.out.println("msg::"+message);
		}
		

			
		}
	}	
	
