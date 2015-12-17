package usr.fiiaurelian.cryptography;

import usr.fiiaurelian.keymanager.Instance;
import usr.fiiaurelian.node.Node;

public abstract class OperationMode {
	
	protected OperationMode() {}
	
	public static final Integer BLOCK_SIZE_BYTES = 16;
	
	public abstract byte[] encrypt( byte[] plainText, String key, byte[] IV ) throws Exception;
	public abstract byte[] decrypt( byte[] encryptedText, String key, byte[] IV ) throws Exception;
	
	protected byte[] padding( byte[] array ) {
		int length = array.length + BLOCK_SIZE_BYTES - array.length % BLOCK_SIZE_BYTES;
		byte[] paddedArray = new byte[ length ];
		
		for( int i = 0; i < array.length; i++ )
			paddedArray[i] = array[i];
		
		return paddedArray; 
	}
	
	protected byte[] getBlock( byte[] source, int start, int end ) {
		byte[] result = new byte[ end - start ];
		for( int i = start; i < end; i++ )
			result[i - start] = source[i];
		return result;
	}
	
	protected byte[] xorBytes( byte[] one, byte[] another ) {
		int length = Math.max( one.length, another.length );
		byte[] resultBytes = new byte[ length ]; 
		for( int i = 0; i < length; i++ ) {
			resultBytes[i] = 0;
			if( i < one.length )
				resultBytes[i] = (byte) ( resultBytes[i] ^ one[i] );
			if( i < another.length )
				resultBytes[i] = (byte) ( resultBytes[i] ^ another[i] );
		}
		return resultBytes;
	}
	
	public static OperationMode getModeByName( String name ) {
		if( name.equals( Instance.CBC_REQUEST ) )
			return new CBCMode();
		if( name.equals( Instance.OFB_REQUEST ) )
			return new OFBMode();
		return null;
		
	}

}
