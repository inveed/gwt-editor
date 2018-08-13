package net.inveed.gwt.editor.client.editor.auto;

import java.util.UUID;

import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;

import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityFormView.PropertyInView;

public class AutoFormSimpleField extends AutoFormField {
	
	private final AbstractFormPropertyEditor<?, ?> editor;
	private final FormLabel label;
	private final FlowPanel panel;
	private boolean enabled;
	
	public AutoFormSimpleField(PropertyInView fm, AbstractFormPropertyEditor<?, ?> e) {
		super(fm);
		if (e == null) {
			throw new NullPointerException("IPropertyEditorField is null");
		}
		this.editor = e;
		
		String uid = UUID.randomUUID().toString().replaceAll("-", "");
		
		this.label = new FormLabel();
		this.label.setFor(uid);
		this.label.addStyleName("col-lg-2");
		this.label.setText(fm.getDisplayName());
		
		this.panel = new FlowPanel();
		this.editor.setId(uid);
		this.panel.add(this.editor);
	}
	
	public void setEnabled(boolean value) {
		this.editor.setEnabled(value);
		this.enabled = value;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	public FormLabel getLabel() {
		return this.label;
	}
	public FlowPanel getPanel() {
		return this.panel;
	}
	
	public void setFullWidth(boolean fw) {
		if (fw) {
			this.panel.addStyleName("col-lg-10");
		} else {
			this.panel.addStyleName("col-lg-4");
		}
	}

	@Override
	public AbstractPropertyEditor<?, ?> getEditor() {
		return this.editor;
	}
}
