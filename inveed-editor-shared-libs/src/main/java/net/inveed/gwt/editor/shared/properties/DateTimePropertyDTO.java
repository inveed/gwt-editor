package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DateTimePropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = 2550049526791308869L;
	static final String P_NOTAFTER = "notAfter";
	static final String P_NOTBEFORE = "notBefore";
	static final String P_FORMAT = "format";
	static final String P_WITHTIME = "withTime";

	public static final String TYPE = "timestamp";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTAFTER)
	public final Long notAfter;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTBEFORE)
	public final Long notBefore;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final Long defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_FORMAT)
	public final String format;
	
	@JsonProperty(P_WITHTIME)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public final boolean withTime;
	
	public DateTimePropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(P_NOTAFTER) Long notAfter,
			@JsonProperty(P_NOTBEFORE)Long notBefore,
			@JsonProperty(MutablePropertyDTO.P_DVAL) Long defaultValue,
			@JsonProperty(P_FORMAT) String format,
			@JsonProperty(P_WITHTIME) boolean withTime
			) {
		super(asNameIndex, required, readonly, enabledWhen);
		this.notAfter = notAfter;
		this.notBefore = notBefore;
		this.defaultValue = defaultValue;
		this.format = format;
		this.withTime = withTime;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
