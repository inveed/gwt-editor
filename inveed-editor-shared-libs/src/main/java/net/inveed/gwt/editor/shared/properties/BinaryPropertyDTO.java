package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BinaryPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = 7515197267762335166L;
	
	private static final String P_MAXLENGTH = "maxLength";
	private static final String P_MINLENGTH = "minLength";
	private static final String P_EMPTYNULL = "emptyAsNull";
	private static final String P_GEN = "allowGeneration";
	
	public static final String TYPE = "binary";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MAXLENGTH)
	public final Integer maxLength;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MINLENGTH)
	public final Integer minLength;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final String defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_EMPTYNULL)
	public final boolean emptyAsNull;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_GEN)
	public final boolean allowGeneration;
	
	public BinaryPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex, 
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(MutablePropertyDTO.P_DVAL) String defaultValue,
			@JsonProperty(P_MAXLENGTH) Integer maxLength,
			@JsonProperty(P_MINLENGTH) Integer minLength,
			@JsonProperty(P_EMPTYNULL) boolean emptyAsNull,
			@JsonProperty(P_GEN) boolean allowGeneration) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.defaultValue = defaultValue;
		this.maxLength = maxLength;
		this.minLength = minLength;
		this.emptyAsNull = emptyAsNull;
		this.allowGeneration = allowGeneration;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
