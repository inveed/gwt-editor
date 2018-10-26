package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.properties.IntegerIdPropertyDTO;

public class IntegerIDPropertyModel extends AbstractPropertyModel<JSLong, IntegerIdPropertyDTO> {
	
	public IntegerIDPropertyModel(IntegerIdPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
	}

	@Override
	public JSLong convertToJSObject(JSONValue v, EntityManager em) {
		return JSLong.parse(v);
	}

	@Override
	public JSLong getValue(JSEntity entity) {
		return (JSLong) entity.getProperty(this.getName(), JSLong.TYPE);
	}

	@Override
	public JSLong getDefaultValue() {
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
