package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONValue;

public interface IJSObject extends Comparable<IJSObject> {

	String getType();
	
	JSONValue getJSONValue();
	
	boolean isEquals(IJSObject other);

	String getDisplayValue();
}
