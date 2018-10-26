package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSBoolean;
import net.inveed.gwt.editor.shared.properties.BooleanPropertyDTO;

public class BooleanPropertyModel extends AbstractMutablePropertyModel<JSBoolean, BooleanPropertyDTO> {
	private JSBoolean defaultValue;
	
	public BooleanPropertyModel(BooleanPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		this.defaultValue = JSBoolean.parse(field.defaultValue);
	}
	
	@Override
	public JSBoolean getValue(JSEntity entity) {
		JSBoolean ret = (JSBoolean) entity.getProperty(this.getName(), JSBoolean.TYPE);
		return ret;
	}
	
	@Override
	public JSBoolean convertToJSObject(JSONValue jsonValue, EntityManager em) {
		return JSBoolean.parse(jsonValue);
	}

	@Override
	public JSBoolean getDefaultValue() {
		return this.defaultValue;
	}
}
