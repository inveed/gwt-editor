package net.inveed.gwt.server.editors;

import java.util.Map;

import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;
import net.inveed.gwt.server.annotations.editor.UIEditorSection;

public class AutoFormSectionPanelBuilder extends AbstractPanelBuilder<UIEditorSection> {	
	public AutoFormSectionPanelBuilder(String name, String parent, UIEditorSection annotation, int order) {
		super(name, parent, annotation, order);
	}
	@Override
	protected EditorSectionDTO buildRow(String viewName, Map<String, FieldInView> fields) {
		IEditorRowDTO[] rows = this.buildRows(viewName, fields);
		if (rows == null || rows.length == 0) {
			return null;
		}
		return new EditorSectionDTO(rows, this.name);
	}
	@Override
	public AbstractPanelBuilder<?> getSection(String name) {
		if (name.equals(this.name)) {
			return this;
		}
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
