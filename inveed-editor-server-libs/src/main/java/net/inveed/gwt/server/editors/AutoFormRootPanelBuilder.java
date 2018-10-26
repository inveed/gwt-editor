package net.inveed.gwt.server.editors;

import java.util.Map;

import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;
import net.inveed.gwt.server.annotations.editor.UIAutoEditorView;
import net.inveed.gwt.server.annotations.editor.UIEditorSection;

public class AutoFormRootPanelBuilder extends AbstractPanelBuilder<UIEditorSection> {	
	public AutoFormRootPanelBuilder(String name, String parent, UIEditorSection annotation, int order) {
		super(name, parent, annotation, order);
	}
	
	public AutoFormViewDTO buildRow(String name, Integer widht, Integer hight, Map<String, FieldInView> fields) {
		IEditorRowDTO[] rows = buildRows(name, fields);
		if (rows == null) {
			//TODO: LOG
			return null;
		}
		return new AutoFormViewDTO(rows, name, widht, hight);
	}
	
	public AutoFormViewDTO buildRow(String viewName, Map<String, FieldInView> fields) {
		return this.buildRow(viewName, null, null, fields);
	}
	
	@Override
	public AbstractPanelBuilder<?> getSection(String name) {
		if (name.length() == 0) {
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
