package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EnumPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = -1410483079234666324L;
	
	private static final String P_RENUM = "referencedEnum";
	private static final String P_NOTSETTEXT = "notSetText";
	
	public static final String TYPE = "enum";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_RENUM)
	public final String referencedEnumName;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final String defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTSETTEXT)
	public final String notSetText;
	
	public EnumPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(P_RENUM) String referencedEnumName,
			@JsonProperty(MutablePropertyDTO.P_DVAL) String defaultValue,
			@JsonProperty(P_NOTSETTEXT) String notSetText) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.referencedEnumName = referencedEnumName;
		this.defaultValue = defaultValue;
		this.notSetText = notSetText;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
