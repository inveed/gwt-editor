package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.client.types.JSTimestamp;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class TimestampPropertyModel extends AbstractPropertyModel<JSTimestamp> {
	private JSTimestamp defaultValue;
	public TimestampPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.TIMESTAMP && field.type != FieldType.TIMESTAMP_MS) {
			//TODO: Exception!
		}
		if (field.defaultValue != null) {
			this.defaultValue = JSTimestamp.parse(field.defaultValue, ConfigurationRegistry.INSTANCE.getTimestampFormat());
		}
	}

	@Override
	public  AbstractFormPropertyEditor<TextPropertyModel, JSString>  createEditor() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public JSTimestamp getRawValue(JSEntity entity) {
		return (JSTimestamp)entity.getProperty(this.getName(), JSTimestamp.TYPE);
	}
	
	@Override
	public JSTimestamp convertToJSObject(JSONValue v, EntityManager em) {
		return JSTimestamp.parse(v, ConfigurationRegistry.INSTANCE.getTimestampFormat());
	}

	@Override
	public JSTimestamp getDefaultValue() {
		return this.defaultValue;
	}

}
