package net.inveed.gwt.editor.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumModelDTO implements Serializable {
	private static final long serialVersionUID = -7206342709013229630L;

	public final Map<String, String> values;
	public final String name;
	
	public EnumModelDTO(
			@JsonProperty("values") Map<String, String> values, 
			@JsonProperty("name") String name) {
		this.values = Collections.unmodifiableMap(values);
		this.name = name;
	}
}
