package net.inveed.gwt.editor.client.editor.auto;

import java.util.List;

import gwt.material.design.client.ui.MaterialRow;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.lists.LinkedEntitiesList;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.editor.shared.forms.rows.EditorListRowDTO;

public class AutoListRow extends AbstractEditorRow<EditorListRowDTO>  {
	private LinkedEntitiesListPropertyModel property;
	private MaterialRow row;
	private LinkedEntitiesList entitiesList;
	
	public AutoListRow(EditorListRowDTO dto, EntityModel model,  AbstractAutoFormContainer<?> container) {
		super(dto, model, container);
		IPropertyDescriptor<?> pd = model.getPropertyDescriptor(dto.property);
		if (pd == null) {
			//TODO: LOG
			return;
		}
		if (pd.getClass() != LinkedEntitiesListPropertyModel.class) {
			//TODO: LOG
			return;
		}
		this.row = new MaterialRow();
		this.property = (LinkedEntitiesListPropertyModel) pd;
		this.entitiesList = new LinkedEntitiesList();
		this.entitiesList.setAbsolutHeight(250);
		this.entitiesList.setGrid("s12");
		this.row.add(this.entitiesList);
	}
	
	@Override
	public void bld() {
		this.addToParent2(this.row);
	}
	
	@Override
	public void findFields(List<AutoFormFieldInfo> fields) {
	}
	
	@Override
	public void bind(JSEntity entity, String viewName) {
		this.entitiesList.bind(entity, this.property, UIConstants.LIST);
		this.entitiesList.initialize();
	}

	@Override
	public void setEnabled(boolean value) {
	}
	
	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public void applyChanges() {
	}
	
	@Override
	public boolean isModified() {
		return false;
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
	}

}
