package net.inveed.gwt.editor.client.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.commons.UIConstants;

public class LinkedEntitiesList extends EntityList {
	private static final Logger LOG = Logger.getLogger(LinkedEntitiesList.class.getName());
	
	private JSEntityList value;
	private JSEntity entity;
	private LinkedEntitiesListPropertyModel property;	
	

	public void bind( JSEntity entity, LinkedEntitiesListPropertyModel property, String viewName) {
		super.bind(property.getTargetEntityType(), entity.getEntityManager(), viewName);
		this.entity = entity;
		this.property = property;
		this.excludeProperty(property.getMappedByProperty());
		this.setTableTitle(property.getDisplayName(viewName));
		
		this.setValue(property.getValue(entity));
	}

	@Override
	protected void refresh() {
		this.fill(this.getValue().getValue());
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}
	
	public void setValue(JSEntityList v) {
		this.value = v;
		if (v == null) {
			this.fill(new ArrayList<>());
			return;
		}
		
		LOG.fine("Setting non-null list");
		List<JSEntity> list = v.getValue();
		this.fill(list);
		LOG.fine("Fill finished");
	}
	
	public JSEntityList getValue() {
		return this.value;
	}
	
	@Override
	protected void onDeleteComplete() {
		ArrayList<JSEntity> l = new ArrayList<>(this.getValue().getValue());
		for (JSEntity e : l) {
			if (e.isDeleted()) {
				this.getValue().remove(e);
			}
		}
		
		super.onDeleteComplete();
	}

	@Override
	protected void openNewItemEditor(EntityModel model) {
		JSEntity entity = new JSEntity(model, this.entity.getEntityManager());
		entity.setProperty(this.property.getMappedByProperty(), this.entity);
		EntityEditorDialog dialog = new EntityEditorDialog(entity, UIConstants.FORM_EMBEDDED_CREATE);
		dialog.setViewEditName(UIConstants.FORM_EMBEDDED_EDIT);
		Promise<Boolean, IError> p = dialog.show();
		p.thenApply((Boolean v) -> {
			if (v != null) {
				if (v) {
					this.getValue().add(entity);
					this.fill(this.getValue().getValue());
					this.refresh();
				}
			}
			return null;
		});
	}

	public void setGrid(String string) {
		this.grid.setGrid(string);
	}
}
