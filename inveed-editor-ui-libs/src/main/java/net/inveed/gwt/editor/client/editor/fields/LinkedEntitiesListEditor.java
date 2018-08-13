package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.lists.LinkedEntitiesList;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.shared.UIConstants;

public class LinkedEntitiesListEditor extends AbstractPropertyEditor<LinkedEntitiesListPropertyModel, JSEntityList> {
	
	private LinkedEntitiesList entitiesList;
	private JSEntity entity;
	
	public LinkedEntitiesListEditor() {
		this.entitiesList = new LinkedEntitiesList();
		this.add(this.entitiesList);
	}

	public void bind(JSEntity entity, LinkedEntitiesListPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.entity = entity;
		this.entitiesList.bind(this.entity, this.getProperty(), UIConstants.LIST);
		this.entitiesList.initialize();
		if (this.getOriginalValue() != null) {
			this.entitiesList.setValue(this.getOriginalValue());
		}
	}
	
	@Override
	public void setTitle(String title) {
		this.entitiesList.setTitle(title);
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.entitiesList;
	}

	@Override
	public void setValue(String v) {
	}
	
	@Override
	public boolean isModified() {
		return false; // добавление нового объекта не является изменением свойства
	}
	
	@Override
	public boolean validate() {
		return true; //TODO: реализовать валидацию по кол-ву элементов!
	}

	@Override
	public JSEntityList getValue() {
		return this.entitiesList.getValue();
	}

	@Override
	public void setEnabled(boolean value) {
		this.entitiesList.setVisible(!value);
	}
}
