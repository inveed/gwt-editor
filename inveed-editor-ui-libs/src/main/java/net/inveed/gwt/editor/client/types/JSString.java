package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.JsonHelper;

public class JSString implements IJSObject, INativeObject<String> {

	public static final String TYPE = "STRING";
	private final String value;
	
	public static JSString parse(JSONValue v) {
		String s = JsonHelper.safeGetString(v);
		if (s == null) {
			return null;
		}
		return new JSString(s);
	}
	public JSString(String v) {
		this.value = v;
	}

	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		if (other.getType() != TYPE) {
			return this.getType().compareTo(other.getType());
		}
		return this.value.compareTo(((JSString)other).getValue());
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		return ((JSString) other).value.equals(this.value);
	}
	
	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public JSONValue getJSONValue() {
		if (this.getValue() == null) {
			return JSONNull.getInstance();
		} else {
			return new JSONString(this.getValue());
		}
	}
	
	@Override
	public String toString() {
		return this.value;
	}
}
