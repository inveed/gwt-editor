package net.inveed.gwt.editor.client.utils;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JsonHelper {
	public static Long safeGetLong(JSONObject json, String property) {
		if (!json.containsKey(property)) {
			return null;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return null;
		}
		if (n.isNumber() == null) {
			return null;
		}
		return (long) n.isNumber().doubleValue();
	}
	
	public static Integer safeGetInt(JSONObject json, String property) {
		if (!json.containsKey(property)) {
			return null;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return null;
		}
		if (n.isNumber() == null) {
			return null;
		}
		return (int) n.isNumber().doubleValue();
	}
	
	public static Boolean safeGetBoolean(JSONObject json, String property) {
		if (!json.containsKey(property)) {
			return null;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return null;
		}
		if (n.isBoolean() == null) {
			return null;
		}
		return n.isBoolean().booleanValue();
	}
	
	public static boolean safeGetBoolean(JSONObject json, String property, boolean dfl) {
		if (!json.containsKey(property)) {
			return dfl;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return dfl;
		}
		if (n.isBoolean() == null) {
			return dfl;
		}
		return n.isBoolean().booleanValue();
	}
	
	public static JSONObject safeGetObject(JSONObject json, String property) {
		if (!json.containsKey(property)) {
			return null;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return null;
		}
		if (n.isObject() == null) {
			return null;
		}
		return n.isObject();
	}
	
	public static String safeGetString(JSONObject json, String property) {
		if (!json.containsKey(property)) {
			return null;
		}
		JSONValue n = json.get(property);
		if (n == null) {
			return null;
		}
		if (n.isString() == null) {
			return null;
		}
		return n.isString().stringValue();
	}
	
	public static String safeGetString(JSONValue n) {
		if (n == null) {
			return null;
		}
		if (n.isString() != null) {
			return n.isString().stringValue();
		} else if (n.isNumber() != null) {
			return ((Double) n.isNumber().doubleValue()).toString();
		}
		return null;
	}
}
