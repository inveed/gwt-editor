package net.inveed.gwt.editor.client.model.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.properties.ObjectRefPropertyDTO;

public class EntityReferencePropertyModel extends AbstractMutablePropertyModel<JSEntity, ObjectRefPropertyDTO>  {

	private final EntityModel targetModel;
	private Map<String, String> filters;
	
	public EntityReferencePropertyModel(ObjectRefPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		this.targetModel = ConfigurationRegistry.INSTANCE.getModel(field.referencedEntityName);
		if (this.targetModel == null) {
			//TODO: Exception!
		}
		if (field.filters != null) {
			this.filters = Collections.unmodifiableMap(field.filters);
		} else {
			this.filters = Collections.unmodifiableMap(new HashMap<>());
		}
		
	}
	
	@Override
	public boolean isValid() {
		return this.targetModel != null && super.isValid();
	}

	public EntityModel getTargetEntityModel() {
		return this.targetModel;
	}
	
	@Override
	public JSEntity convertToJSObject(JSONValue entityJson, EntityManager em) {
		if (entityJson == null) {
			return null;
		} else if (entityJson.isObject() != null) {
			// Полноценный объект или вложенные идентификаторы
			JSEntity ret = em.get(this.targetModel, entityJson.isObject());
			return ret;
		} else {
			IPropertyDescriptor<?> idProp = this.getTargetEntityModel().getIdPropertyDescriptor();
			if (idProp == null) {
				return null;
			}
			IJSObject idObj = idProp.convertToJSObject(entityJson, em);
			return em.get(idProp.getEntityModelWrapper(), idObj);
		}
	}
	
	public String getNotSetText() {
		return this.getDTO().notSetText;
	}
	
	@Override
	public JSEntity getValue(JSEntity entity) {
		return (JSEntity) entity.getProperty(this.getName(), JSEntity.TYPE);
	}
	
	public Map<String, String> getFilters() {
		return this.filters;
	}
	
	
	@Override
	public JSEntity getDefaultValue() {
		return null;
	}
}
