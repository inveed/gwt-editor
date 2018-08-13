package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public class ListViewAttributesDTO implements Serializable {
	private static final long serialVersionUID = -7905891221149646531L;
	public final int order;
	public final int width;
	
	public ListViewAttributesDTO(
			@JsonProperty("order") Integer order,
			@JsonProperty("width") Integer width) {
		this.order = order == null? 0 : order;
		this.width = width == null ? 0 : width;
	}
}
