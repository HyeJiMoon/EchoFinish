

package echo.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame implements ActionListener{
	JPanel p_north;
	JTextField t_port;
	JButton bt_start;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	ServerSocket server; //접속 감지용 소켓
	
	Thread thread; // 서버 가동용 쓰레드 (작동 시켰을 때 멈추니까 실행부에 두면안돼서!)
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port),10);
		bt_start=new JButton("가동");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north,BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		
		
		setBounds(600,100,300,400);//나란히 띄울거라서 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	//서버 생성 및 가동	
	//예외의 종류 - checked Exception 예외처리강요 
	
	//				   runtime Exception 예외처리 강요 X -> 근데 안하면 비정상종료 된당!!  컴파일 타이밍에러
	
	public void startServer(){
		bt_start.setEnabled(false); //계쏙해서 가동누르는 불량유저를 위한 비활성화 버튼 ! 
		
		try {
			port=Integer.parseInt(t_port.getText());//포트라는 변수를 담는다
			server=new ServerSocket(port);
			area.append("서버준비됨..\n");
			//서버 가동
			//실행부라 불리는 메인쓰레드는 절대 무한루프나 대기, 지연상태에 빠지게 해서는 안된다(디자인안먹히는상태엿어)
			//왜? 실행부는 유저들의 이벤트를 감지하거나, 프로그램을 운영해야하므로, 무한루프나 , 대기에 빠지면 본연의 역할을 할 수 없게 된다.
			//자바니까 먹히긴하지, 스마트폰 개발분야에서는 이와같은 코드는 이미 컴파일타임부터 에러발생한다 -->쓰레드로 역할을 분리하자 
			//전날에 한 것인 0406과는 다르게 지금은 GUI에서 하고 있자나! 그래서 문제가 생김! 
			//server.accept();
			Socket socket=server.accept();
			area.append("서버 가동..\n");
			//클라이언트는 대화를 하기 위해 접속한 것이므로, 접속이 되는 순간 스트림을 얻어놓자 ! 
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//클라이언트의 메세지 받기! 
			String data;
			while(true){
				data=buffr.readLine(); //한줄 -> 계쏙 들어야하니까 While  클라이언트의 메세지  
				area.append("클라이언트의 말:"+data+"\n"); //무슨말하는지 area에 뿌려보자 
				
				buffw.write(data+"\n"); //클라이언트에게 다시보내기 
				buffw.flush(); //버퍼비우기
			}
			
		} catch (NumberFormatException e) {//이 코드는 숫자를 넣지않으면 예외가 날 수 있다(Checkexception) 
			//얘는 왜 강요를 안하나? 하지 않으면 비정상종료된다 . 강요하지 않는 예외!!!!!!!!!!!  
			JOptionPane.showMessageDialog(this, "포트는 숫자로 넣어라");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void actionPerformed(ActionEvent e) {
		//(작동 시켰을 때 멈추니까 실행부에 두면안돼서!) area.append서버 준비됨 여기서 보자 GUI기반에서는 무조건 thread 가 있어야한다!
		thread=new Thread(){
			//독립수행될 코드는 run에 넣자
			public void run() {
				startServer();
			}			
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}
