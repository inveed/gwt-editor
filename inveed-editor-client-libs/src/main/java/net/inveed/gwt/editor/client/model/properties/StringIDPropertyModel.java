package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.properties.StringIdPropertyDTO;

public class StringIDPropertyModel extends AbstractPropertyModel<JSString, StringIdPropertyDTO> {

	public StringIDPropertyModel(StringIdPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
	}

	@Override
	public JSString convertToJSObject(JSONValue v, EntityManager em) {
		return JSString.parse(v);
	}
	
	@Override
	public JSString getValue(JSEntity entity) {
		return (JSString) entity.getProperty(this.getName(), JSString.TYPE);
	}

	@Override
	public JSString getDefaultValue() {
		return null;
	}

	@Override
	public boolean isReadonly(boolean isNewObject) {
		return true;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public String getEnabledCondition() {
		return null;
	}

	@Override
	public Integer getAsNameIndex() {
		return null;
	}
	
	@Override
	public boolean isId() {
		return true;
	}
}
