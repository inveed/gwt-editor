package net.inveed.gwt.editor.client.types;

import java.util.HashMap;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class JSMap<V extends IJSObject> extends HashMap<String, V> implements IJSObject {
	public static final String TYPE = "MAP";
	private static final long serialVersionUID = -5251441113981629478L;

	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		
		if (other.getType() != TYPE) {
			return this.getType().compareTo(other.getType());
		}
		
		return 0;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public JSONValue getJSONValue() {
		JSONObject ret = new JSONObject();
		for (String k : this.keySet()) {
			V v = this.get(k);
			if (v == null) {
				continue;
			}
			ret.put(k, v.getJSONValue());
		}
		return ret;
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		JSMap<?> om = (JSMap<?>) other;
		if (om.size() != this.size()) {
			return false;
		}
		JSONValue v1 = om.getJSONValue();
		JSONValue v2 = this.getJSONValue();
		return v1.toString().equals(v2.toString());
	}
	
	public JSMap<V> clone() {
		JSMap<V> ret = new JSMap<>();
		ret.putAll(this);
		return ret;
	}
	
	@Override
	public String toString() {
		return "...";
	}
	
	@Override
	public String getDisplayValue() {
		return this.toString();
	}
}
