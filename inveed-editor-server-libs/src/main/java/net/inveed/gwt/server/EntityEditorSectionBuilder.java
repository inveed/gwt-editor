package net.inveed.gwt.server;

import net.inveed.gwt.editor.shared.forms.EditorSectionDTO;

public class EntityEditorSectionBuilder {
	public String name;
	public int order;
	
	public EditorSectionDTO build() {
		return new EditorSectionDTO(this.name, this.order);
	}

}
