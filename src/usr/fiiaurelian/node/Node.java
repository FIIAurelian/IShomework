package usr.fiiaurelian.node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.bouncycastle.util.Arrays;

import usr.fiiaurelian.cryptography.AESWrapper;
import usr.fiiaurelian.cryptography.CBCMode;
import usr.fiiaurelian.cryptography.OperationMode;
import usr.fiiaurelian.keymanager.Instance;
import usr.fiiaurelian.utils.HexData;

public class Node extends Thread {
	
	private static final Integer BUFFER_SIZE    = 1024;
	private static final String  ACCEPT_MESSAGE = "OK"; 
	
	private ServerSocketChannel serverSocket;
	private String       		encKey;
	private String       		keyManagerName;
	private Integer      		keyManagerPort;
	
	public Node( Integer port ) {
		try {
			serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind( new InetSocketAddress( port ) );
			serverSocket.configureBlocking( false );
		} catch ( IOException ioException ) {
			ioException.printStackTrace();
		}
	}
	
	public Node withEncryptionKey( String key ) {
		this.encKey = key;
		return this;
	}
	
	public void setEncriptionKey( String key ) {
		this.encKey = key;
	}
	
	public void setKeyManagerName( String name ) {
		this.keyManagerName = name;
	}
	
	public void setKeyManagerPort( Integer port ) {
		this.keyManagerPort = port;
	}
	
	@Override
	public void run() {
		try {
			Selector selector = Selector.open();
			SystemInPipe stdinPipe = new SystemInPipe();
		    SelectableChannel stdin = stdinPipe.getStdinChannel();
		    stdin.register( selector, SelectionKey.OP_READ );
		    stdinPipe.start();
			serverSocket.register( selector, SelectionKey.OP_ACCEPT );
			for( ; ; ) {
				selector.select();
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while( iterator.hasNext() ) {
					SelectionKey key = iterator.next();
					iterator.remove();
					if( key.isAcceptable() ) {
						acceptClient( serverSocket, selector, key );
					}
					if( key.isReadable() ) {
						String message = readMessage( key );
						interpretMessage( message, key );
					}
				}
			}
			
		} catch ( Exception ioException ) {
			ioException.printStackTrace();
		}
	}
	
	private void acceptClient( ServerSocketChannel serverSocket, Selector selector, SelectionKey key ) throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking( false );
		client.socket().setTcpNoDelay( true );
		client.register( selector, SelectionKey.OP_READ );
	}
	
	private String readMessage( SelectionKey key ) throws IOException {
		StringBuilder message = new StringBuilder();
		ReadableByteChannel channel = ( ReadableByteChannel ) key.channel();
		ByteBuffer byteBuffer = ByteBuffer.allocate( BUFFER_SIZE );
		int readedBytes = channel.read( byteBuffer );
		while( readedBytes > 0 ) {
			byteBuffer.flip();
			while( byteBuffer.hasRemaining() ) {
				message.append( ( char ) byteBuffer.get() );
			}
			byteBuffer.clear();
			readedBytes = channel.read( byteBuffer );
		}
		return message.toString();
	}
	
	private void interpretMessage( String message, SelectionKey key ) {
		CommandArguments commandArguments = CommandArguments.getArgumentsForCommand( message );
		if( commandArguments.isSendType() == true ) 
			sendEncryptedFile( commandArguments, key );
		else
			receiveEncryptedFile( commandArguments, key );
	}
	
	private void sendEncryptedFile( CommandArguments arguments, SelectionKey key ) {
		System.out.println( "Initiez procedura de trimitere fisier." );
		System.out.println( "Cer cheia si vectorul de init de la Key Manager." );
		String keyIV = getKeyIVForMode( arguments.getMode() );
		
		System.out.println( keyIV );
		
		int splitPosition = keyIV.indexOf( '$' );
		String secretKey  = keyIV.substring( 0, splitPosition );
		String IV         = keyIV.substring( splitPosition + 1 );
		
		System.out.println( "Cheia este: " + secretKey );
		System.out.println( "Vectorul de init este: " + IV );
		
		Socket client;
		try {
			System.out.println( "Transmit la client modul: " + arguments.getMode() );
			client = new Socket( arguments.getAddressName() , arguments.getAddressPort() );
			DataInputStream in = new DataInputStream( client.getInputStream() );
			DataOutputStream out = new DataOutputStream( client.getOutputStream() );
			out.write( arguments.getMode().getBytes() );
			byte[] buffer = new byte[ BUFFER_SIZE ];
			int readedBytes = in.read( buffer );
			String response = new String( buffer );
			System.out.println( "Am primit raspunsul: " + response );
			if( response.startsWith( ACCEPT_MESSAGE ) ) {
				OperationMode operationMode = OperationMode.getModeByName( arguments.getMode() );
				FileInputStream fileInputStream = new FileInputStream( new File( arguments.getFilePath() ) );
				while( ( readedBytes = ( fileInputStream.read( buffer ) ) ) != -1 ) {
					if( readedBytes < 1 )continue;
					System.out.println( "Scriu: " + new String( buffer ) );
					buffer = operationMode.encrypt( Arrays.copyOfRange( buffer, 0, readedBytes ), secretKey, HexData.hexafy( IV ) );
					out.write( buffer, 0, buffer.length );
					buffer = new byte[ BUFFER_SIZE ];
				}
				fileInputStream.close();
				System.out.println("Am terminat de trimis fisierul!");
			}
			client.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
	}
	
	private void receiveEncryptedFile( CommandArguments arguments, SelectionKey key ) {
		System.out.println( "Modul este: " + arguments.getMode() );
		
		System.out.println( "Cer cheia si vectorul de init de la Key Manager." );
		String keyIV = getKeyIVForMode( arguments.getMode() );
		
		System.out.println( keyIV );
		
		int splitPosition = keyIV.indexOf( '$' );
		String secretKey  = keyIV.substring( 0, splitPosition );
		String IV         = keyIV.substring( splitPosition + 1 );
		
		System.out.println( "Cheia este: " + secretKey );
		System.out.println( "Vectorul de init este: " + IV );
		
		try {
			SocketChannel channel = ( SocketChannel ) key.channel();
			ByteBuffer buffer = ByteBuffer.allocate( BUFFER_SIZE );
			buffer.put( ACCEPT_MESSAGE.getBytes() );
			buffer.flip();
			channel.write( buffer );
			readFile( ( ReadableByteChannel ) key.channel(), OperationMode.getModeByName( arguments.getMode() ), secretKey, IV );
			channel.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	
	}
	
	private void readFile( ReadableByteChannel channel, OperationMode operationMode, String key, String IV ) throws Exception {
		File file = new File( "received" );
		if(file.exists() == false)
			file.createNewFile();
		
		FileOutputStream fileOutputStream = new FileOutputStream( file );
		ByteBuffer byteBuffer = ByteBuffer.allocate( BUFFER_SIZE );
		byte[] buffer = new byte[ BUFFER_SIZE ];
		int readedBytes = channel.read( byteBuffer );
		while( readedBytes != -1 ) {
			System.out.println("Am citit: " + readedBytes);
			int index = 0;
			byteBuffer.flip();
			while( byteBuffer.hasRemaining() ) {
				buffer[ index++ ] = byteBuffer.get();
			}
			buffer = operationMode.decrypt( buffer, key, HexData.hexafy( IV ) );
			if( readedBytes > 0 ) {
				System.out.println("Am primit: " + new String( buffer ));
				fileOutputStream.write( buffer, 0, readedBytes );
			}
			byteBuffer.clear();
			readedBytes = channel.read( byteBuffer );
		}
		fileOutputStream.close();
		System.out.println("Am terminat de primit fisierul.");
	}
	
	private String getKeyIVForMode( String mode ) {
		Socket client;
		try {
			client = new Socket( keyManagerName , keyManagerPort );
			DataInputStream in = new DataInputStream( client.getInputStream() );
			DataOutputStream out = new DataOutputStream( client.getOutputStream() );
			out.writeUTF( mode );
			String keyIV = ( new AESWrapper() ).decrypt( in.readUTF(), encKey );
			System.out.println( "Am primit: " + keyIV );
			client.close();
			return keyIV;
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return "";
	}
	
}
