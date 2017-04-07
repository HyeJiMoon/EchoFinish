//엔터를 쳐야 보내진것을 받을 수 있는데 
//채팅은 언제나 무한루프돌면서 계속 실시간 채팅을 받아올 수 있게해야한다 
 
/*
 *키보드 입력시마다 서버에 메세지를 보내고 다시 받아오게 처리하면 생기는 문제점
 * --> 키보드를 치지않으면 서버의 메세지를 실시간 받아올 수 없다
 * 
 * --> 해결책: 
 * 			이벤트 발생과 상관없이 언제나 무한루프를 돌면서 서버의 메세지를 청취할 수 있는 별도의 실행부(쓰레드)를 만들자 
 */

//동생은 듣고 말하기를 다하자 send listen
package echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	boolean flag=true; //어떤 조건에따라 무한루프 풀 수 있으니까
	Socket socket;  
	BufferedReader buffr; 
	BufferedWriter buffw;
	JTextArea area;  //new 
	
	
	public ClientThread(Socket socket, JTextArea area) { //받아오려면 메서드!!! 생성자 필요해 
		this.socket=socket;
		this.area=area;
		
		try {
			//대화를 나누기전에 스트림 얻어놓끼!
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream())); //앞으로 스레드가 청취역할!
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	//서버에 메세지 보내기! (말하기 ) 서버에 나옴
	public void send(String msg){
		try {
			buffw.write(msg+"\n"); //msg 로 받은 것을 쓰고
			buffw.flush();//비워내겟다 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//서버에 메세지 받아오기 (듣기 ) 보낸것에도 뜸
	//무한루프 돌면서 감시하고 있어야지 채팅이 올 수 있게
	public void listen(){
		String msg=null; //스트링형으로 받고
		try {
			msg=buffr.readLine();//msg로
			area.append(msg+"\n"); //area 에 뿌려주기
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	public void run() {
		//무한루프지만 무섭지않다 readLine에서 대기상태에 빠지니까
		while(flag){
			listen();
		}
	}		
		
	
}
