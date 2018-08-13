package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public class PropertyAttributesDTO implements Serializable {
	private static final long serialVersionUID = -8834692662995808334L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Integer asNameIndex;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Double min;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Double max;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Boolean required;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Boolean readonly;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String regexp;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String regexpError;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String referencedEntityName;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String referencedEnumName;	

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String startWith;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String mappedBy;
	
	
	public PropertyAttributesDTO(
			@JsonProperty("asNameIndex") Integer asNameIndex,
			@JsonProperty("min") Double min,
			@JsonProperty("max") Double max,
			@JsonProperty("required") Boolean required,
			@JsonProperty("readonly") Boolean readonly,
			@JsonProperty("regexp") String regexp,
			@JsonProperty("regexpError") String regexpError,
			@JsonProperty("referencedEntityName") String referencedEntityName,
			@JsonProperty("startWith") String startWith,
			@JsonProperty("mappedBy") String mappedBy,
			@JsonProperty("referencedEnumName") String referencedEnumName) {
		this.asNameIndex = asNameIndex;
		this.min = min;
		this.max = max;
		this.required = required;
		this.readonly = readonly;
		this.regexp = regexp;
		this.regexpError = regexpError;
		this.referencedEntityName = referencedEntityName;
		this.startWith = startWith;
		this.mappedBy = mappedBy;
		this.referencedEnumName = referencedEnumName;
	}
	
}
