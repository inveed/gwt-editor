package net.inveed.gwt.editor.client.editor.auto;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;

public abstract class AbstractEditorRow<T extends IEditorRowDTO> {
	private final T dto;
	private final EntityModel model;
	private final IContainer container;
	
	public AbstractEditorRow(T dto, EntityModel model, IContainer parent) {
		this.dto = dto;
		this.model = model;
		this.container = parent;
	}
	
	public abstract void findFields(List<AutoFormFieldInfo> fields);
	
	public abstract void bld();
	
	public abstract boolean validate();
	public abstract void applyChanges();
	
	public abstract boolean isModified();
	
	public abstract void persist(JsonRPCTransaction transaction);
	
	protected void addToParent2(Widget w) {
		if (this.getContainer() == null) {
			return;
		}
		if (this.getContainer().getWidget() == null) {
			return;
		}
		this.getContainer().getWidget().add(w);
	}
	
	public abstract void bind(JSEntity entity, String viewName);
	
	public T getDTO() {
		return this.dto;
	}
	
	public EntityModel getEntityModel() {
		return this.model;
	}
	
	public IContainer getContainer() {
		return this.container;
	}
	
	public abstract void setEnabled(boolean value);
}
