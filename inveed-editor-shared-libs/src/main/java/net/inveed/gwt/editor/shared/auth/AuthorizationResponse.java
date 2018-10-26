package net.inveed.gwt.editor.shared.auth;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorizationResponse implements Serializable {
	public static final int AUTH_OK = 0;
	public static final int AUTH_REJECT = -1;
	public static final int AUTH_RSA_ERROR = -10;
	public static final int AUTH_TIME_DEVIATION = -20;
	
	
	private static final String F_CODE = "code";
	private static final String F_LOGIN = "login";
	private static final String F_TOKEN = "token";
	private static final String F_PUBLIC_DATA = "publicData";

	private static final long serialVersionUID = -4935309671133811124L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_CODE)
	public final int code;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_LOGIN)
	public final String login;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_TOKEN)
	public final String token;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_PUBLIC_DATA)
	public final String publicData;
	
	public AuthorizationResponse(int code) {
		this.code = code;
		this.login = null;
		this.token = null;
		this.publicData = null;
	}
	public AuthorizationResponse(
			@JsonProperty(F_CODE) int code,
			@JsonProperty(F_LOGIN) String login,
			@JsonProperty(F_TOKEN) String token, 
			@JsonProperty(F_PUBLIC_DATA)  String publicData) {
		this.code = code;
		this.login = login;
		this.token = token;
		this.publicData = publicData;
	}
}
