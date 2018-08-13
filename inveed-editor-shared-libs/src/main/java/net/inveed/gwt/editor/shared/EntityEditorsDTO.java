package net.inveed.gwt.editor.shared;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public class EntityEditorsDTO {
	static final String P_VIEWS = "views";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_VIEWS)
	public final Map<String, EntityEditorDTO> views;
	
	public EntityEditorsDTO(
			@JsonProperty(P_VIEWS) Map<String, EntityEditorDTO> views) {
		this.views = views;
	}
}
