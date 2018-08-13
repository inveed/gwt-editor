package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.DatePropertyEditor;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSDate;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class DatePropertyModel extends AbstractPropertyModel<JSDate> {
	private JSDate defaultValue;
	
	public DatePropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.DATE) {
			//TODO: Exception!
		}
		this.defaultValue = JSDate.parse(field.defaultValue, ConfigurationRegistry.INSTANCE.getDateFormat());
	}

	@Override
	public DatePropertyEditor createEditor() {
		return new DatePropertyEditor();
	}
	
	@Override
	public JSDate getRawValue(JSEntity entity) {
		return (JSDate) entity.getProperty(this.getName(), JSDate.TYPE);
	}

	@Override
	public JSDate convertToJSObject(JSONValue v, EntityManager em) {
		return JSDate.parse(v, ConfigurationRegistry.INSTANCE.getDateFormat());
	}

	@Override
	public JSDate getDefaultValue() {
		return this.defaultValue;
	}
}
