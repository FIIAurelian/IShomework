package usr.fiiaurelian.node;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Node extends Thread {
	
	private static final int TIMEOUT = 10000;
	
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
			System.err.println( ioException.getMessage() );
		}
	}
	
	@Override
	public void run() {
		try {
			Selector selector = Selector.open();
			serverSocket.register(selector, SelectionKey.OP_ACCEPT);
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
						processMessage( key );
					}
				}
			}
			
		} catch (IOException ioException) {
			System.err.println( ioException.getMessage() );
		}
	}
	
	private void acceptClient( ServerSocketChannel serverSocket, Selector selector, SelectionKey key ) throws IOException {
		SocketChannel client = serverSocket.accept();
		client.configureBlocking( false );
		client.socket().setTcpNoDelay( true );
		client.register( selector, SelectionKey.OP_READ );
	}
	
	private void processMessage( SelectionKey key ) throws IOException {
		SocketChannel channel = ( SocketChannel ) key.channel();
		ByteBuffer byteBuffer = ByteBuffer.allocate( 1024 );
		int readedBytes = channel.read( byteBuffer );
		while( readedBytes > -1 ) {
			byteBuffer.flip();
			while( byteBuffer.hasRemaining() ) {
				System.out.print( (char) byteBuffer.get() );
			}
			byteBuffer.clear();
			readedBytes = channel.read( byteBuffer );
		}
		System.out.println();
		channel.close();
	}
	
}
