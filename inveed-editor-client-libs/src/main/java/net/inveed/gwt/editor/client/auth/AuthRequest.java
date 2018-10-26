package net.inveed.gwt.editor.client.auth;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.http.client.URL;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.utils.ByteArrayConvertor;
import net.inveed.gwt.editor.client.utils.CryptoHelper;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;
import net.inveed.gwt.editor.shared.auth.AuthorizationResponse;

public class AuthRequest {
	public static interface AuthorizationResponseMapper extends ObjectMapper<AuthorizationResponse> {}
	
	private final String login;
	private final String password;
	private final String url;
	
	private boolean secondTry;
	
	private final PromiseImpl<AuthorizationResponse, IError> promise;
	
	public AuthRequest(String login, String password, String url) {
		this.login = login;
		this.password = password;
		this.url = url;
		this.promise = new PromiseImpl<>();
		this.secondTry = false;
	}
	
	public Promise<AuthorizationResponse, IError> send() {
		byte[] ecnryptedPwd = ConfigurationRegistry.INSTANCE.encrypt(this.password.getBytes());
		byte[] encryptedTime = ConfigurationRegistry.INSTANCE.encrypt(
				ByteArrayConvertor.longToByteArray(ConfigurationRegistry.INSTANCE.getServerTimeMills()));
        
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append('?');
		
		sb.append("user=");
		sb.append(URL.encodeQueryString(this.login));
		sb.append('&');
		
		sb.append("pwd=");
		sb.append(URL.encodeQueryString(CryptoHelper.encodeBase64(ecnryptedPwd)));
		sb.append('&');
		
		sb.append("t=");
		sb.append(URL.encodeQueryString(CryptoHelper.encodeBase64(encryptedTime)));
		sb.append('&');
		sb.append("sk=");
		sb.append(URL.encodeQueryString(CryptoHelper.encodeBase64(ConfigurationRegistry.INSTANCE.getSKEncrypted())));
		
		Promise<RequestResult, HTTPError> httpPromise = JsHttpClient.doGet(sb.toString(), false);
		httpPromise.thenApply((r)-> {
			parseResponse(r);
			return null;
		});
		
		httpPromise.onError((e,t)->{
			this.promise.error(null, null);
			return null;
		});
		return this.promise;
	}
	
	private void parseResponse(RequestResult resp) {
		AuthorizationResponse dto;
		try {
			AuthorizationResponseMapper mapper = GWT.create(AuthorizationResponseMapper.class);
			dto = mapper.read(resp.response.getText());
		} catch (Exception e) {
			this.promise.error(null, null);
			return;
		}
		if (dto.code == AuthorizationResponse.AUTH_OK) {
			this.promise.complete(dto);
			return;
		}
		if (!this.secondTry && dto.code == AuthorizationResponse.AUTH_RSA_ERROR) {
			this.secondTry = true;
			Promise<Void, IError> seUpdate = 
					ConfigurationRegistry.INSTANCE.updateServerEnvironment();
			seUpdate.thenApply((v)->{
				
				this.send();
				return null;
			});
			seUpdate.onError((e,t)->{
				this.promise.error(null, null);
				return null;
			});
			return;
		}
		this.promise.error(null, null);
	}
}
