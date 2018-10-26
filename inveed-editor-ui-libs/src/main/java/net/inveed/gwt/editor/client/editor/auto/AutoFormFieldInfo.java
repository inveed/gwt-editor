package net.inveed.gwt.editor.client.editor.auto;

import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;

public class AutoFormFieldInfo {
	private final IPropertyDescriptor<?> property;
	private final AbstractFormPropertyEditor<?, ?> editor;
	private boolean enabled;
	
	public AutoFormFieldInfo(IPropertyDescriptor<?> fm, AbstractFormPropertyEditor<?, ?> e) {
		if (fm == null) {
			throw new NullPointerException("PropertyInView is null");
		}
		this.property = fm;
		if (e == null) {
			throw new NullPointerException("IPropertyEditorField is null");
		}
		this.editor = e;
		this.enabled = true;
		e.setTitle(fm.getDisplayName(e.getViewName()));
	}
	
	public IPropertyDescriptor<?> getPropertyDescriptor() {
		return this.property;
	}
	
	
	public void setEnabled(boolean value) {
		this.editor.setEnabled(value);
		this.enabled = value;
	}

	public boolean isEnabled() {
		return this.enabled;
	}
	
	public AbstractPropertyEditor<?, ?> getEditor() {
		return this.editor;
	}

	public void setGridWidth(int gridSize) {
		this.editor.setGrid("s" + gridSize);
	}
}
