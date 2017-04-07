//���͸� �ľ� ���������� ���� �� �ִµ� 
//ä���� ������ ���ѷ������鼭 ��� �ǽð� ä���� �޾ƿ� �� �ְ��ؾ��Ѵ� 
 
/*
 *Ű���� �Է½ø��� ������ �޼����� ������ �ٽ� �޾ƿ��� ó���ϸ� ����� ������
 * --> Ű���带 ġ�������� ������ �޼����� �ǽð� �޾ƿ� �� ����
 * 
 * --> �ذ�å: 
 * 			�̺�Ʈ �߻��� ������� ������ ���ѷ����� ���鼭 ������ �޼����� û���� �� �ִ� ������ �����(������)�� ������ 
 */

//������ ��� ���ϱ⸦ ������ send listen
package echo.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import javax.swing.JTextArea;

public class ClientThread extends Thread{
	boolean flag=true; //� ���ǿ����� ���ѷ��� Ǯ �� �����ϱ�
	Socket socket;  
	BufferedReader buffr; 
	BufferedWriter buffw;
	JTextArea area;  //new 
	
	
	public ClientThread(Socket socket, JTextArea area) { //�޾ƿ����� �޼���!!! ������ �ʿ��� 
		this.socket=socket;
		this.area=area;
		
		try {
			//��ȭ�� ���������� ��Ʈ�� ������!
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream())); //������ �����尡 û�뿪��!
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	//������ �޼��� ������! (���ϱ� ) ������ ����
	public void send(String msg){
		try {
			buffw.write(msg+"\n"); //msg �� ���� ���� ����
			buffw.flush();//������ٴ� 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//������ �޼��� �޾ƿ��� (��� ) �����Ϳ��� ��
	//���ѷ��� ���鼭 �����ϰ� �־���� ä���� �� �� �ְ�
	public void listen(){
		String msg=null; //��Ʈ�������� �ް�
		try {
			msg=buffr.readLine();//msg��
			area.append(msg+"\n"); //area �� �ѷ��ֱ�
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	public void run() {
		//���ѷ������� �������ʴ� readLine���� �����¿� �����ϱ�
		while(flag){
			listen();
		}
	}		
		
	
}
