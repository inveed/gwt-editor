package net.inveed.gwt.editor.shared.lists;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public class PropertyInListViewDTO implements Serializable {
	private static final long serialVersionUID = -7905891221149646531L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("order")
	public final Integer order;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("width")
	public final Integer width;
	
	@JsonProperty("property")
	public final String property;
	
	public PropertyInListViewDTO(
			@JsonProperty("order") Integer order,
			@JsonProperty("width") Integer width,
			@JsonProperty("property") String property) {
		this.order = order;
		this.width = width;
		this.property = property;
	}
}
