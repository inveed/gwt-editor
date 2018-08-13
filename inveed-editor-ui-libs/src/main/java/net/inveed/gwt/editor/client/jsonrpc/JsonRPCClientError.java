package net.inveed.gwt.editor.client.jsonrpc;

import net.inveed.gwt.editor.client.utils.IError;

public class JsonRPCClientError implements IError {
	public static final String TYPE = "JsonRPCClientError";
	
	public static final int ERR_CANNOT_PARSE_RESPONSE = 300;
	public static final int ERR_INVALID_JSON_RESPONSE = 400;
	public static final int ERR_ID_MISSING = 500;
	public static final int ERR_ID_INVALID = 501;
	public static final int ERR_ID_DIFFERENT = 502;
	public static final int ERR_ERROR_NOT_OBJECT = 600;
	public static final int ERR_UNKNOWN = 999;
	
	private final int code;
	public JsonRPCClientError(int code) {
		this.code = code;
	}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getCode() {
		return this.code;
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
