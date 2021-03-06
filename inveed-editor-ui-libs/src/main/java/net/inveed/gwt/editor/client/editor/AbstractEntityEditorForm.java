package net.inveed.gwt.editor.client.editor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;

import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor.ValueChangeListener;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.JSEntity;

public abstract class AbstractEntityEditorForm extends Composite {
	private final String viewName;
	private JSEntity entity;
	
	private final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
	
	public AbstractEntityEditorForm(String viewName) {
		this.viewName = viewName;
	}
	
	public final void bind(JSEntity entity) {
		this.entity = entity;
		this.bind();
	}

	protected abstract void bind();
	
	public JSEntity getEntity() {
		return this.entity;
	}
	
	public String getViewName() {
		return this.viewName;
	}

	public abstract void applyChanges();
	
	public abstract void persist(JsonRPCTransaction transaction);
	
	public abstract boolean validate();
	
	public abstract Integer getRequestedWidth();
	public abstract Integer getRequestedHeight();

	public void addValueChangedListener(ValueChangeListener l) {
		this.valueChangeListeners.add(l);
	}
	
	protected void onValueChanged() {
		for (ValueChangeListener l : this.valueChangeListeners) {
			l.onValueChanged();
		}
	}

	public abstract boolean isModified();

	public abstract void setEnabled(boolean value);
}