package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.EnumItemSelector;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.enums.EnumModel;
import net.inveed.gwt.editor.client.types.enums.EnumModel.JSEnumValue;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class EnumPropertyModel extends AbstractPropertyModel<JSEnumValue>  {
	private final EnumModel enumModel;
	private JSEnumValue defaultValue;
	
	public EnumPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.ENUM) {
			//TODO: Exception!
		}
		this.enumModel = ConfigurationRegistry.INSTANCE.getEnum(field.attributes.referencedEnumName);
		if (this.enumModel == null) {
			//TODO: Exception!
		}
		this.defaultValue = this.enumModel.getByCode(field.defaultValue);
	}

	public EnumModel getEnumModel() {
		return this.enumModel;
	}

	@Override
	public EnumItemSelector createEditor() {
		return new EnumItemSelector();
	}
	
	@Override
	public JSEnumValue convertToJSObject(JSONValue v, EntityManager em) {
		if (v.isString() != null) {
			return this.enumModel.getByCode(v.isString().stringValue());
		}
		
		return null;
	}

	@Override
	public JSEnumValue getRawValue(JSEntity entity) {
		return (JSEnumValue) entity.getProperty(this.getName(), JSEnumValue.TYPE);
	}

	@Override
	public JSEnumValue getDefaultValue() {
		return this.defaultValue;
	}
}
