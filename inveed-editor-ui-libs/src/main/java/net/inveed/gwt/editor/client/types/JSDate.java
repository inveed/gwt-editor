package net.inveed.gwt.editor.client.types;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSDate implements IJSObject, INativeObject<Date> {
	public static final String TYPE = "DATE";
	private Date value;
	private DateTimeFormat jsonFormat;
	
	
	public static JSDate parse(String v, DateTimeFormat fmt) {
		if (v == null) {
			return null;
		}
		try {
			Date d = fmt.parse(v);
			if (d != null) {
				return new JSDate(d, fmt);
			}
		} catch (Exception e) {}
		try {
			Double dv = Double.parseDouble(v);
			if (dv != null) {
				return new JSDate(new Date((long)(dv * 1000D)), null);
			}
		} catch (Exception e) {}
		return null;
	}
	public static JSDate parse(JSONValue json, DateTimeFormat fmt) {
		if (json.isString() != null) {
			String v = json.isString().stringValue();
			try {
				Date d = fmt.parse(v);
				if (d != null) {
					return new JSDate(d, fmt);
				}
			} catch (Exception e) {}
			return null;
		} else if (json.isNumber() != null) {
			return new JSDate(new Date((long)(json.isNumber().doubleValue() * 1000D)), null);
		} else {
			return null;
		}
	}
	public JSDate(Date date, DateTimeFormat jsonFormat) {
		if (date == null) {
			throw new NullPointerException("date cannot be null");
		}
		this.value = date;
		this.jsonFormat = jsonFormat;
	}

	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		if (other.getType() != TYPE) {
			return this.getType().compareTo(other.getType());
		}
		JSDate od = (JSDate) other;
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
		
		return ((JSDate) other).value.equals(this.value);
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
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
