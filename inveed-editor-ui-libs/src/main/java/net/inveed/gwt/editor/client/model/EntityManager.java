package net.inveed.gwt.editor.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCRequest;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;

public class EntityManager {
	private static final Logger LOG = Logger.getLogger(EntityManager.class.getName());
	
	private HashMap<String, HashMap<String, JSEntity>> entityCache = new HashMap<>();
	public JSEntity get(EntityModel type, IJSObject id) {
		String rootModelName = type.getRootModel().getEntityName();
		String idString = id.getJSONValue().toString();
		HashMap<String, JSEntity> idMap = this.entityCache.get(rootModelName);
		if (idMap == null) {
			idMap = new HashMap<>();
			this.entityCache.put(rootModelName, idMap);
		}
		JSEntity ret = idMap.get(idString);
		if (ret != null) {
			LOG.fine("Got entity '" + rootModelName + "'#'" + idString + "' from cache.");
			return ret;
		}
		LOG.fine("Entity '" + rootModelName + "'#'" + idString + "' not found in cache. Creating new.");
		ret = new JSEntity(type, id, this);
		idMap.put(idString, ret);
		return ret;
	}

	public JSEntity get(EntityModel basicType, JSONObject json) {
		IJSObject eid = basicType.getEntityID(json, this);
		JSEntity ret = this.get(basicType, eid);
		if (ret != null) {
			LOG.fine("Found entity '" + basicType.getEntityName() + "'#'" + eid.getJSONValue().toString() + "' in cache. Updating.");
			ret.updateFromJson(json);
			return ret;
		}
		
		String rootModelName = basicType.getRootModel().getEntityName();
		HashMap<String, JSEntity> idMap = this.entityCache.get(rootModelName);
		if (idMap == null) {
			idMap = new HashMap<>();
			this.entityCache.put(rootModelName, idMap);
		}
		JSEntity e = new JSEntity(basicType, json, this);
		idMap.put(e.getID().getJSONValue().toString(), e);
		return e;
		
	}
	
	public Promise<JSEntityList, IError> listEntities(EntityModel type, int page, int pageSize, Map<String, JSONValue> params) {
		if (params == null) {
			params = new HashMap<>();
		}
		
		PromiseImpl<JSEntityList, IError> ret = new PromiseImpl<>();
		params.put(type.getServiceArgPage(), new JSONNumber(page));
		params.put(type.getServiceArgPageSize(), new JSONNumber(pageSize));
		
		Promise<JSONValue, IError> p = JsonRPCRequest.makeCall(type.getServiceName() + "#" + type.getServiceMethodList(), params);
	
		p.thenApply((JSONValue v) -> {
				if (v == null || v.isNull() != null) {
					ret.complete(null);
				}
				ret.complete(JSEntityList.parse(v, type, EntityManager.this));
				return null;
			});
			p.onError((IError err, Throwable t) -> {
				ret.error(err, t);
				return null;
			});
			
		return ret;
	}

	public void close() {
	}

	void remove(JSEntity jsEntity) {
		EntityModel rootModel = jsEntity.getModel().getRootModel();
		String rootModelName = rootModel.getEntityName();
		HashMap<String, JSEntity> idMap = this.entityCache.get(rootModelName);
		if (idMap == null) {
			return;
		}
		String idString = jsEntity.getID().getJSONValue().toString();
		idMap.remove(idString);
	}
}
