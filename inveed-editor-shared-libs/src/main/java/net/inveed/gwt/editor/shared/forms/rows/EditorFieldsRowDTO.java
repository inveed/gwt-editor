package net.inveed.gwt.editor.shared.forms.rows;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public final class EditorFieldsRowDTO implements IEditorRowDTO {
	private static final String P_FIELDS = "fields";
	
	@JsonProperty(P_FIELDS)
	public final EditorFieldDTO[] fields;
	
	public EditorFieldsRowDTO(
			@JsonProperty(P_FIELDS) EditorFieldDTO[] fields) {
		this.fields = fields;
	}
	
	@Override
	public boolean equalsTo(IEditorRowDTO obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		EditorFieldsRowDTO dto = (EditorFieldsRowDTO) obj;
		if (dto.fields == null && this.fields == null) {
			return true;
		} else if (dto.fields == null || this.fields == null) {
			return false;
		} else if (dto.fields.length != this.fields.length) {
			return false;
		}
		for (int i = 0; i < this.fields.length; i++) {
			EditorFieldDTO f1 = this.fields[i];
			EditorFieldDTO f2 = dto.fields[i];
			if (!f1.equalsTo(f2)) {
				return false;
			}
		}
		return true;
	}
}
