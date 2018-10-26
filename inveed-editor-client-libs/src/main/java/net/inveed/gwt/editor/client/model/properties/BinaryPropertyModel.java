package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.properties.BinaryPropertyDTO;

public class BinaryPropertyModel extends AbstractMutablePropertyModel<JSString, BinaryPropertyDTO> {

	private JSString defaultValue;
	public BinaryPropertyModel(BinaryPropertyDTO model, String name, EntityModel entity) {
		super(model, name, entity);
		this.defaultValue = model.defaultValue == null ? null : (model.defaultValue.length() == 0 ? null : new JSString(model.defaultValue));
	}

	@Override
	public JSString getValue(JSEntity entity) {
		return (JSString) entity.getProperty(this.getName(), JSString.TYPE);
	}
	
	@Override
	public JSString convertToJSObject(JSONValue v, EntityManager em) {
		return JSString.parse(v);
	}

	@Override
	public JSString getDefaultValue() {
		return this.defaultValue;
	}
	
	public Integer getMinLength() {
		return this.getDTO().minLength;
	}
	
	public Integer getMaxLength() {
		return this.getDTO().maxLength;
	}
	
	public boolean isAllowGenerate() {
		return this.getDTO().allowGeneration;
	}
	
	public Integer getGenLength() {
		return this.getMaxLength() == null ? this.getMinLength() : this.getMaxLength();
	}
}
