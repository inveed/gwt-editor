package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSTimeInterval;
import net.inveed.gwt.editor.commons.DurationFormat;
import net.inveed.gwt.editor.commons.DurationPrecision;
import net.inveed.gwt.editor.shared.properties.DurationPropertyDTO;

public class DurationPropertyModel extends AbstractMutablePropertyModel<JSTimeInterval, DurationPropertyDTO> {
	private JSTimeInterval defaultValue;
	
	public DurationPropertyModel(DurationPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (this.getDTO().defaultValue != null) {
			this.defaultValue = JSTimeInterval.parse(field.defaultValue, field.format, field.maxItem);
		}
	}
	
	public DurationFormat getFormat() {
		return this.getDTO().format;
	}
	
	public DurationPrecision getPrecision() {
		return this.getDTO().precision;
	}
	
	public DurationPrecision getMaxItem() {
		return this.getDTO().maxItem;
	}

	@Override
	public JSTimeInterval getValue(JSEntity entity) {
		return (JSTimeInterval) entity.getProperty(this.getName(), JSTimeInterval.TYPE);
	}
	@Override
	public JSTimeInterval convertToJSObject(JSONValue jv, EntityManager em) {
		return JSTimeInterval.parse(jv, this.getDTO().format, this.getDTO().maxItem);
	}

	@Override
	public JSTimeInterval getDefaultValue() {
		return this.defaultValue;
	}
}
