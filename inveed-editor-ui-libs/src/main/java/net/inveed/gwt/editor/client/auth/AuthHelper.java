package net.inveed.gwt.editor.client.auth;

import java.util.UUID;

import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.CryptoHelper;
import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;

public class AuthHelper {
	public static interface IAuthCallback {
		Promise<Boolean, IError> authenticate();
	}
	private static final String URL_PREPARE = "/prepare";
	public static final AuthHelper INSTANCE = new AuthHelper();
	private IAuthCallback authCallback;
	
	public void setAuthCallback(IAuthCallback value) {
		this.authCallback = value;
	}
	public IAuthCallback getAuthCallback() {
		return this.authCallback;
	}
	
	private String token;
	
	private AuthHelper() {}
	
	public String getToken() {
		return this.token;
	}
	
	public Promise<Boolean, IError> authenticate(String username, String password, String url) {
		String prepareUrl = url + URL_PREPARE + "?user=" + username;
		PromiseImpl<Boolean, IError> ret = new PromiseImpl<>();
		
		JsHttpClient.doGet(prepareUrl, false).thenApply((r)-> {
			if (r.response.getStatusCode() != 200) {
				ret.onError(null);
				return null;
			}
			JSONValue resp = JSONParser.parseStrict(r.response.getText());
			if (resp == null) {
				ret.onError(null);
				return null;
			}
			JSONObject o = resp.isObject();
			if (o == null) {
				ret.onError(null);
				return null;
			}
			String salt = JsonHelper.safeGetString(o, "salt");
			Long time = JsonHelper.safeGetLong(o, "time");
			
			if (salt == null || time == null) {
				ret.onError(null);
				return null;
			}
			completeAuth(username, password, url, time, salt, ret);
			return null;
		}).onError((e,t)->{
			ret.onError(null);
			return null;
		});
		
		return ret;
	}
	
	
	private void completeAuth(String username, String password, String url, long time, String salt, PromiseImpl<Boolean, IError> p) {
		byte[] saltBytes =  CryptoHelper.decodeBase64(salt);
		byte[] clearPasswordBytes = password.getBytes();
		byte[] pbytes = new byte[saltBytes.length + clearPasswordBytes.length];
		
		System.arraycopy(saltBytes, 0, pbytes, 0, saltBytes.length);
		System.arraycopy(clearPasswordBytes, 0, pbytes, saltBytes.length, clearPasswordBytes.length);
		byte[] pwdHash = CryptoHelper.sha1(pbytes);
		
		String rnd = UUID.randomUUID().toString().replaceAll("-", "");
		String pwdConcat = salt + "$" + CryptoHelper.encodeBase64(pwdHash) + "##" + Long.toString(time) + "##" + rnd;
		
		String pwdSigned = CryptoHelper.encodeBase64(CryptoHelper.sha1(pwdConcat.getBytes()));
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append('?');
		
		sb.append("user=");
		sb.append(username);
		sb.append('&');
		
		sb.append("pwd=");
		sb.append(URL.encodeQueryString(pwdSigned));
		sb.append('&');
		
		sb.append("time=");
		sb.append(time);
		sb.append('&');
		
		sb.append("rnd=");
		sb.append(rnd);
		
		JsHttpClient.doGet(sb.toString(), false).thenApply((r)-> {
			if (r.response.getStatusCode() != 200) {
				p.onError(null);
				return null;
			}
			token = r.response.getText();
			p.complete(true);
			return null;
		}).onError((e,t)->{
			p.onError(null);
			return null;
		});
	}
}
