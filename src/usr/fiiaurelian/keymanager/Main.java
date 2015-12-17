package usr.fiiaurelian.keymanager;

public class Main {
	
	public static void main( String[] args ) {
		CLI commandLineInterface = new CLI( args );
		commandLineInterface.parse();
		
		Instance server = new Instance( commandLineInterface.getPort() )
							  .withCBCMode( "00112233445566778889aabbccddeeff", "01020304050607080102030405060708" )
							  .withOFBMode( "00112233445566778889aabbccddeeff", "01020304050607080102030405060708" )
							  .withEncryptionKey( commandLineInterface.getENCKey() );
		server.start();
	}
}
