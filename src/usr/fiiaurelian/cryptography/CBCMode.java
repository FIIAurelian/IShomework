package usr.fiiaurelian.cryptography;

import usr.fiiaurelian.utils.HexData;

public class CBCMode extends OperationMode {
	
	public CBCMode() {}
	
	public byte[] encrypt( byte[] plainText, String key, byte[] IV ) throws Exception {
		plainText = padding( plainText );
		
		byte[] encryptedText = new byte[ plainText.length ];
		byte[] lastBlock = IV;
		int numberOfBlocks = plainText.length / BLOCK_SIZE_BYTES;
		int position = 0;
		for( int i = 0; i < numberOfBlocks; i++ ) {
			byte[] block = getBlock( plainText, BLOCK_SIZE_BYTES * i, BLOCK_SIZE_BYTES * ( i + 1 ) );
			block = xorBytes( block, lastBlock );
			byte[] encryptedBlock = ( new AESWrapper() ).encrypt( block, key );
			for( int j = 0; j < BLOCK_SIZE_BYTES; j++ ) {
				encryptedText[ position++ ] = encryptedBlock[j];
				lastBlock[j] = encryptedBlock[j];
			}
		}
		
		return encryptedText;
	}
	
	public byte[] decrypt( byte[] encryptedText, String key, byte[] IV ) throws Exception {
		if( encryptedText.length % BLOCK_SIZE_BYTES > 0 ) 
			throw new Exception("Invalid length. Must be multiple of 16.");
		
		byte[] decryptedText = new byte[ encryptedText.length ];
		byte[] lastBlock = IV;
		int numberOfBlocks = encryptedText.length / BLOCK_SIZE_BYTES;
		int position = 0;
		for( int i = 0; i < numberOfBlocks; i++ ) {
			byte[] block = getBlock( encryptedText, BLOCK_SIZE_BYTES * i, BLOCK_SIZE_BYTES * ( i + 1 ) );
			byte[] decryptedBlock = ( new AESWrapper() ).decrypt( block, key );
			decryptedBlock = xorBytes( decryptedBlock, lastBlock );
			for( int j = 0; j < BLOCK_SIZE_BYTES; j++ )
				decryptedText[ position++ ] = decryptedBlock[j];
			lastBlock = block;
		}
		
		return decryptedText;
	}
	
	public static void main(String[] args) throws Exception {
		String message = "Aurelian este seful lui Nicolae Berendea!";
		String key = "00112233445566778889aabbccddeeff";
		String padding = "00112233445566778889aabbcccdeeff";
		CBCMode cbc = new CBCMode();
		byte[] encrypted = cbc.encrypt( message.getBytes(), key, HexData.hexafy( padding ) );
		byte[] decrypted = cbc.decrypt( encrypted, key, HexData.hexafy( padding ) );
		System.out.println( new String(decrypted) );
		
	}


}
