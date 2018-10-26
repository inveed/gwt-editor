package net.inveed.gwt.server.auth;

import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;
import java.util.Base64;


import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.encodings.PKCS1Encoding;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.RSAKeyParameters;


import net.inveed.commons.NumberedException;
import net.inveed.commons.utils.ByteArrayConvertor;
import net.inveed.commons.utils.CryptoUtil;
import net.inveed.gwt.editor.shared.auth.AuthorizationResponse;

public class SecurityProvider {
	
	private final ICredentialProvider credentialProvider;
	private final RSAPrivateKey pkey;
	public SecurityProvider(ICredentialProvider cp, RSAPrivateKey pkey) {
		this.credentialProvider = cp;
		this.pkey = pkey;
	}
	
	public AuthorizationResponse authorize(String username, String pwd, String sk, String time) throws NumberedException, InvalidCipherTextException {
		if (username == null) {
			throw new NullPointerException("username");
		}
		if (pwd == null) {
			throw new NullPointerException("pwdHash");
		}
		if (sk == null) {
			throw new NullPointerException("rnd");
		}
		if (time == null) {
			throw new NullPointerException("rnd");
		}
		
		username = username.trim().toLowerCase();
		if (username.length() == 0) {
			throw new NullPointerException("username");
		}
		
		byte[] skEncrypted = Base64.getDecoder().decode(sk);
		byte[] pwdEncrypted = Base64.getDecoder().decode(pwd);
		byte[] timeEncrypted = Base64.getDecoder().decode(time);
		
		byte[] skClear;
		RSAKeyParameters privateKey = new RSAKeyParameters(true, this.pkey.getModulus(), this.pkey.getPrivateExponent());
		
		AsymmetricBlockCipher eng = new PKCS1Encoding(new RSAEngine());
		eng.init(false, privateKey);
		skClear = eng.processBlock(skEncrypted, 0, skEncrypted.length);
		
		byte[] aesKey = Arrays.copyOfRange(skClear, 0, 16);
		byte[] aesIV = Arrays.copyOfRange(skClear, 16, 32);
		byte[] pwdClear = CryptoUtil.decryptAESCBC_PKCS7Padding(pwdEncrypted, aesKey, aesIV);
		byte[] timeClear = CryptoUtil.decryptAESCBC_PKCS7Padding(timeEncrypted, aesKey, aesIV);
		long timeMills = ByteArrayConvertor.byteArrayToLong(timeClear);
		
		long currentTimeSeconds = System.currentTimeMillis();
		long diff = currentTimeSeconds > timeMills ? currentTimeSeconds - timeMills : timeMills - currentTimeSeconds;
		if (diff > 120000) {
			return new AuthorizationResponse(AuthorizationResponse.AUTH_TIME_DEVIATION);
		}
		IUserCredential u = this.credentialProvider.getUserCredential(username, new String(pwdClear));
		if (u == null) {
			return new AuthorizationResponse(AuthorizationResponse.AUTH_REJECT);
		}
		return u.authorize();
	}
}

