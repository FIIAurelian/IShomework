package usr.fiiaurelian.keymanager;

import static org.apache.commons.cli.OptionBuilder.create;
import static org.apache.commons.cli.OptionBuilder.withArgName;
import static org.apache.commons.cli.OptionBuilder.withDescription;
import static org.apache.commons.cli.OptionBuilder.hasArg;
import static org.apache.commons.cli.OptionBuilder.isRequired;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CLI {
	
	private static String PORT    = "port";
	private static String CBC_KEY = "k1";
	private static String OFB_KEY = "k2";
	private static String ENC_KEY = "k3";
	private static String CBC_IV  = "iv1";
	private static String OFB_IV  = "iv2";
	
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
		
		withArgName( "CBC Key" );
		withDescription( "This option sets the key for CBC mode." );
		hasArg();
		options.addOption( create( CBC_KEY ) );
		
		withArgName( "OFB Key" );
		withDescription( "This option sets the key for OFB mode." );
		hasArg();
		options.addOption( create( OFB_KEY ) );
		
		withArgName( "Change Key" );
		withDescription( "This option sets the key to propagate CBC of OFB keys." );
		hasArg();
		isRequired();
		options.addOption( create( ENC_KEY ) );
		
		withArgName( "CBC Initialization Vector" );
		withDescription( "This option sets the initialization vector for CBC mode." );
		hasArg();
		options.addOption( create( CBC_IV ) );
		
		withArgName( "OFB Initialization Vector" );
		withDescription( "This option sets the initialization vector for OFB mode." );
		hasArg();
		options.addOption( create( OFB_IV ) );
		
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
	
	public String getCBCKey() {
		return commandLine.getOptionValue( CBC_KEY ); 
	}
	
	public String getOFBKey() {
		return commandLine.getOptionValue( OFB_KEY ); 
	}
	
	public String getENCKey() {
		return commandLine.getOptionValue( ENC_KEY );
	}
	
	public String getCBCInitVector() {
		return commandLine.getOptionValue( CBC_IV );
	}
	
	public String getOFBInitVector() {
		return commandLine.getOptionValue( CBC_IV );
	}
	
}
