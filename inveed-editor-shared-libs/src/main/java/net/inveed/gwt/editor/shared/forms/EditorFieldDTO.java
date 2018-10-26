package net.inveed.gwt.editor.shared.forms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class EditorFieldDTO {
	private static final String P_READONLY = "readonly";
	private static final String P_PROPERTY = "property";
	private static final String P_CUSTOMEDITOR = "editor";
	
	@JsonProperty(P_PROPERTY)
	public final String property;
	
	@JsonProperty(P_READONLY)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public final boolean readonly;
	
	@JsonProperty(P_CUSTOMEDITOR)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String customEditor;
	
	public EditorFieldDTO(
			@JsonProperty(P_PROPERTY) String property,
			@JsonProperty(P_CUSTOMEDITOR) String customEditor,
			@JsonProperty(P_READONLY) boolean readonly
			) {
		this.property = property;
		this.customEditor = customEditor;
		this.readonly = readonly;
	}

	public boolean equalsTo(EditorFieldDTO f) {
		if (f == null) {
			return false;
		}
		if (f.readonly != this.readonly) {
			return false;
		}
		if (!this.property.equals(f.property)) {
			return false;
		}
		if (this.customEditor != null && !this.customEditor.equals(f.customEditor)) {
			return false;
		} else if (f.customEditor != null) {
			return false;
		} else {
			return true;
		}
	}
}
