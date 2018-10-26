package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.properties.TextPropertyDTO;

public class TextPropertyModel extends AbstractMutablePropertyModel<JSString, TextPropertyDTO>  {
	
	
	private static final String REGEX_ERR = "invalidValue";
	
	private JSString defaultValue;
	
	public TextPropertyModel(TextPropertyDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		
		this.defaultValue = new JSString(field.defaultValue);
	}

	public Integer getMaxLength() {
		return this.getDTO().maxLength;
	}
	
	public Integer getMinLength() {
		return this.getDTO().minLength;
	}
	
	private String convertRegex(String r) {
		if (r == null) {
			return null;
		}
		if (this.isRequired()) {
			return "^(" + r + ")$";
		} else {
			return "^(" + r + ")?$";
		}
	}
	
	public String getRegEx() {
		return this.convertRegex(this.getDTO().regexp);
	}
	
	public String getRegexError(String viewName) {
		String errCode = this.getDTO().regexpError;
		if (errCode == null) {
			errCode = REGEX_ERR;
		}
		errCode = errCode.trim();
		if (errCode.length() == 0) {
			errCode = REGEX_ERR;
		}
		return this.getEntityModelWrapper().getError(errCode, viewName);
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
	
	public boolean isMultiline() {
		return this.getDTO().multiline;
	}
}
