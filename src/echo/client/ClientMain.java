package echo.client;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import db.DBManager;

public class ClientMain extends JFrame implements ItemListener,ActionListener{

	JPanel p_north;
	Choice choice;
	JTextField t_port, t_input;
	JButton bt_connect;
	JTextArea area;
	JScrollPane scroll;
	int port=7777;
	DBManager manager;
	ArrayList<Chat> list=new ArrayList<Chat>(); //<우리반사람>
	Socket socket; //대화용소켓! 따라서 스트림도 뽑아낼 것임!
	String ip;
	//BufferedReader buffr;
	//BufferedWriter buffw;
	ClientThread ct;
	
	public ClientMain() {
		p_north=new JPanel();
		choice=new Choice();
		t_port=new JTextField(Integer.toString(port),10);
		t_input=new JTextField(10);
		bt_connect=new JButton("접속");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		manager=DBManager.getInstance(); //오 static 변수 선언한거 가져오는거->클래스명으로 접근. instance가져오는거
	
		p_north.add(choice);	
		p_north.add(t_port);
		p_north.add(bt_connect);
		
		add(p_north,BorderLayout.NORTH);
		add(scroll);
		add(t_input,BorderLayout.SOUTH);
		
		loadIP(); //화면창이전에 호출 되어야지
		
		for(int i=0;i<list.size();i++){
			choice.add(list.get(i).getName());
		}
		
		//리스너와의 연결
		choice.addItemListener(this);
		bt_connect.addActionListener(this);
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key=e.getKeyCode();
				
				if(key==KeyEvent.VK_ENTER){
					//서버에보내는 메서드
					String msg=t_input.getText(); 
					ct.send(msg);
					t_input.setText("");//입력한 글씨 지우기
						//ct.listen();//다시 받기  -> 이건 동생이 계속해서 받고있기 때문에 필요없어진 코드
						
				}	
			}	
		});
		
		setBounds(600,100,300,400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	//서버가 메세지 던지는걸 캐스팅이라 하는데
	//유니캐스팅 멀티캐스팅 
	//유니캐스팅 서버가 한사람한테 전달하는거 ㅅ
	
	//데이터베이스 가져오기 
	public void loadIP(){
		Connection con=manager.getConnection();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql="select * from chat order by chat_id asc";
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			//rs의 모든 데이터를 dto로 옮기는 과정 : 매핑과정
			while(rs.next()){
				Chat dto=new Chat(); //Chat 올리기 후에 rs 로 옮기기
				dto.setChat_id(rs.getInt("chat_id"));
				dto.setName(rs.getString("name"));
				dto.setIp(rs.getString("ip"));
				
				list.add(dto);			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			manager.disConnect(con);
			
		}
	}
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();  //초이스에는 선택한 인덱스를 구하는 메서드가 있으니까 형변환
		int index=ch.getSelectedIndex(); //일종의 배열에서 가져와야하기때문에
		Chat chat=list.get(index);
		
		this.setTitle(chat.getIp());
		ip=chat.getIp(); //멤버변수에도 대입 그래야 따른데서도 써먹을 수 있지 
	
	}
	//서버에 접속을 시도 하자! 
	public void connect(){
		//소켓 생성시 접속이 발생한다!!!!!!!!!!!!!!!!!!!
		try {
			port=Integer.parseInt(t_port.getText()); //안정성을위해서 런타임캐치!
			socket=new Socket(ip,port);
			
			//동생스레드 만들기 ! --( 실시간으로 서버의 메세지를 청취하기 위해 , 쓰레드를 생성하여 대화업무를 다 맡겨버리자
			//따라서 종이컵 & 실의 보유자는 동생(쓰레드)
			ct=new ClientThread(socket, area);
			ct.start();
			ct.send("안녕"); //앞의 ct 는 동생인데 말을 걸고 받는거는 동생이 하니까 앞에ct . 붙이자 
			
			//접속이 되었고, 대화를 나누기전에 스트림 얻어놓기 !  -->쓰레드로!!!!!
			//buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			
			//buffw.write("안녕?\n");
			//buffw.flush();
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent e) {
		connect();
	}
	

	public static void main(String[] args) {
		new ClientMain();
	
	}


	
}
