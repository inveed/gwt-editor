package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.IntegerPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class IntegerFieldModel extends AbstractPropertyModel<JSLong> {
	private Long max;
	private Long min;
	private JSLong defaultValue;
	
	public IntegerFieldModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.INTEGER) {
			//TODO: Exception!
		}
		if (field.attributes != null) {
			if (field.attributes.max != null) {
				this.max = field.attributes.max.longValue();
			}
			if (field.attributes.min != null) {
				this.min = field.attributes.min.longValue();
			}
		}
		if (field.defaultValue != null) {
			this.defaultValue = new JSLong(Long.parseLong(field.defaultValue));
		}
	}

	public Long getMaxValue() {
		return this.max;
	}
	
	public Long getMinValue() {
		return this.min;
	}
	@Override
	public IntegerPropertyEditor createEditor() {
		return new IntegerPropertyEditor();
	}
	
	@Override
	public JSLong getRawValue(JSEntity entity) {
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
