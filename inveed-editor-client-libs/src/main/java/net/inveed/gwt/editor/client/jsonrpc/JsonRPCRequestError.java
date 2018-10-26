package net.inveed.gwt.editor.client.jsonrpc;

import net.inveed.gwt.editor.client.utils.IError;

public final class JsonRPCRequestError implements IError {
	public static final String TYPE = "JsonRPCRequestError";
	
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
