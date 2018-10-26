package net.inveed.gwt.editor.client.editor;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;

public abstract class CustomEntityEditorForm extends AbstractEntityEditorForm {
	private List<AbstractPropertyEditor<?, ?>> fields;

	public CustomEntityEditorForm(String viewName) {
		super(viewName);
		this.fields = new ArrayList<>();
	}
	
	protected void addPropertyEditor(AbstractPropertyEditor<?, ?> e) {
		this.fields.add(e);
	}

	@Override
	protected void bind() {
		for (AbstractPropertyEditor<?, ?> fld : this.fields) {
			fld.bind(this.getEntity(), this.getViewName());
		}
	}
	
	@Override
	public boolean isModified() {
		for (AbstractPropertyEditor<?, ?> f : this.fields) {
			if (f.isModified())
				return true;
		}
		return false;
	}
	
	@Override
	public boolean validate() {
		boolean ret = true;
		for (AbstractPropertyEditor<?, ?> fld : this.fields) {
			if (!fld.validate()) {
				ret = false;
			}
		}
		return ret;
	}
	public void applyChanges() {
		for (AbstractPropertyEditor<?, ?> fld : this.fields) {
			if (!fld.isModified()) {
				continue;
			}
			fld.applyChanges();
		}
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		this.getEntity().save(transaction);
		for (AbstractPropertyEditor<?, ?> fld : this.fields) {
			fld.save(transaction);
		}
	}
	@Override
	public Integer getRequestedWidth() {
		return null;
	}

	@Override
	public Integer getRequestedHeight() {
		return null;
	}
}
