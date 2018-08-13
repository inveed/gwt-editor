package net.inveed.gwt.editor.client.editor.auto;

import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityFormView.PropertyInView;

public abstract class AutoFormField {
	private final PropertyInView inView;
	
	public AutoFormField(PropertyInView fm) {
		if (fm == null) {
			throw new NullPointerException("PropertyInView is null");
		}
		this.inView = fm;
	}
	
	public PropertyInView getPropertyInView() {
		return this.inView;
	}
	
	public int getOrder() {
		return inView.attr.order;
	}
	
	public abstract AbstractPropertyEditor<?, ?> getEditor();
	public abstract void setEnabled(boolean value);
	public abstract boolean isEnabled();
}
