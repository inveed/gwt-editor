package net.inveed.gwt.editor.shared;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
@JsonPropertyOrder(value={"service", "createArgs"})
public class EntityAccessServiceDTO implements Serializable{
	private static final long serialVersionUID = -2224338403190447184L;

	public final String service;
	
	public final String methodGet;
	public final String methodCreate;
	public final String methodUpdate;
	public final String methodList;
	public final String methodDelete;
	
	public final String argData;
	public final String argID;
	public final String argPage;
	public final String argPageSize;
	
	public final Map<String, String> createArgs;
	
	public EntityAccessServiceDTO(
			@JsonProperty("service") String service,
			@JsonProperty("methodGet")  String getMethod,
			@JsonProperty("methodCreate")  String createMethod,
			@JsonProperty("methodUpdate")  String updateMethod,
			@JsonProperty("methodList")  String listMethod,
			@JsonProperty("methodDelete")  String deleteMethod,
			@JsonProperty("argData")  String dataArg,
			@JsonProperty("argID")  String idArg,
			@JsonProperty("argPage")  String pageArg,
			@JsonProperty("argPageSize")  String pageSizeArg,
			@JsonProperty("createArgs")  Map<String, String> createArgs) {
		this.service = service;
		this.methodGet = getMethod;
		this.methodCreate = createMethod;
		this.methodUpdate = updateMethod;
		this.methodList = listMethod;
		this.methodDelete = deleteMethod;
		this.argData = dataArg;
		this.argID = idArg;
		this.argPage = pageArg;
		this.argPageSize = pageSizeArg;
		this.createArgs = createArgs;
		
	}
	
	
}
