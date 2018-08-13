package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PreAuthDTO implements Serializable {
	private static final long serialVersionUID = 1635173631373058638L;
	
	@JsonProperty("salt")
	public final String salt;
	
	@JsonProperty("time")
	public final long timeSeconds;
	
	public PreAuthDTO(
			@JsonProperty("salt") String salt,
			@JsonProperty("time") long timeSeconds) {
		this.salt = salt;
		this.timeSeconds = timeSeconds;
	}
}
