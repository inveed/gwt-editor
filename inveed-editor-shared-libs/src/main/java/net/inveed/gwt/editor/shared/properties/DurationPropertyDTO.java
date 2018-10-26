package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.inveed.gwt.editor.commons.DurationFormat;
import net.inveed.gwt.editor.commons.DurationPrecision;

public class DurationPropertyDTO extends MutablePropertyDTO {
	private static final long serialVersionUID = -2440601242088699483L;
	
	static final String P_NOTLONGER = "notLonger";
	static final String P_NOTSHORTER = "notShorter";
	static final String P_FORMAT = "format";
	static final String P_PRECISION = "precision";
	static final String P_MAXITEM = "maxItem";
	
	public static final String TYPE = "duration";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTLONGER)
	public final String notLonger;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_NOTSHORTER)
	public final String notShorter;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(MutablePropertyDTO.P_DVAL)
	public final String defaultValue;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_PRECISION)
	public final DurationPrecision precision;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MAXITEM) 
	public final DurationPrecision maxItem;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_FORMAT)
	public final DurationFormat format;
	
	public DurationPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(MutablePropertyDTO.P_REQUIRED) boolean required,
			@JsonProperty(MutablePropertyDTO.P_READONLY) boolean readonly,
			@JsonProperty(MutablePropertyDTO.P_EWHEN) String enabledWhen,
			@JsonProperty(P_NOTLONGER) String notLonger,
			@JsonProperty(P_NOTSHORTER) String notShorter,
			@JsonProperty(MutablePropertyDTO.P_DVAL) String defaultValue,
			@JsonProperty(P_PRECISION) DurationPrecision precision,
			@JsonProperty(P_MAXITEM) DurationPrecision maxItem,
			@JsonProperty(P_FORMAT) DurationFormat format
			) {
		super(asNameIndex, required, readonly, enabledWhen);
		
		this.notLonger = notLonger;
		this.notShorter = notShorter;
		this.defaultValue = defaultValue;
		this.precision = precision;
		this.maxItem = maxItem;
		this.format = format;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
