package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
@JsonPropertyOrder(alphabetic=true)
public class FormViewAttributesDTO implements Serializable {
	private static final long serialVersionUID = -5347701959666331096L;
	public final String container;
	public final int order;
	public final FormFieldLocation location;
	public final boolean readonly;
	
	public FormViewAttributesDTO(
			@JsonProperty("container") String container,
			@JsonProperty("location") FormFieldLocation location,
			@JsonProperty("order") Integer order,
			@JsonProperty("readonly") Boolean readonly) {
		this.container = container;
		//this.groupName = groupName;
		this.location = location;
		this.readonly = readonly == null ? false : readonly;
		this.order = order == null ? 0 : order;
	}
}
