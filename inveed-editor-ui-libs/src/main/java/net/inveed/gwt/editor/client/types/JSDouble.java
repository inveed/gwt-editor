package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONValue;

public class JSDouble implements IJSObject, INativeObject<Double> {

	public static final String TYPE = "DOUBLE";
	private final double value;
	
	public static JSDouble parse(JSONValue v) {
		if (v.isNumber() != null) {
			return new JSDouble( v.isNumber().doubleValue());
		} else if (v.isBoolean() != null) {
			return new JSDouble((v.isBoolean().booleanValue() ? 1D : 0D));
		} else if (v.isString() != null) {
			return new JSDouble(Double.parseDouble(v.isString().stringValue()));
		} else {
			return null;
		}
	}
	public JSDouble(double v) {
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
		JSDouble dbl = (JSDouble) other;
		return Double.compare(this.value, dbl.value);
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		return ((JSDouble) other).value == this.value;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public JSONValue getJSONValue() {
		return new JSONNumber(this.getValue());
	}
	
	@Override
	public String toString() {
		return Double.toString(this.value);
	}

}
