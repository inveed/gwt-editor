package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.LinkedEntitiesListEditor;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class LinkedEntitiesListPropertyModel extends AbstractPropertyModel<JSEntityList> {
	private EntityModel targetEntityType;
	private String mappedBy;
	
	public LinkedEntitiesListPropertyModel(PropertyModelDTO model, String name, EntityModel entity) {
		super(model, name, entity);
		this.targetEntityType = ConfigurationRegistry.INSTANCE.getModel(model.attributes.referencedEntityName);
		this.mappedBy = model.attributes.mappedBy;
	}
	
	@Override
	public boolean isReadonly() {
		return true;
	}
	@Override
	public boolean isReadonly(boolean isNewObject) {
		return true;
	}
	
	public String getMappedByProperty() {
		return this.mappedBy;
	}
	public EntityModel getTargetEntityType() {
		return this.targetEntityType;
	}
	
	@Override
	public LinkedEntitiesListEditor createEditor() {
		return new LinkedEntitiesListEditor();
	}

	@Override
	public JSEntityList convertToJSObject(JSONValue jv, EntityManager em) {
		if (jv == null) {
			return null;
		}
		return JSEntityList.parse(jv, this.targetEntityType, em);
	}

	@Override
	public JSEntityList getRawValue(JSEntity entity) {
		IJSObject ret = entity.getProperty(this.getName());
		if (ret == null) {
			return null;
		}
		if (!JSEntityList.TYPE.equals(ret.getType())) {
			return null;
		}
		return (JSEntityList) ret;
	}

	@Override
	public JSEntityList getDefaultValue() {
		return null;
	}
	
}
