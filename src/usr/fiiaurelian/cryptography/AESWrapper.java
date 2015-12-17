package usr.fiiaurelian.cryptography;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

import usr.fiiaurelian.utils.HexData;


public class AESWrapper extends OperationMode {
	
	private static final String AES_ALGORITHM = "AES/ECB/NoPadding";
	private static final String KEY_ALGORITHM = "AES";
	
	private Cipher AESCipher;
	
	public String encrypt( String plainText, String key ) throws Exception {
		byte[] encryptedBytes = encrypt( plainText.getBytes(), key );
		return new String( Base64.toBase64String( encryptedBytes ) );
	}
	
	public String decrypt( String encryptedText, String key ) throws Exception {
		byte[] encryptedTextBytes = Base64.decode( encryptedText );
		byte[] decryptedBytes = decrypt( encryptedTextBytes, key );
		return new String( decryptedBytes );
	}
	
	public byte[] encrypt( byte[] plainText, String key ) throws Exception {
		plainText = padding( plainText );
		AESCipher = Cipher.getInstance( AES_ALGORITHM );
		AESCipher.init( Cipher.ENCRYPT_MODE, generateKeyFromString( key ) );
		return AESCipher.doFinal( plainText );
	}
	
	public byte[] decrypt( byte[] encryptedText, String key ) throws Exception {
		AESCipher = Cipher.getInstance( AES_ALGORITHM );
		AESCipher.init( Cipher.DECRYPT_MODE, generateKeyFromString( key ) );
		return AESCipher.doFinal( encryptedText ); 
	}
	
	private SecretKey generateKeyFromString( String key ) {
		SecretKey secretKey = new SecretKeySpec( HexData.hexafy( key ), KEY_ALGORITHM );
		return secretKey;
	}
	
	public static void main(String[] args) {
		String plain = "Aurelian este seful lui Nicolae Berendea!";
		String key = "astaestecheiaxxx";
		String encrypted;
		AESWrapper aes = new AESWrapper();
		try {
			encrypted = aes.encrypt( plain, key );
			System.out.println( encrypted );
			byte[] buffer = encrypted.getBytes();
			String decrypted = aes.decrypt( new String( buffer ), key );
			System.out.println( decrypted );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] encrypt(byte[] plainText, String key, byte[] IV)
			throws Exception {
		return null;
	}

	@Override
	public byte[] decrypt(byte[] encryptedText, String key, byte[] IV)
			throws Exception {
		return null;
	}
	

}
