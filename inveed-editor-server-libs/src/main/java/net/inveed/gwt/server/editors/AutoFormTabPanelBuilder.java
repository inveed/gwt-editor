package net.inveed.gwt.server.editors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorTabContainerDTO;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;
import net.inveed.gwt.server.annotations.editor.UIEditorTabPanel;

public class AutoFormTabPanelBuilder extends AbstractPanelBuilder<UIEditorTabPanel>{		
	public AutoFormTabPanelBuilder(String name, String parent, UIEditorTabPanel annotation, int order) {
		super(name, parent, annotation, order);
	}

	@Override
	protected IEditorRowDTO buildRow(String viewName, Map<String, FieldInView> fields) {
		List<EditorSectionDTO> sections = new ArrayList<>();
		for (AbstractPanelBuilder<?> child : this.children) {
			if (!(child instanceof AutoFormSectionPanelBuilder)) {
				//TODO: LOG!
				continue;
			}
			AutoFormSectionPanelBuilder sb = (AutoFormSectionPanelBuilder) child;
			sections.add(sb.buildRow(viewName, fields));
		}
		if (sections.size() == 0) {
			return null;
		}
		return new EditorTabContainerDTO(sections.toArray(new EditorSectionDTO[0]));
	}

	@Override
	public AbstractPanelBuilder<?> getSection(String name) {
		if (this.children == null) {
			return null;
		}
		for (AbstractPanelBuilder<?> child : this.children) {
			AbstractPanelBuilder<?> ret = child.getSection(name);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}
}
