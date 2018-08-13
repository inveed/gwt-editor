package net.inveed.gwt.server;

import java.util.HashMap;
import java.util.Map;

import net.inveed.gwt.editor.shared.EntityAccessServiceDTO;

public class EntityAccessServiceBuilder {
	public String service;
	public String methodGet = "get";
	public String methodCreate = "create";
	public String methodUpdate = "update";
	public String methodList = "list";
	public String methodDelete = "delete";
	
	public String argData = "data";
	public String argId = "id";
	public String argPage = "page";
	public String argPageSize = "pageSize";
	
	public Map<String, String> createArgs = new HashMap<>();
	
	public EntityAccessServiceDTO build() {
		if (this.service == null) {
			return null;
		}
		return new EntityAccessServiceDTO(service, methodGet, methodCreate, methodUpdate, methodList, methodDelete, argData, argId, argPage, argPageSize, createArgs);
	}
}
