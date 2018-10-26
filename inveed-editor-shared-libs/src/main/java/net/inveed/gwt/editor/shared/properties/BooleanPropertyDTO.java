package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BooleanPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = -2461487820498578882L;
	
	public static final String TYPE = "bool";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final Boolean defaultValue;
	
	public BooleanPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(MutablePropertyDTO.P_DVAL) Boolean defaultValue) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.defaultValue = defaultValue;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
