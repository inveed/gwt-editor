package net.inveed.gwt.editor.shared.forms.rows;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;

public class EditorTabContainerDTO implements IEditorRowDTO {
	public static final String P_TABS = "tabs";
	
	@JsonProperty(P_TABS)
	public final EditorSectionDTO[] tabs;
	
	public EditorTabContainerDTO(
			@JsonProperty(P_TABS) EditorSectionDTO[] tabs) {
		this.tabs = tabs;
	}
	
	@Override
	public boolean equalsTo(IEditorRowDTO obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		EditorTabContainerDTO dto = (EditorTabContainerDTO) obj;
		if (dto.tabs == null && this.tabs == null) {
			return true;
		} else if (dto.tabs == null || this.tabs == null) {
			return false;
		} else if (dto.tabs.length != this.tabs.length) {
			return false;
		}
		for (int i = 0; i < this.tabs.length; i++) {
			EditorSectionDTO rcdto = this.tabs[i];
			EditorSectionDTO rodto = dto.tabs[i];
			if (!rcdto.equalsTo(rodto)) {
				return false;
			}
		}
		return true;
	}
}
