package usr.fiiaurelian.cryptography;

import usr.fiiaurelian.utils.HexData;

public class OFBMode extends OperationMode {
	
	public OFBMode() {}
	
	public byte[] encrypt( byte[] plainText, String key, byte[] IV ) throws Exception {
		plainText = padding( plainText );
		
		byte[] encryptedText = new byte[ plainText.length ];
		byte[] lastBlock = IV;
		int numberOfBlocks = plainText.length / BLOCK_SIZE_BYTES;
		int position = 0;
		for( int i = 0; i < numberOfBlocks; i++ ) {
			byte[] block = getBlock( plainText, BLOCK_SIZE_BYTES * i, BLOCK_SIZE_BYTES * ( i + 1 ) );
			byte[] encryptedBlock = ( new AESWrapper() ).encrypt( lastBlock, key );
			block = xorBytes( encryptedBlock, block );
			for( int j = 0; j < BLOCK_SIZE_BYTES; j++ ) {
				encryptedText[ position++ ] = block[j];
				lastBlock[j] = encryptedBlock[j];
			}
		}
		
		return encryptedText;
	}
	
	public byte[] decrypt( byte[] encryptedText, String key, byte[] IV ) throws Exception {
		return encrypt( encryptedText, key, IV );
	}

	
	public static void main(String[] args) throws Exception {
		String message = "Aurelian este seful lui Nicolae Berendea!";
		String key = "00112233445566778889aabbccddeeff";
		String padding = "00112233445566778889aabbcccdeeff";
		OFBMode ofb = new OFBMode();
		
		byte[] encrypted = ofb.encrypt( message.getBytes(), key, HexData.hexafy( padding ) );
		byte[] decrypted = ofb.decrypt( encrypted, key, HexData.hexafy( padding ) );
		System.out.println( new String(decrypted) );
		
	}

}
