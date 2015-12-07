package usr.fiiaurelian.keymanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Instance extends Thread {
	
	public static final String CBC_REQUEST = "CBC";
	public static final String OFB_REQUEST = "OFB";
	
	private static final int TIMEOUT = 10000;
	
	private ServerSocket serverSocket;
	private String cbcKey;
	private String ofbKey;
	private String encKey;
	private String cbcInitializationVector;
	private String ofbInitializationVector;
	
	public Instance( Integer port ) {
		try {
			serverSocket = new ServerSocket( port );
			serverSocket.setSoTimeout( TIMEOUT );
		} catch ( IOException ioException ) {
			System.err.println( ioException.getMessage() );
		}
	}
	
	public Instance withCBCMode( String key, String initializationVector ) {
		this.cbcKey = key;
		this.cbcInitializationVector = initializationVector;
		return this;
	}
	
	public Instance withOFBMode( String key, String initializationVector ) {
		this.ofbKey = key;
		this.ofbInitializationVector = initializationVector;
		return this;
	}
	
	@Override
	public void run() {
		for( ;; ) {
			try {
				Socket instance = serverSocket.accept();
				DataInputStream in = new DataInputStream( instance.getInputStream() );
				DataOutputStream out = new DataOutputStream( instance.getOutputStream() );
				String type = in.readUTF();
				out.writeUTF( computeAnswerForRequest( type ) );
				instance.close();
			} catch ( IOException ioException ) {
				System.err.println( ioException.getMessage() );
			}
			
		}
	}
	
	private String computeAnswerForRequest( String request ) {
		String answer;
		switch( request.toUpperCase() ) {
		case CBC_REQUEST:
			answer = cbcKey;
			break;
		case OFB_REQUEST:
			answer = ofbKey;
			break;
		default:
			answer = null;
		}
		return answer;
	}

}
