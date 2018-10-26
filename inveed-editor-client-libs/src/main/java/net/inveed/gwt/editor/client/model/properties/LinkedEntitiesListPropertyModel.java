package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.shared.properties.EntityListPropertyDTO;

public class LinkedEntitiesListPropertyModel extends AbstractPropertyModel<JSEntityList, EntityListPropertyDTO> {
	private EntityModel targetEntityType;
	private String mappedBy;
	
	public LinkedEntitiesListPropertyModel(EntityListPropertyDTO model, String name, EntityModel entity) {
		super(model, name, entity);
		this.targetEntityType = ConfigurationRegistry.INSTANCE.getModel(model.referencedEntityName);
		this.mappedBy = model.mappedBy;
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
	public JSEntityList convertToJSObject(JSONValue jv, EntityManager em) {
		if (jv == null) {
			return null;
		}
		return JSEntityList.parse(jv, this.targetEntityType, em);
	}

	@Override
	public JSEntityList getValue(JSEntity entity) {
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

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getEnabledCondition() {
		return null;
	}

	@Override
	public Integer getAsNameIndex() {
		return null;
	}	
}
