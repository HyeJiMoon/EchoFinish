

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
	ServerSocket server; //���� ������ ����
	
	Thread thread; // ���� ������ ������ (�۵� ������ �� ���ߴϱ� ����ο� �θ�ȵż�!)
	BufferedReader buffr;
	BufferedWriter buffw;
	
	public ServerMain() {
		p_north=new JPanel();
		t_port=new JTextField(Integer.toString(port),10);
		bt_start=new JButton("����");
		area=new JTextArea();
		scroll=new JScrollPane(area);
		
		p_north.add(t_port);
		p_north.add(bt_start);
		
		add(p_north,BorderLayout.NORTH);
		add(scroll);
		
		bt_start.addActionListener(this);
		
		
		
		setBounds(600,100,300,400);//������ ���Ŷ� 
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	//���� ���� �� ����	
	//������ ���� - checked Exception ����ó������ 
	
	//				   runtime Exception ����ó�� ���� X -> �ٵ� ���ϸ� ���������� �ȴ�!!  ������ Ÿ�ֿ̹���
	
	public void startServer(){
		bt_start.setEnabled(false); //����ؼ� ���������� �ҷ������� ���� ��Ȱ��ȭ ��ư ! 
		
		try {
			port=Integer.parseInt(t_port.getText());//��Ʈ��� ������ ��´�
			server=new ServerSocket(port);
			area.append("�����غ��..\n");
			//���� ����
			//����ζ� �Ҹ��� ���ξ������ ���� ���ѷ����� ���, �������¿� ������ �ؼ��� �ȵȴ�(�����ξȸ����»��¿���)
			//��? ����δ� �������� �̺�Ʈ�� �����ϰų�, ���α׷��� ��ؾ��ϹǷ�, ���ѷ����� , ��⿡ ������ ������ ������ �� �� ���� �ȴ�.
			//�ڹٴϱ� ����������, ����Ʈ�� ���ߺо߿����� �̿Ͱ��� �ڵ�� �̹� ������Ÿ�Ӻ��� �����߻��Ѵ� -->������� ������ �и����� 
			//������ �� ���� 0406���� �ٸ��� ������ GUI���� �ϰ� ���ڳ�! �׷��� ������ ����! 
			//server.accept();
			Socket socket=server.accept();
			area.append("���� ����..\n");
			//Ŭ���̾�Ʈ�� ��ȭ�� �ϱ� ���� ������ ���̹Ƿ�, ������ �Ǵ� ���� ��Ʈ���� ������ ! 
			buffr=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			//Ŭ���̾�Ʈ�� �޼��� �ޱ�! 
			String data;
			while(true){
				data=buffr.readLine(); //���� -> ��� �����ϴϱ� While  Ŭ���̾�Ʈ�� �޼���  
				area.append("Ŭ���̾�Ʈ�� ��:"+data+"\n"); //�������ϴ��� area�� �ѷ����� 
				
				buffw.write(data+"\n"); //Ŭ���̾�Ʈ���� �ٽú����� 
				buffw.flush(); //���ۺ���
			}
			
		} catch (NumberFormatException e) {//�� �ڵ�� ���ڸ� ���������� ���ܰ� �� �� �ִ�(Checkexception) 
			//��� �� ���並 ���ϳ�? ���� ������ ����������ȴ� . �������� �ʴ� ����!!!!!!!!!!!  
			JOptionPane.showMessageDialog(this, "��Ʈ�� ���ڷ� �־��");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void actionPerformed(ActionEvent e) {
		//(�۵� ������ �� ���ߴϱ� ����ο� �θ�ȵż�!) area.append���� �غ�� ���⼭ ���� GUI��ݿ����� ������ thread �� �־���Ѵ�!
		thread=new Thread(){
			//��������� �ڵ�� run�� ����
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
