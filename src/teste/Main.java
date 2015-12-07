package teste;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
	public static void main(String[] args) throws UnknownHostException, IOException {
		String serverName = args[0];
		Integer port = Integer.parseInt(args[1]);
		Socket client = new Socket(serverName, port);
		DataInputStream in = new DataInputStream( client.getInputStream() );
		DataOutputStream out = new DataOutputStream( client.getOutputStream() );
		out.write( args[2].getBytes() );
		//System.out.println(in.readUTF());
		client.close();
	}
}
