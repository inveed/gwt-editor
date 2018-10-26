package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlatformModelDTO implements Serializable {
	private static final String F_ENUMS = "enums";
	private static final String F_ENTITIES = "entities";

	private static final long serialVersionUID = -4935309671133811124L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_ENTITIES)
	public final String[] entities;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_ENUMS)
	public final String[] enums;
	
	public PlatformModelDTO(
			@JsonProperty(F_ENTITIES) String[] entities, 
			@JsonProperty(F_ENUMS) String[] enums) {
		this.entities = entities;
		this.enums = enums;
	}
}
