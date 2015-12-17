package usr.fiiaurelian.utils;

public class HexData {
	
	public static byte[] hexafy( String hexText ) {
		byte[] result = new byte[ hexText.length() / 2 ];
		int index = 0;
		for( int i = 0; i < hexText.length(); i += 2 ) {
			result[ index++ ] = ( byte ) ( Character.digit( hexText.charAt( i ) , 16 ) * 16 + Character.digit( hexText.charAt( i+1 ), 16 ) );
		}
		return result;
	}

}
