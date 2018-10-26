package net.inveed.gwt.editor.client.utils;

public class HttpClientError implements IError {
	public static final String TYPE = "HttpClientError";
	public static final int ERR_EMPTY_RESPONSE = -1;
	public static final int ERR_STATUS = 200;
	
	public HttpClientError (int code) {
		
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalizedMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
}
