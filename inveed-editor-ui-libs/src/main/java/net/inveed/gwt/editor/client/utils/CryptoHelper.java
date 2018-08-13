package net.inveed.gwt.editor.client.utils;


import com.google.gwt.user.client.Random;
import com.googlecode.gwt.crypto.bouncycastle.digests.SHA1Digest;
import com.googlecode.gwt.crypto.bouncycastle.util.encoders.Base64;

public class CryptoHelper {
	public static final byte[] generateRandomSeed(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("length cannot be < 1");
		}
		byte[] ret = new byte[length];
		int cycles = length/4;
		int mod = Math.floorMod(length, 4);
		if (mod > 0) {
			cycles ++;
		}
		for (int i = 0; i < cycles; i++) {
			int random = Random.nextInt();
			int offset = i * 4;
			if (offset + 4 > ret.length) {
				offset = ret.length - 4;
			}
			ByteArrayConvertor.intToByteArray(random, ret, offset);
		}
		return ret;
	}
	
	public static final byte[] sha1(byte[] data) {
		SHA1Digest dig = new SHA1Digest(); 
	    byte[] hash = new byte[dig.getDigestSize()];
	    dig.update(data, 0, data.length);
	    dig.doFinal(hash, 0);
	    return hash;
	}
	
	public static final String encodeBase64(byte[] data) {
		return new String(Base64.encode(data));
	}
	
	public static final byte[] decodeBase64(String base64String) {
		return Base64.decode(base64String);
	}
}
