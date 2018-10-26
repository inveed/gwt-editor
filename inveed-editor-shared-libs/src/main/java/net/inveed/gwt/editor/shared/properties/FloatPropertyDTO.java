package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FloatPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = 2831400792785747480L;
	private static final String P_MAXVALUE = "max";
	private static final String P_MINVALUE = "min";
	
	public static final String TYPE = "float";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final Double defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MAXVALUE)
	public final Double maxValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MINVALUE)
	public final Double minValue;
	
	public FloatPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(MutablePropertyDTO.P_DVAL) Double defaultValue,
			@JsonProperty(P_MAXVALUE) Double maxValue,
			@JsonProperty(P_MINVALUE) Double minValue) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.maxValue = maxValue;
		this.minValue = minValue;
		this.defaultValue = defaultValue;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
