package net.inveed.gwt.editor.client.model.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.Column;

import net.inveed.gwt.editor.client.editor.fields.EntityRefListBoxSelector;
import net.inveed.gwt.editor.client.lists.EntityReferenceCell;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class EntityReferencePropertyModel extends AbstractPropertyModel<JSEntity>  {

	private final EntityModel targetModel;
	private Map<String, String> filters;
	
	public EntityReferencePropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.OBJECT_REF) {
			//TODO: Exception!
		}
		this.targetModel = ConfigurationRegistry.INSTANCE.getModel(field.attributes.referencedEntityName);
		if (this.targetModel == null) {
			//TODO: Exception!
		}
		if (field.filters != null) {
			this.filters = Collections.unmodifiableMap(field.filters);
		} else {
			this.filters = Collections.unmodifiableMap(new HashMap<>());
		}
		
	}

	public EntityModel getTargetEntityModel() {
		return this.targetModel;
	}

	@Override
	public EntityRefListBoxSelector createEditor() {
		return new EntityRefListBoxSelector();
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
			if (this.getTargetEntityModel().getIdFields().size() == 0) {
				return null;
			} else if (this.getTargetEntityModel().getIdFields().size() == 1) {
				IPropertyDesc<?> idProp = this.getTargetEntityModel().getIdFields().get(0);
				IJSObject idObj = idProp.convertToJSObject(entityJson, em);
				return em.get(idProp.getEntityModelWrapper(), idObj);
			} else {
				return null;
			}
		}
	}
	

	
	@Override
	public JSEntity getRawValue(JSEntity entity) {
		return (JSEntity) entity.getProperty(this.getName(), JSEntity.TYPE);
	}
	
	public Map<String, String> getFilters() {
		return this.filters;
	}
	
	
	@Override
	public Column<JSEntity, ?> createTableColumn() {
		EntityReferenceCell cell = new EntityReferenceCell();
		
		Column<JSEntity, JSEntity> ret = new Column<JSEntity, JSEntity>(cell) {
			@Override
			public JSEntity getValue(JSEntity row) {
				return getRawValue(row);
			}
			
		};
		
		
		/*
		Column<JSEntity, String> ret = new TextColumn<JSEntity>() {
			@Override
			public String getValue(JSEntity row) {
				JSEntity v = getRawValue(row);
				if (v == null) {
					return "-- NOT SET --";
				}
				return v.getDisplayValue();
			}
		};
		*/
		return ret;
	}

	@Override
	public JSEntity getDefaultValue() {
		return null;
	}
}
