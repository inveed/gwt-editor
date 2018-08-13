package net.inveed.gwt.editor.shared;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
@JsonPropertyOrder(value={PropertyModelDTO.P_TYPE, PropertyModelDTO.P_ATTRIBUTES, PropertyModelDTO.P_FORM_VIEWS, PropertyModelDTO.P_LIST_VIEWS})
public class PropertyModelDTO implements Serializable {
	static final String P_TYPE = "type";
	static final String P_DVAL = "defaultValue";
	static final String P_EWHEN = "enabledWhen";
	static final String P_ATTRIBUTES = "attributes";
	static final String P_FORM_VIEWS = "formViews";
	static final String P_LIST_VIEWS = "listViews";
	static final String P_FILTERS = "filters";
	
	private static final long serialVersionUID = 1383872148614342637L;

	@JsonProperty(index=10, value=P_TYPE)
	public final FieldType type;
	
	@JsonProperty(index=20, value=P_DVAL)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String defaultValue;
	
	@JsonProperty(index=20, value=P_EWHEN)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String enabledWhen;
	
	@JsonProperty(index=20, value=P_ATTRIBUTES)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final PropertyAttributesDTO attributes;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(index=30, value=P_FORM_VIEWS)
	public final Map<String, FormViewAttributesDTO> formViews;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(index=40, value=P_LIST_VIEWS)
	public final Map<String, ListViewAttributesDTO> listViews;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(index=40, value=P_FILTERS)
	public final Map<String, String> filters;
	
	public PropertyModelDTO(
			@JsonProperty(P_TYPE) FieldType type,
			@JsonProperty(P_DVAL) String defaultValue,
			@JsonProperty(P_EWHEN) String enabledWhen,
			@JsonProperty(P_ATTRIBUTES) PropertyAttributesDTO attributes,
			@JsonProperty(P_FORM_VIEWS) Map<String, FormViewAttributesDTO> formViews,
			@JsonProperty(P_LIST_VIEWS) Map<String, ListViewAttributesDTO> listViews,
			@JsonProperty(P_FILTERS) Map<String, String> filters) {
		this.type = type;
		this.defaultValue = defaultValue;
		this.enabledWhen = enabledWhen;
		this.attributes = attributes;
		this.formViews = formViews;
		this.listViews = listViews;
		this.filters = filters;
	}
}
