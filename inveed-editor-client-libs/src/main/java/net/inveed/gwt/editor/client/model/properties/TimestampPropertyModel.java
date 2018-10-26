package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSTimestamp;
import net.inveed.gwt.editor.shared.properties.DateTimePropertyDTO;

public class TimestampPropertyModel extends AbstractMutablePropertyModel<JSTimestamp, DateTimePropertyDTO> {
	private JSTimestamp defaultValue;
	public TimestampPropertyModel(DateTimePropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.defaultValue != null) {
			this.defaultValue = new JSTimestamp(field.defaultValue, field.format);
		}
	}
	
	@Override
	public JSTimestamp getValue(JSEntity entity) {
		return (JSTimestamp)entity.getProperty(this.getName(), JSTimestamp.TYPE);
	}
	
	@Override
	public JSTimestamp convertToJSObject(JSONValue v, EntityManager em) {
		return JSTimestamp.parse(v, this.getDTO().format);
	}

	@Override
	public JSTimestamp getDefaultValue() {
		return this.defaultValue;
	}
	
	public Long getNotBeforeMsec() {
		return this.getDTO().notBefore;
	}
	
	public Long getNotAfterMsec() {
		return this.getDTO().notAfter;
	}

	public String getFormat() {
		return this.getDTO().format;
	}
}
