package net.inveed.gwt.editor.shared.forms.rows;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class EditorListRowDTO implements IEditorRowDTO {
	private static final String P_PROPERTY = "property";
	
	@JsonProperty(P_PROPERTY)
	public final String property;
	
	public EditorListRowDTO(
			@JsonProperty(P_PROPERTY) String property
			) {
		this.property = property;
	}
	@Override
	public boolean equalsTo(IEditorRowDTO obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		EditorListRowDTO dto = (EditorListRowDTO) obj;
		return this.property.equals(dto.property);
	}
}
