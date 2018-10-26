package net.inveed.gwt.editor.shared;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerEnvironmentDTO implements Serializable {
	
	
	private static final String F_JSON_RPC_URL = "jsonRpcUrl";
	private static final String F_RSAKEY = "pk";
	private static final String F_TIME = "time";

	private static final long serialVersionUID = -4935309671133811124L;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_JSON_RPC_URL)
	public final String jsonRpcUrl;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(F_RSAKEY)
	public final String pk;
	
	@JsonProperty(F_TIME)
	public final long time;
	
	public ServerEnvironmentDTO(
			@JsonProperty(F_JSON_RPC_URL) String jsonRpcUrl,
			@JsonProperty(F_TIME) long time, 
			@JsonProperty(F_RSAKEY) String pk) {
		this.jsonRpcUrl = jsonRpcUrl;
		this.time = time;
		this.pk = pk;
	}
}
