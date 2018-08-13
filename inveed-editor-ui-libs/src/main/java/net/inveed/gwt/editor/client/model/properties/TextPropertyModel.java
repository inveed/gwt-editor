package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.SingleRowTextPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class TextPropertyModel extends AbstractPropertyModel<JSString>  {
	
	private static final String IPv4_REGEXP = "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	private static final String IPv6_REGEXP = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";
	private static final String HOSTNAME_REGEX = "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])";
	private static final String IP_OR_HOST_REGEX = "(" + IPv4_REGEXP + ")|(" + IPv6_REGEXP + ")|(" + HOSTNAME_REGEX + ")";
	private static final String PORTNUM_REGEX = "[0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]";
	
	private static final String HTTP_URL_REGEX = "https?://(" + IP_OR_HOST_REGEX + ")(:(" + PORTNUM_REGEX + "))?((\\/([~0-9a-zA-Z\\#\\+\\%@\\.\\/_-]+))?(\\?[0-9a-zA-Z\\+\\%@\\/&\\[\\];=_-]+)?)";
	private static final String HTTP_URL_REGEX_ERR = "invalidUrl";
	
	private static final String REGEX_ERR = "invalidValue";
	
	private Integer maxLength;
	private Integer minLength;
	
	private String regexp;
	private String regexpError;
	
	private JSString defaultValue;
	private boolean multiline;
	
	public TextPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.TEXT && field.type != FieldType.TEXT_LONG && field.type != FieldType.PASSWORD && field.type != FieldType.URL) {
			//TODO: Exception!
		}
		if (field.type == FieldType.TEXT_LONG) {
			this.multiline = true;
		} else {
			this.multiline = false;
		}
		if (field.attributes != null) {
			if (field.attributes.max != null) {
				this.maxLength = field.attributes.max.intValue();
			}
			if (field.attributes.min != null) {
				this.minLength = field.attributes.min.intValue();
			}
			this.regexp = field.attributes.regexp;
			this.regexpError = field.attributes.regexpError;
		}
		if (field.defaultValue != null) {
			this.defaultValue = new JSString(field.defaultValue);
		}
	}

	public Integer getMaxLength() {
		return this.maxLength;
	}
	
	public Integer getMinLength() {
		return this.minLength;
	}
	
	private String convertRegex(String r) {
		if (this.isRequired()) {
			return "^(" + r + ")$";
		} else {
			return "^(" + r + ")?$";
		}
	}
	public String getNativeRegex() {
		switch (this.getType()) {
		case URL:
			return convertRegex(HTTP_URL_REGEX);

		default:
			return null;
		}
	}
	
	public String getNativeRegexError(String viewName) {
		switch (this.getType()) {
		case URL:
			return this.getError(HTTP_URL_REGEX_ERR, viewName);

		default:
			return null;
		}
	}
	public String getRegEx() {
		return this.regexp;
	}
	
	public String getRegexError(String viewName) {
		String errCode = this.regexpError;
		if (errCode == null) {
			errCode = REGEX_ERR;
		}
		errCode = errCode.trim();
		if (errCode.length() == 0) {
			errCode = REGEX_ERR;
		}
		return this.getError(errCode, viewName);
	}

	@Override
	public SingleRowTextPropertyEditor createEditor() {
		return new SingleRowTextPropertyEditor(this.multiline);
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
		return this.defaultValue;
	}
}
