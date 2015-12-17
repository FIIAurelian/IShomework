package usr.fiiaurelian.node;

public class CommandArguments {
	
	private static final String SEND = "send";
	
	private final boolean sendType;
	private final String  mode;
	private final String  addressName;
	private final Integer addressPort;
	private final String  filePath;
	
	private CommandArguments(boolean sendType, String mode, String addressName,
			Integer addressPort, String filePath) {
		this.sendType = sendType;
		this.mode = mode;
		this.addressName = addressName;
		this.addressPort = addressPort;
		this.filePath = filePath;
	}

	public boolean isSendType() {
		return sendType;
	}

	public String getMode() {
		return mode;
	}

	public String getAddressName() {
		return addressName;
	}

	public Integer getAddressPort() {
		return addressPort;
	}

	public String getFilePath() {
		return filePath;
	}

	public static CommandArguments getArgumentsForCommand( String command ) {
		boolean sendType = false;
		String mode = "";
		String addressName = "";
		Integer addressPort = 0;
		String filePath = "";
		if( command.startsWith( SEND ) == true ) {
			String[] tokens = command.split( " " );
			sendType = true;
			mode = tokens[5].substring( 0, 3 );
			addressName = tokens[3].split( ":" )[0];
			addressPort = Integer.parseInt( tokens[3].split( ":" )[1] );
			filePath = tokens[1];
		} else {
			mode = command.substring( 0, 3 );
		}
		return new CommandArguments( sendType, mode, addressName, addressPort, filePath );
	}
	
}
