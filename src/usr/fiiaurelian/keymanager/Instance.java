package usr.fiiaurelian.keymanager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import usr.fiiaurelian.cryptography.AESWrapper;

public class Instance extends Thread {
	
	public static final String CBC_REQUEST = "CBC";
	public static final String OFB_REQUEST = "OFB";
	
	private static final int TIMEOUT = 10000;
	
	private ServerSocket serverSocket;
	private String       cbcKey;
	private String       ofbKey;
	private String       encKey;
	private String       cbcInitializationVector;
	private String       ofbInitializationVector;
	
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
	
	public Instance withEncryptionKey( String key ) {
		this.encKey = key;
		return this;
	}
	
	@Override
	public void run() {
		for( ;; ) {
			try {
				Socket instance = serverSocket.accept();
				System.out.println( "S-a conectat: " + instance.getInetAddress() );
				DataInputStream in = new DataInputStream( instance.getInputStream() );
				DataOutputStream out = new DataOutputStream( instance.getOutputStream() );
				String type = in.readUTF();
				System.out.println( "Cere cheia pentru: " + type );
				String answer = computeAnswerForRequest( type );
				out.writeUTF( answer );
				System.out.println( "Transmit: " + answer );
				System.out.println( "Am transmis cheia si IV pentru " + type );
				instance.close();
			} catch ( Exception ioException ) {
				System.err.println( ioException.getMessage() );
			}
			
		}
	}
	
	private String computeAnswerForRequest( String request ) throws Exception {
		String answer;
		AESWrapper aes = new AESWrapper();
		switch( request.toUpperCase() ) {
		case CBC_REQUEST:
			answer = cbcKey + "$" + cbcInitializationVector;
			break;
		case OFB_REQUEST:
			answer = ofbKey + "$" + ofbInitializationVector;
			break;
		default:
			answer = null;
		}
		return aes.encrypt( answer, encKey );
	}

}
