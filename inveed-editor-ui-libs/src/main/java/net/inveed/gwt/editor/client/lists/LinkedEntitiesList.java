package net.inveed.gwt.editor.client.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.view.client.ListDataProvider;

import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.shared.UIConstants;

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
	}
	
	@Override
	protected void refresh() {
		//this.grid.getView().setRedraw(true);
		//this.grid.getView().refresh();
		this.fill(this.getValue().getValue());
	}
	
	@Override
	public void initialize() {
		super.initialize();
		//this.setTableTitle("");//this.property.getDisplayName(this.view.getName()));
	}
	
	public void setValue(JSEntityList v) {
		this.value = v;
		if (v == null) {
			//this.grid.setTotalRows(0);	
			ListDataProvider<JSEntity> data = new ListDataProvider<>();
			data.addDataDisplay(this.grid);
		
			return;
		}
		
		LOG.fine("Setting non-null list");
		List<JSEntity> list = v.getValue();
		this.fill(list);
		//int hval = 55 * (list.size() + 5);
		//this.grid.setHeight(hval + "px");
		LOG.fine("Fill finished");
	}
	/*
	private void setGridList(List<JSEntity> list) {
		LOG.fine("Array found, size = " + list.size());
		this.grid.setRowCount(list.size());	
		this.grid.setRowData(0, list);
	}*/
	
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
		EntityEditorDialog dialog = new EntityEditorDialog(entity);
		
		Promise<Boolean, IError> p = dialog.show(UIConstants.FORM_EMBEDDED_CREATE);
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
}
