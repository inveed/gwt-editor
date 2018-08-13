package net.inveed.gwt.server;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.shared.forms.EditorPanelDTO;
import net.inveed.gwt.editor.shared.forms.EditorSectionDTO;

public class EntityEditorPanelBuilder {
	public String name;
	public int order;
	public final List<EntityEditorSectionBuilder> sections = new ArrayList<>();
	
	public EditorPanelDTO build() {
		ArrayList<EditorSectionDTO> slist = new ArrayList<>();
		for (EntityEditorSectionBuilder b : sections) {
			slist.add(b.build());
		}
		return new EditorPanelDTO(this.name, this.order, slist.toArray(new EditorSectionDTO[0]));
	}

}
