package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlatformConfigurationDTO implements Serializable {
	private static final long serialVersionUID = -4935309671133811124L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String[] entities;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String[] enums;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String dateFormat;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final String timestampFormat;
	
	public PlatformConfigurationDTO(
			@JsonProperty("entities") String[] entities, 
			@JsonProperty("enums") String[] enums, 
			@JsonProperty("dateFormat") String dateFormat,
			@JsonProperty("timestampFormat") String timestampFormat) {
		this.entities = entities;
		this.enums = enums;
		if (dateFormat != null)
			this.dateFormat = dateFormat;
		else
			this.dateFormat = "yyyy-MM-dd";
		
		if (timestampFormat != null)
			this.timestampFormat = timestampFormat;
		else 
			this.timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.sss ZZ";
	}

}
