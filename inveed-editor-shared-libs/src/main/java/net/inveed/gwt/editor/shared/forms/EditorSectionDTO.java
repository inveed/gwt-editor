package net.inveed.gwt.editor.shared.forms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EditorSectionDTO {
	static final String P_NAME = "name";
	static final String P_ORDER = "order";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NAME)
	public final String name;

	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_ORDER)
	public final int order;
	
	public EditorSectionDTO(
			@JsonProperty(P_NAME) String name, 
			@JsonProperty(P_ORDER) int order) {
		this.name = name;
		this.order = order;
	}
}
