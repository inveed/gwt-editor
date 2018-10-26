package net.inveed.gwt.editor.client.auth;


import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.shared.auth.AuthorizationResponse;


public class AuthHelper {
	public static interface IAuthCallback {
		Promise<AuthorizationResponse, IError> authenticate();
	}
	public static final AuthHelper INSTANCE = new AuthHelper();
	private IAuthCallback authCallback;
	
	public void setAuthCallback(IAuthCallback value) {
		this.authCallback = value;
	}
	
	public IAuthCallback getAuthCallback() {
		return this.authCallback;
	}
	
	private String token;
	private String publicData;
	
	private AuthHelper() {}
	
	public String getToken() {
		return this.token;
	}
	public String getPublicData() {
		return this.publicData;
	}
	
	public Promise<AuthorizationResponse, IError> authenticate(String username, String password, String url) {
		AuthRequest req = new AuthRequest(username, password, url);
		Promise<AuthorizationResponse, IError> ret = req.send();
		ret.thenApply((v)->{
			if (v != null) {
				this.token = v.token;
				this.publicData = v.publicData;
			}
			return null;
		});
		return ret;
	}
}
