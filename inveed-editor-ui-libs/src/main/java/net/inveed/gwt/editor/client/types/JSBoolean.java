package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONValue;

public class JSBoolean implements IJSObject, INativeObject<Boolean> {
	public static final String TYPE = "BOOLEAN";
	public static final JSBoolean TRUE = new JSBoolean(true);
	public static final JSBoolean FALSE = new JSBoolean(false);
	
	private boolean v;
	
	public static JSBoolean parse(String sv) {
		if (sv == null) {
			return null;
		}
		sv = sv.trim().toLowerCase();
		if (sv.equals("yes") || sv.equals("true") || sv.equals("y") || sv.equals("t")) {
			return JSBoolean.TRUE;
		} else {
			return JSBoolean.FALSE;
		}
	}
	public static JSBoolean parse(JSONValue v) {
		if (v.isBoolean() != null) {
			return new JSBoolean(v.isBoolean().booleanValue());
		}
		if (v.isString() != null) {
			String sv = v.isString().stringValue();
			return parse(sv);
		}
		return null;
	}
	private JSBoolean(boolean v) {
		this.v = v;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public Boolean getValue() {
		return this.v;
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		return ((JSBoolean) other).v == this.v;
	}
	
	@Override
	public String toString() {
		if (this.getValue()) {
			return "YES";
		} else {
			return "NO";
		}
	}

	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		if (other.getType() != TYPE) {
			return this.getType().compareTo(other.getType());
		}
		boolean ov = ((JSBoolean) other).getValue();
		if (!this.v && ov) {
			return -1;
		} else if (this.v && !ov) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public JSONValue getJSONValue() {
		return JSONBoolean.getInstance(this.getValue());
	}
}
