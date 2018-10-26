package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;

public class JSLong implements IJSObject, INativeObject<Long> {
	public static final String TYPE = "LONG"; 
	private final long value;
	
	public static JSLong parse(JSONValue v) {
		if (v.isNumber() != null) {
			return new JSLong((long) v.isNumber().doubleValue());
		} else if (v.isBoolean() != null) {
			return new JSLong((v.isBoolean().booleanValue() ? 1L : 0L));
		} else if (v.isString() != null) {
			return new JSLong(Long.parseLong(v.isString().stringValue()));
		} else {
			return null;
		}
	}
	
	public JSLong(long value) {
		this.value = value;
	}
	
	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		if (other.getType() != TYPE) {
			return this.getType().compareTo(other.getType());
		}
		
		JSLong o = (JSLong) other;
		return Long.compare(this.value, o.value);
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		return ((JSLong) other).value == this.value;
	}
	@Override
	public Long getValue() {
		return this.value;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public JSONValue getJSONValue() {
		return new JSONNumber(this.getValue());
	}
	
	@Override
	public String toString() {
		return Long.toString(this.value);
	}
	
	@Override
	public String getDisplayValue() {
		return this.toString();
	}
}
