package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.DoublePropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSDouble;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class FloatPropertyModel extends AbstractPropertyModel<JSDouble>  {
	private Double max;
	private Double min;
	private JSDouble defaultValue;
	
	public FloatPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.FLOAT) {
			//TODO: Exception!
		}
		if (field.attributes != null) {
			if (field.attributes.max != null) {
				this.max = field.attributes.max;
			}
			if (field.attributes.min != null) {
				this.min = field.attributes.min;
			}
		}
		
		if (field.defaultValue != null) {
			defaultValue = new JSDouble(Double.parseDouble(field.defaultValue));
		}
	}

	public Double getMaxValue() {
		return this.max;
	}
	
	public Double getMinValue() {
		return this.min;
	}
	@Override
	public DoublePropertyEditor createEditor() {
		return new DoublePropertyEditor();
	}
	
	@Override
	public JSDouble getRawValue(JSEntity entity) {
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
