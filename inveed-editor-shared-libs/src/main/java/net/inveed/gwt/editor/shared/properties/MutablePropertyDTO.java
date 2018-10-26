package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class MutablePropertyDTO extends AbstractPropertyDTO {
	private static final long serialVersionUID = -8721846672557811809L;
	
	static final String P_REQUIRED = "required";
	static final String P_READONLY = "readonly";
	static final String P_DVAL = "defaultValue";
	static final String P_EWHEN = "enabledWhen";
	
	@JsonProperty(MutablePropertyDTO.P_REQUIRED)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public final boolean required;
	
	@JsonProperty(MutablePropertyDTO.P_READONLY)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public final boolean readonly;
	
	@JsonProperty(index=20, value=P_EWHEN)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String enabledWhen;

	public MutablePropertyDTO(
			Integer asNameIndex,
			boolean required, 
			boolean readonly, 
			String enabledWhen) {
		super(asNameIndex);
		this.required = required;
		this.enabledWhen = enabledWhen;
		this.readonly = readonly;
	}
}
