package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.SecretKeyPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class SecretFieldPropertyModel extends AbstractPropertyModel<JSString>  {	
	private Integer maxLength;
	private Integer minLength;
	private boolean autoGenerate = true;
	
	public SecretFieldPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.SECRET_KEY && field.type != FieldType.BINARY_KEY) {
			//TODO: Exception!
		} else if (field.type == FieldType.BINARY_KEY) {
			this.autoGenerate = false;
		}
		if (field.attributes != null) {
			if (field.attributes.max != null) {
				this.maxLength = field.attributes.max.intValue();
			}
			if (field.attributes.min != null) {
				this.minLength = field.attributes.min.intValue();
			}
		}
	}

	public boolean isAutoGenerate() {
		return this.autoGenerate;
	}
	
	public Integer getMaxLength() {
		return this.maxLength;
	}
	
	public Integer getMinLength() {
		return this.minLength;
	}

	@Override
	public SecretKeyPropertyEditor createEditor() {
		int l = 16;
		if (this.minLength != null) {
			l = this.minLength;
		}
		return new SecretKeyPropertyEditor(l, this.isAutoGenerate());
	}
	
	@Override
	public JSString getRawValue(JSEntity entity) {
		return (JSString) entity.getProperty(this.getName(), JSString.TYPE);
	}
	
	@Override
	public JSString convertToJSObject(JSONValue v, EntityManager em) {
		return JSString.parse(v);
	}

	@Override
	public JSString getDefaultValue() {
		return null;
	}
}
