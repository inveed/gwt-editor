package net.inveed.gwt.editor.client.jsonrpc;

import java.util.logging.Logger;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsonHelper;

public class JsonRPCError implements IError {
	private static final Logger LOG = Logger.getLogger(JsonRPCError.class.getName());
	public static final String TYPE = "JsonRPCError";
	
	private final int code;
	private final String message;
	private final String extCode;
	private final JSONArray args;
	private final JSONValue data;
	
	public JsonRPCError(int code, String message, JSONValue data) {
		this.code = code;
		this.message = message;
		this.data = data;
		this.args = null;
		this.extCode = null;
	}
	
	public JsonRPCError(JSONObject error) {
		if (error.containsKey("code")) {
			JSONValue cv = error.get("code");
			if (cv.isNumber() != null) {
				this.code = (int) cv.isNumber().doubleValue();
			} else {
				LOG.warning("Got invalid error object with code '" + cv.toString() +"'");
				this.code = 0;
			}
		} else {
			LOG.warning("Got invalid error object without code");
			this.code = 0;
		}
		if (error.containsKey("message")) {
			this.message = error.get("message").toString();
		}
		else {
			LOG.warning("Got invalid error object without message");
			this.message = "";
		}
		if (error.containsKey("data")) {
			this.data = error.get("data");
		} else {
			this.data = null;
		}
		if (error.containsKey("extended")) {
			JSONValue data = error.get("extended");
			if (data.isObject() != null) {
				JSONObject ext = data.isObject();
				JSONArray arr = null;
				if (ext.containsKey("code")) {
					this.extCode = JsonHelper.safeGetString(ext.get("code"));
				} else {
					this.extCode = null;
				}
				if (ext.containsKey("args")) {
					JSONValue av = ext.get("args");
					arr = av.isArray();
					if (arr != null) {
						this.args = arr;
					} else {
						this.args = null;
					}
				} else {
					this.args = null;
				}
			} else {
				this.extCode = null;
				this.args = null;
			}
		} else {
			this.extCode = null;
			this.args = null;
		}
	}
	
	public int getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public String getLocalizedMessage() {
		return this.getMessage();//TODO:!!!
	}
	
	public JSONValue getData() {
		return this.data;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	public String getExtCode() {
		return this.extCode;
	}
	public JSONArray getArgs() {
		return this.args;
	}
}
