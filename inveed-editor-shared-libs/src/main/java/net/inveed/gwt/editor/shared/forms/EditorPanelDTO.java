package net.inveed.gwt.editor.shared.forms;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EditorPanelDTO {
	static final String P_NAME = "name";
	static final String P_ORDER = "order";
	static final String P_SECTIONS = "sections";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NAME)
	public final String name;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_ORDER)
	public final int order;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_SECTIONS)
	public final EditorSectionDTO[] sections;
	
	public EditorPanelDTO(
			@JsonProperty(P_NAME) String name, 
			@JsonProperty(P_ORDER) int order,
			@JsonProperty(P_SECTIONS) EditorSectionDTO[] sections) {
		this.name = name;
		this.order = order;
		this.sections = sections;
	}

	public EditorPanelDTO merge(EditorPanelDTO other) {
		HashMap<String, EditorSectionDTO> sections = new HashMap<>();
		for (EditorSectionDTO s : this.sections) {
			sections.put(s.name, s);
		}
		for (EditorSectionDTO s : other.sections) {
			sections.put(s.name, s);
		}
		EditorSectionDTO[] dtos = sections.values().toArray(new EditorSectionDTO[0]);
		return new EditorPanelDTO(name, order, dtos);
	}
}
