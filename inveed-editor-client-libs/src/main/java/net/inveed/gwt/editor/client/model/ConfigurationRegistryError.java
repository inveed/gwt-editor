package net.inveed.gwt.editor.client.model;

import net.inveed.gwt.editor.client.utils.IError;

public final class ConfigurationRegistryError implements IError {
	public static final String TYPE = "ConfigurationRegistryError";
	public static final int ERR_INVALID_URL = 1000;
	public static final int ERR_INVALID_STATE = 10001;
	public static final int ERR_CANNOT_PARSE_ENTITY_MODEL = 10010;
	public static final int ERR_CANNOT_PARSE_ENUM_MODEL = 10011;

	public ConfigurationRegistryError(int code) {
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
