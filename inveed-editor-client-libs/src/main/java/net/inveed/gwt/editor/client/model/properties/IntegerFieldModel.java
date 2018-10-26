package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.properties.IntegerPropertyDTO;

public class IntegerFieldModel extends AbstractMutablePropertyModel<JSLong, IntegerPropertyDTO> {
	private JSLong defaultValue;
	
	public IntegerFieldModel(IntegerPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		
		if (field.defaultValue != null) {
			this.defaultValue = new JSLong(field.defaultValue);
		}
	}

	public Long getMaxValue() {
		return this.getDTO().maxValue;
	}
	
	public Long getMinValue() {
		return this.getDTO().minValue;
	}
	
	@Override
	public JSLong getValue(JSEntity entity) {
		return (JSLong) entity.getProperty(this.getName(), JSLong.TYPE);
	}
	@Override
	public JSLong convertToJSObject(JSONValue v, EntityManager em) {
		return JSLong.parse(v);
	}

	@Override
	public JSLong getDefaultValue() {
		return this.defaultValue;
	}
}
