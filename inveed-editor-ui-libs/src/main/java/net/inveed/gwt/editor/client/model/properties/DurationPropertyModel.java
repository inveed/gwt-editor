package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.DurationPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSTimeInterval;
import net.inveed.gwt.editor.client.types.JSTimeInterval.Format;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class DurationPropertyModel extends AbstractPropertyModel<JSTimeInterval> {
	private JSTimeInterval.Format format;
	private JSTimeInterval defaultValue;
	
	public DurationPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		switch (field.type) {
		case DURATION_ISO:
			this.format = Format.ISO;
			break;
		case DURATION_MIN:
			this.format = Format.MINUTES;
			break;
		case DURATION_SECONDS:
			this.format = Format.SECONDS;
			break;
		case DURATION_MS:
			this.format = Format.MSECONDS;
			break;
		default:
			//TODO: Error!
			break;
		}
		this.defaultValue = JSTimeInterval.parse(field.defaultValue, this.format);
		
	}

	public JSTimeInterval.Format getFormat() {
		return this.format;
	}
	@Override
	public DurationPropertyEditor createEditor() {
		return new DurationPropertyEditor();
	}
	
	@Override
	public JSTimeInterval getRawValue(JSEntity entity) {
		return (JSTimeInterval) entity.getProperty(this.getName(), JSTimeInterval.TYPE);
	}
	@Override
	public JSTimeInterval convertToJSObject(JSONValue jv, EntityManager em) {
		return JSTimeInterval.parse(jv, this.format);
	}

	@Override
	public JSTimeInterval getDefaultValue() {
		return this.defaultValue;
	}
}
