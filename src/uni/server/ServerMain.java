package uni.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
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
	ServerSocket server;
	Thread thread; //서버운영쓰레드 결국 
	
	
	public ServerMain() {
		p_north = new JPanel();
		t_port = new JTextField(Integer.toString(port) ,10);
		bt_start = new JButton("가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		p_north.add(t_port);
		p_north.add(bt_start);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		setBounds(600,100,300,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//서버를 가동한다 =서버 소켓을 올린다
	public void startServer(){
		bt_start.setEnabled(false);
		try {
			port=Integer.parseInt(t_port.getText());
			server =new ServerSocket(port); //여기까지 서버생성
			
			area.append("서버생성\n");//서버 가동
			
			//server.accept(); //무한루프상태에 빠트리면 안돼 이거는 쓰레드가 하는거야
			thread=new Thread(){ //thread내부익명 //서버를 가동할 쓰레드
				public void run() {
				
					//여기의 try는 인스턴스로 만들 대상 여기에다 두면 한사람접속후 다른사람 후 계속 끊기므로 다른 데 두자 
					//마치 bullet만들었던거 처럼 그 총알은 각각의 속성을 갖는다 이것도 또한 마찬가지
//들어왔던 사람마다 코드를 쟁탈 인스턴스로 만들어 또 다른 공간에 만들어 새로 생성되게끔 그래서 많은 사람이 들어올수있게 => 독립적으로 움직이며, 객체화 시켜야한다 ! : 쓰레드

					try {
						//IP알아맞추기위한 종이컵=소켓
						while(true){ //다중의 접속자를 발견하기 위해서 while!! 
							Socket socket=server.accept();
							String ip=socket.getInetAddress().getHostAddress();
							area.append(ip+"접속자 발견!\n");
							
							//접속자마다 아바타 생성해주고 대화를 나눌 수 있도록 해주자 
							Avatar av=new Avatar(socket, area);
							av.start();
							
							//대화를 나눌 스트림 뽑기 
							//BufferedReader buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
							//BufferedWriter buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							
							//String msg = buffr.readLine();
							//area.append(msg+"\n");
						}
					} catch (IOException e) {
				
						e.printStackTrace();
					}
				}
				
			};
			thread.start();//쓰레드 동작시작 
			

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		startServer();
	
	}
	
	
	
	public static void main(String[] args) {
		new ServerMain();
	}
}