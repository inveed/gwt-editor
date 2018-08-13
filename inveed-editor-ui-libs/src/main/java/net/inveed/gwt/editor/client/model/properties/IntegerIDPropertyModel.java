package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.IntegerIDField;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class IntegerIDPropertyModel extends AbstractPropertyModel<JSLong> {

	public IntegerIDPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.ID_INTEGER) {
			//TODO: Exception!
		}
	}

	@Override
	public IntegerIDField createEditor() {
		return new IntegerIDField();
	}
	
	@Override
	public JSLong convertToJSObject(JSONValue v, EntityManager em) {
		return JSLong.parse(v);
	}

	@Override
	public JSLong getRawValue(JSEntity entity) {
		return (JSLong) entity.getProperty(this.getName(), JSLong.TYPE);
	}

	@Override
	public JSLong getDefaultValue() {
		return null;
	}

}
