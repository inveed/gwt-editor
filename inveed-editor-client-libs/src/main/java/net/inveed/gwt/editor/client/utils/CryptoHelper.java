package net.inveed.gwt.editor.client.utils;


import java.util.Arrays;

import com.google.gwt.user.client.Random;
import com.googlecode.gwt.crypto.bouncycastle.BufferedBlockCipher;
import com.googlecode.gwt.crypto.bouncycastle.CipherParameters;
import com.googlecode.gwt.crypto.bouncycastle.DataLengthException;
import com.googlecode.gwt.crypto.bouncycastle.InvalidCipherTextException;
import com.googlecode.gwt.crypto.bouncycastle.digests.SHA1Digest;
import com.googlecode.gwt.crypto.bouncycastle.engines.AESEngine;
import com.googlecode.gwt.crypto.bouncycastle.modes.CBCBlockCipher;
import com.googlecode.gwt.crypto.bouncycastle.paddings.BlockCipherPadding;
import com.googlecode.gwt.crypto.bouncycastle.paddings.PKCS7Padding;
import com.googlecode.gwt.crypto.bouncycastle.paddings.PaddedBufferedBlockCipher;
import com.googlecode.gwt.crypto.bouncycastle.params.KeyParameter;
import com.googlecode.gwt.crypto.bouncycastle.params.ParametersWithIV;
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
	
	public static final byte[] encryptAESCBCPKCS7(byte[] clear,
													byte[] key, 
													int keyOffset, 
													int keyLen,
													byte[] iv,
													int ivOffset,
													int ivLen
													) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		CipherParameters params = 
				new ParametersWithIV(new KeyParameter(key, keyOffset, keyLen), iv, ivOffset, ivLen);
		BlockCipherPadding padding = new PKCS7Padding();
		BufferedBlockCipher cipher = 
				new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
		cipher.reset();
		cipher.init(true, params);
		
		byte[] buffer = new byte[cipher.getOutputSize(clear.length)];
        int len = cipher.processBytes(clear, 0, clear.length, buffer, 0);
        len += cipher.doFinal(buffer, len);
        return Arrays.copyOfRange(buffer, 0, len);
	}
}
