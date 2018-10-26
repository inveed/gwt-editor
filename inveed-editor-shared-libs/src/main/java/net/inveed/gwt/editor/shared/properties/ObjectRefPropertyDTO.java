package net.inveed.gwt.editor.shared.properties;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectRefPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = -4083893238785951957L;

	private static final String P_FILTERS = "filters";
	private static final String P_RENTITY = "referencedEntity";
	private static final String P_NOTSETTEXT = "notSetText";
	
	public static final String TYPE = "ref";
		
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_RENTITY)
	public final String referencedEntityName;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final String defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_FILTERS)
	public final HashMap<String, String> filters;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTSETTEXT)
	public final String notSetText;
	
	public ObjectRefPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(P_RENTITY) String referencedEntityName,
			@JsonProperty(MutablePropertyDTO.P_DVAL) String defaultValue,
			@JsonProperty(P_FILTERS) HashMap<String, String>  filters,
			@JsonProperty(P_NOTSETTEXT) String notSetText) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.defaultValue = defaultValue;
		this.filters = filters;
		this.referencedEntityName = referencedEntityName;
		this.notSetText = notSetText;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
