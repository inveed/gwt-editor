package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.StringIDField;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class StringIDPropertyModel extends AbstractPropertyModel<JSString> {

	public StringIDPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.ID_STRING) {
			//TODO: Exception!
		}
	}

	@Override
	public StringIDField createEditor() {
		return new StringIDField();
	}
	
	@Override
	public JSString convertToJSObject(JSONValue v, EntityManager em) {
		return JSString.parse(v);
	}
	
	@Override
	public JSString getRawValue(JSEntity entity) {
		return (JSString) entity.getProperty(this.getName(), JSString.TYPE);
	}

	@Override
	public JSString getDefaultValue() {
		return null;
	}

}
