package net.inveed.gwt.server.editors;

import java.util.Collection;
import java.util.HashMap;

import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.server.annotations.editor.UIAutoEditorView;
import net.inveed.gwt.server.annotations.editor.UIEditorField;
import net.inveed.gwt.server.editors.AutoFormBuilder.Field;

public class AutoFormViewBuilder {
	public final String name;
	public final UIAutoEditorView viewAnnotation;
	private AutoFormViewBuilder parent;
	
	public AutoFormViewBuilder(UIAutoEditorView viewAnnotation) {
		this.name = viewAnnotation.viewName();
		this.viewAnnotation = viewAnnotation;
	}
	
	public AutoFormViewBuilder(String viewName) {
		this.name = viewName;
		this.viewAnnotation = null;
	}
	
	public void setParent(AutoFormViewBuilder parent) {
		this.parent = parent;
	}
	
	public AutoFormViewBuilder getParent() {
		return this.parent;
	}
	
	public AutoFormViewDTO build(
			Collection<AutoFormBuilder.Field> fields,
			AutoFormRootPanelBuilder root
			) {
		
		//Creating a fields' map
		HashMap<String, FieldInView> fldInViews = new HashMap<>();
		for (Field fld : fields) {
			UIEditorField fa = findFieldAnnotation(fld);
			if (fa == null) {
				continue;
			}

			AbstractPanelBuilder<?> section = root.getSection(fa.container());
			if (section == null) {
				//TODO: LOG container not found
				section = root;
			}

			FieldInView fldInView = new FieldInView(fld.name, fld.builder, fld.property, fa, section);
			fldInViews.put(fld.name, fldInView);
		}
		return root.buildRow(this.name, this.getWidth(), this.getHeight(), fldInViews);
	}
	
	private Integer getWidth() {
		Integer ret = this.viewAnnotation == null ? null : (this.viewAnnotation.width() == 0 ? null : this.viewAnnotation.width());
		if (ret == null && this.getParent() != null) {
			return this.getParent().getWidth();
		} else {
			return ret;
		}
	}
	
	private Integer getHeight() {
		Integer ret = this.viewAnnotation == null ? null : (this.viewAnnotation.heigh() == 0 ? null : this.viewAnnotation.heigh());
		if (ret == null && this.getParent() != null) {
			return this.getParent().getHeight();
		} else {
			return ret;
		}
	}
	
	private UIEditorField findFieldAnnotation(Field f) {
		UIEditorField ret = f.annotations.get(this.name);
		if (ret != null) {
			return ret;
		}
		if (this.getParent() == null) {
			return null;
		}
		return this.getParent().findFieldAnnotation(f);
	}
}
