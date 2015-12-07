package usr.fiiaurelian.node;

import static org.apache.commons.cli.OptionBuilder.create;
import static org.apache.commons.cli.OptionBuilder.hasArg;
import static org.apache.commons.cli.OptionBuilder.isRequired;
import static org.apache.commons.cli.OptionBuilder.withArgName;
import static org.apache.commons.cli.OptionBuilder.withDescription;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
	
	private static final String ENC_KEY 		 	  = "k3";
	private static final String PORT    		 	  = "port";
	private static final String KEY_MANAGER  	 	  = "keyManager";
	private static final char   KEY_MANAGER_DELIMITER = ':'; 
	
	private String[] 	args;
	private Options 	options;
	private CommandLine commandLine;
	
	@SuppressWarnings("deprecation")
	public CLI( String[] args ) {
		this.args = args;
		options =new Options();
		
		withArgName( "Port" );
		withDescription( "This option sets the port at which Key Manager listen." );
		hasArg();
		isRequired();
		options.addOption( create( PORT ) );
		
		withArgName( "Change Key" );
		withDescription( "This option sets the key to propagate CBC of OFB keys." );
		hasArg();
		isRequired();
		options.addOption( create( ENC_KEY ) );
		
		withArgName( "Key Manager Address" );
		withDescription( "The server address of Key Manager. Pattern = name:port" );
		hasArg();
		isRequired();
		options.addOption( create( KEY_MANAGER ) );
	}
	
	public void parse() {
		CommandLineParser parser = new DefaultParser();
		
		try {
			commandLine = parser.parse( options, args );
		} catch( ParseException parseException ) {
			System.err.println( parseException.getMessage() );
		}
		
	}
	
	public Integer getPort() {
		return Integer.parseInt( commandLine.getOptionValue( PORT ) );
	}
	
	public String getENCKey() {
		return commandLine.getOptionValue( ENC_KEY );
	}
	
	public String getKeyManagerName() {
		int delimiterPosition = commandLine.getOptionValue( KEY_MANAGER ).indexOf( KEY_MANAGER_DELIMITER );
		return commandLine.getOptionValue( KEY_MANAGER ).substring( 0, delimiterPosition );
	}
	
	public Integer getKeyManagerPort() {
		int delimiterPosition = commandLine.getOptionValue( KEY_MANAGER ).indexOf( KEY_MANAGER_DELIMITER );
		String serverPort = commandLine.getOptionValue( KEY_MANAGER ).substring( delimiterPosition + 1 );
		return Integer.parseInt( serverPort );
	}
	
}
