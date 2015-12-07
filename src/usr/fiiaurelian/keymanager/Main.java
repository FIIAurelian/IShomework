package usr.fiiaurelian.keymanager;

public class Main {
	
	public static void main( String[] args ) {
		CLI commandLineInterface = new CLI( args );
		commandLineInterface.parse();
		
		Instance server = new Instance( commandLineInterface.getPort() )
							  .withCBCMode( "00112233445566778889aabbccddeeff", "0102030405060708" )
							  .withOFBMode( "ffeeddccbbaa98887766554433221100", "0102030405060708" );
		server.start();
	}
}
