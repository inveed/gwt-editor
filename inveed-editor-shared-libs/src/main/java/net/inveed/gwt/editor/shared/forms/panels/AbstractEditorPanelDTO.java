package net.inveed.gwt.editor.shared.forms.panels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;

@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME,
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "#type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutoFormViewDTO.class, name = "form"),
    @JsonSubTypes.Type(value = EditorSectionDTO.class, name = "section")
})
public abstract class AbstractEditorPanelDTO {
	static final String P_ROWS = "rows";
	
	@JsonProperty(P_ROWS)
	public final IEditorRowDTO[] rows;
	
	public AbstractEditorPanelDTO(IEditorRowDTO[] rows) {
		this.rows = rows;
	}
}
