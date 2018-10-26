package net.inveed.gwt.editor.shared.forms.rows;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;

@JsonTypeInfo(
		  use = JsonTypeInfo.Id.NAME,
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "#type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = AutoFormViewDTO.class, name = "form"),
    @JsonSubTypes.Type(value = EditorListRowDTO.class, name = "list"),
    @JsonSubTypes.Type(value = EditorFieldsRowDTO.class, name = "fields"),
    @JsonSubTypes.Type(value = EditorTabContainerDTO.class, name = "tabpanel"),
    @JsonSubTypes.Type(value = EditorSectionDTO.class, name = "section")
})
public interface IEditorRowDTO {
	boolean equalsTo(IEditorRowDTO rodto);
}