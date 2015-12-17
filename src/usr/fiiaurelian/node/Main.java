package usr.fiiaurelian.node;

public class Main {
	
	public static void main(String[] args) {
		CLI commandLineInterface = new CLI( args );
		commandLineInterface.parse();
		
		Node server = new Node( commandLineInterface.getPort() )
						       .withEncryptionKey( commandLineInterface.getENCKey() );
		server.setKeyManagerName( commandLineInterface.getKeyManagerName() );
		server.setKeyManagerPort( commandLineInterface.getKeyManagerPort() );
		server.start();
	}
}
