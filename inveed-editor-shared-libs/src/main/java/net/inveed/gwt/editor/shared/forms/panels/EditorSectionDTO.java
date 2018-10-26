package net.inveed.gwt.editor.shared.forms.panels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;

public final class EditorSectionDTO extends AbstractEditorPanelDTO implements IEditorRowDTO {
	static final String P_TITLE = "title";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_TITLE)
	public final String title;
	
	public EditorSectionDTO(
			@JsonProperty(AbstractEditorPanelDTO.P_ROWS) IEditorRowDTO[] rows,
			@JsonProperty(P_TITLE) String title) {
		super(rows);
		this.title = title;
	}
	
	@Override
	public boolean equalsTo(IEditorRowDTO obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		EditorSectionDTO dto = (EditorSectionDTO) obj;
		if (dto.rows == null && this.rows == null) {
			return true;
		} else if (dto.rows == null || this.rows == null) {
			return false;
		} else if (dto.rows.length != this.rows.length) {
			return false;
		}
		for (int i = 0; i < this.rows.length; i++) {
			IEditorRowDTO rcdto = this.rows[i];
			IEditorRowDTO rodto = dto.rows[i];
			if (!rcdto.equalsTo(rodto)) {
				return false;
			}
		}
		return true;
	}
}
