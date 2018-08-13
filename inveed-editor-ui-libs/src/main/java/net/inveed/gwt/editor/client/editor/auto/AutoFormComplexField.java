package net.inveed.gwt.editor.client.editor.auto;

import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityFormView.PropertyInView;

public class AutoFormComplexField extends AutoFormField {

	private AbstractPropertyEditor<?, ?> editor;
	private boolean enabled;
	
	public AutoFormComplexField(PropertyInView fm, AbstractPropertyEditor<?, ?> editor) {
		super(fm);
		this.editor = editor;
	}

	@Override
	public AbstractPropertyEditor<?, ?> getEditor() {
		return this.editor;
	}

	@Override
	public void setEnabled(boolean value) {
		this.enabled = value;
		editor.setEnabled(value);
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

}
