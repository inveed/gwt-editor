package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSDouble;
import net.inveed.gwt.editor.shared.properties.FloatPropertyDTO;

public class FloatPropertyModel extends AbstractMutablePropertyModel<JSDouble, FloatPropertyDTO>  {
	private JSDouble defaultValue;
	
	public FloatPropertyModel(FloatPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		
		if (field.defaultValue != null) {
			defaultValue = new JSDouble(field.defaultValue);
		}
	}

	public Double getMaxValue() {
		return this.getDTO().maxValue;
	}
	
	public Double getMinValue() {
		return this.getDTO().minValue;
	}
	
	@Override
	public JSDouble getValue(JSEntity entity) {
		return (JSDouble) entity.getProperty(this.getName(), JSDouble.TYPE);
	}
	
	@Override
	public JSDouble convertToJSObject(JSONValue v, EntityManager em) {
		return JSDouble.parse(v);
	}

	@Override
	public JSDouble getDefaultValue() {
		return this.defaultValue;
	}
}
