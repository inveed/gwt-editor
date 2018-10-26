package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = 7515197267762335166L;
	
	private static final String P_MAXLENGTH = "maxLength";
	private static final String P_MINLENGTH = "minLength";
	private static final String P_REGEXP = "regexp";
	private static final String P_REGEXPERR = "regexpError";
	private static final String P_STARTWITH = "startWith";
	private static final String P_PASSWORD = "password";
	private static final String P_MULTI = "multiline";
	private static final String P_EMPTYNULL = "emptyAsNull";
	private static final String P_TRIM = "trim";
	
	public static final String TYPE = "string";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MAXLENGTH)
	public final Integer maxLength;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MINLENGTH)
	public final Integer minLength;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final String defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_REGEXP)
	public final String regexp;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_REGEXPERR)
	public final String regexpError;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_STARTWITH)
	public final String startWith;

	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_PASSWORD)
	public final boolean password;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_MULTI)
	public final boolean multiline;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_EMPTYNULL)
	public final boolean emptyAsNull;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_TRIM)
	public final boolean trim;
	
	public TextPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(MutablePropertyDTO.P_DVAL) String defaultValue,
			@JsonProperty(P_MAXLENGTH) Integer maxLength,
			@JsonProperty(P_MINLENGTH) Integer minLength,
			@JsonProperty(P_REGEXP) String regexp,
			@JsonProperty(P_REGEXPERR) String regexpError,
			@JsonProperty(P_STARTWITH) String startWith,
			@JsonProperty(P_PASSWORD) boolean isPassword,
			@JsonProperty(P_MULTI) boolean multiline, 
			@JsonProperty(P_EMPTYNULL) boolean emptyAsNull,
			@JsonProperty(P_TRIM) boolean trim) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.defaultValue = defaultValue;
		this.maxLength = maxLength;
		this.minLength = minLength;
		this.regexp = regexp;
		this.regexpError = regexpError;
		this.startWith = startWith;
		this.password = isPassword;
		this.multiline = multiline;
		this.emptyAsNull = emptyAsNull;
		this.trim = trim;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
