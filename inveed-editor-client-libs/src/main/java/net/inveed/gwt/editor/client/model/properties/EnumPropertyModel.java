package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.EnumModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.EnumModel.JSEnumValue;
import net.inveed.gwt.editor.shared.properties.EnumPropertyDTO;

public class EnumPropertyModel extends AbstractMutablePropertyModel<JSEnumValue, EnumPropertyDTO>  {
	private final EnumModel enumModel;
	private JSEnumValue defaultValue;
	
	public EnumPropertyModel(EnumPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);

		this.enumModel = ConfigurationRegistry.INSTANCE.getEnum(field.referencedEnumName);
		if (this.enumModel == null) {
			//TODO: Exception!
		}
		this.defaultValue = this.enumModel.getByCode(field.defaultValue);
	}

	public EnumModel getEnumModel() {
		return this.enumModel;
	}
	
	@Override
	public boolean isValid() {
		return this.enumModel != null && super.isValid();
	}
	@Override
	public JSEnumValue convertToJSObject(JSONValue v, EntityManager em) {
		if (v.isString() != null) {
			return this.enumModel.getByCode(v.isString().stringValue());
		}
		
		return null;
	}
	
	public String getNotSetText() {
		return this.getDTO().notSetText;
	}

	@Override
	public JSEnumValue getValue(JSEntity entity) {
		return (JSEnumValue) entity.getProperty(this.getName(), JSEnumValue.TYPE);
	}

	@Override
	public JSEnumValue getDefaultValue() {
		return this.defaultValue;
	}
}
