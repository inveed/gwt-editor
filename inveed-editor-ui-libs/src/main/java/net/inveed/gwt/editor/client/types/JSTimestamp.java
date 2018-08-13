package net.inveed.gwt.editor.client.types;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSTimestamp implements IJSObject {
	public static final String TYPE = "TIMESTAMP";
	private Date value;
	private DateTimeFormat jsonFormat;
	
	public static JSTimestamp parse(String v, DateTimeFormat fmt) {
		try {
			Double d = Double.parseDouble(v);
			if (d != null) {
				return parse(new JSONNumber(d), fmt);
			}
		} catch (Exception e) {}
		return parse(new JSONString(v), fmt);
	}
	public static JSTimestamp parse(JSONValue json, DateTimeFormat fmt) {
		if (json.isString() != null) {
			String v = json.isString().stringValue();
			try {
				Date d = fmt.parse(v);
				if (d != null) {
					return new JSTimestamp(d, fmt);
				}
			} catch (Exception e) {}
			return null;
		} else if (json.isNumber() != null) {
			return new JSTimestamp(new Date((long)(json.isNumber().doubleValue() * 1000D)), null);
		} else {
			return null;
		}
	}
	public JSTimestamp(Date date, DateTimeFormat jsonFormat) {
		if (date == null) {
			throw new NullPointerException("date cannot be null");
		}
		this.value = date;
		this.jsonFormat = jsonFormat;
	}

	@Override
	public int compareTo(IJSObject other) {
		if (other.getType() != TYPE) {
			return TYPE.compareTo(other.getType());
		}
		JSTimestamp od = (JSTimestamp) other;
		return this.value.compareTo(od.value);
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		return ((JSTimestamp) other).value.equals(this.value);
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	public Date getValue() {
		return this.value;
	}

	@Override
	public JSONValue getJSONValue() {
		if (this.jsonFormat == null) {
			return new JSONNumber((double) this.value.getTime() / 1000D);
		} else {
			return new JSONString(this.jsonFormat.format(this.value));
		}
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
}
