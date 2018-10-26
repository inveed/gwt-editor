package net.inveed.gwt.editor.client.types;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.commons.UIConstants;

public class JSTimestamp implements IJSObject {
	public static final String TYPE = "TIMESTAMP";
	private Date value;
	private String jsonFormat;
	
	public static JSTimestamp parse(String v, String fmt) {
		try {
			Double d = Double.parseDouble(v);
			if (d != null) {
				return parse(new JSONNumber(d), fmt);
			}
		} catch (Exception e) {}
		return parse(new JSONString(v), fmt);
	}
	public static JSTimestamp parse(JSONValue json, String fmt) {
		if (json.isString() != null) {
			String v = json.isString().stringValue();
			Date d;
			try {
				if (UIConstants.FORMAT_TIMESTAMP_SECONDS.equals(fmt)) {
					d = new Date(Long.parseLong(v) * 1000L);
				} else if (UIConstants.FORMAT_TIMESTAMP_MILLS.equals(fmt)) {
					d = new Date(Long.parseLong(v));
				} else {
					DateTimeFormat f = DateTimeFormat.getFormat(fmt);
					d = f.parse(v);
				}
				return new JSTimestamp(d, fmt);
			} catch (Exception e) {
				return null;
			}
		} else if (json.isNumber() != null) {
			double v = json.isNumber().doubleValue();
			Date d;
			try {
				if (UIConstants.FORMAT_TIMESTAMP_SECONDS.equals(fmt)) {
					d = new Date(((long) v) * 1000L);
				} else if (UIConstants.FORMAT_TIMESTAMP_MILLS.equals(fmt)) {
					d = new Date((long) v);
				} else {
					//TODO: error
					return null;
				}
				return new JSTimestamp(d, fmt);
			} catch (Exception e) {
				return null;
			}
		} else {
			return null;
		}
	}
	public JSTimestamp(long timeMills, String jsonFormat) {
		this(new Date(timeMills), jsonFormat);
	}
	public JSTimestamp(Date date, String jsonFormat) {
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
		if (this.value == null) {
			return JSONNull.getInstance();
		} else if (UIConstants.FORMAT_TIMESTAMP_SECONDS.equals(this.jsonFormat)) {
			return new JSONNumber((double) (this.value.getTime() / 1000L));
		} else if (UIConstants.FORMAT_TIMESTAMP_MILLS.equals(this.jsonFormat)) {
			return new JSONNumber((double) this.value.getTime());
		} else {
			DateTimeFormat f = DateTimeFormat.getFormat(this.jsonFormat);
			return new JSONString(f.format(this.value));
		}
	}
	
	@Override
	public String toString() {
		return this.value.toString();
	}
	
	@Override
	public String getDisplayValue() {
		return this.toString();
	}
}
