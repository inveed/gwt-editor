package net.inveed.gwt.editor.client.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCRequest;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSMap;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.shared.FieldType;

public class JSEntity implements IJSObject {
	public static final class JSEntityError implements IError {
		public static final String TYPE = "JSEntityError";
		public static final int ERR_EMPTY_ENTITY_RESPONSE = 0;
		public static final int ERR_INVALID_ENTITY_RESPONSE = 0;
		public static final int ERR_ENTITY_NOT_FOUND = 0;
		public JSEntityError(int code) {
			
		}
		
		@Override
		public String getMessage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getLocalizedMessage() {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public String getType() {
			return TYPE;
		}
		
	}
	public static final String TYPE = "ENTITY";
	private EntityModel type;
	private JSMap<IJSObject> commitedProperties = new JSMap<>();
	private JSMap<IJSObject> modifiedProperties = new JSMap<>();
	private boolean initialized;
	
	private IJSObject id;
	private boolean isDeleted;
	private String displayValue;
	private final EntityManager entityManager;
	
	
	public JSEntity(EntityModel type, EntityManager em) {
		this.entityManager = em;
		this.type = type;
		this.initialized = false;
	}
	
	public JSEntity(EntityModel type, IJSObject id, EntityManager em) {
		this.entityManager = em;
		this.type = type;
		this.id = id;
		this.initialized = false;
	}
	
	public JSEntity(EntityModel model, JSONObject json, EntityManager em) {
		this.entityManager = em;
		this.type = model;
		this.id = model.getEntityID(json, em);
		this.updateFromJson(json);
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (!other.getType().equals(this.getType())) {
			return false;
		}
		JSEntity oe = (JSEntity) other;
		if (oe.getID() == null || this.getID() == null) {
			return false;
		}
		return oe.getID().isEquals(this.getID());
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	public IJSObject getID() {
		return this.id;
	}
	
	public String getDisplayValue() {
		return this.displayValue;
	}
	
	public EntityModel getModel() {
		return type;
	}
	
	public IJSObject getProperty(String name, String type) {
		IJSObject ret = this.getProperty(name);
		if (ret == null) {
			return ret;
		}
		if (ret.getType().equals(type)) {
			return ret;
		}
		return null;
	}
	
	public IJSObject getProperty(String name) {
		
		if (name == null) {
			return null;
		}
		name = name.trim();
		if (this.modifiedProperties.containsKey(name)) {
			return this.modifiedProperties.get(name);
		} else {
			return this.commitedProperties.get(name);
		}
	}
	
	public IJSObject getCommitedProperty(String name) {
		if (name == null) {
			return null;
		}
		name = name.trim();
		return this.commitedProperties.get(name);
	}
	
	public void setProperty(String name, IJSObject v) {
		IJSObject cv = this.commitedProperties.get(name);
		if (cv == v) {
			this.modifiedProperties.remove(name);
			return;
		}
		if (cv != null) {
			if (cv.equals(v)) {
				this.modifiedProperties.remove(name);
				return;
			}
		}
		this.modifiedProperties.put(name, v);
	}
	
	private String getDisplayValue(JSONObject entity) {
		List<IPropertyDesc<?>> nameFields = this.getModel().getNameProperties();
		if (nameFields != null && nameFields.size() > 0) {
			StringBuilder sb = new StringBuilder();
			for (IPropertyDesc<?> pd : this.getModel().getNameProperties()) {
				IJSObject pv = pd.getValue(entity, this.getEntityManager());
				sb.append(pv.toString());
				sb.append(" ");
			}
			return sb.toString().trim();
		}
		return this.getModel().getDisplayName(null) + "# " + this.id.toString();
	}
	
	void updateFromJson(JSONObject json) {
		boolean hasOnlyID = true;
		
		EntityModel newModel = this.getModel().getEntityType(json);
		if (newModel != null) {
			if (newModel.isParentType(this.getModel())) {
				this.type = newModel;
			}
		}
		
		for (IPropertyDesc<?> prop : this.getModel().getFields()) {
			try {
			JSONValue jv = prop.getValue(json);
				if (jv.isNull() == null) {
					this.commitedProperties.put(prop.getName(), prop.getValue(json, this.getEntityManager()));
					if (prop.getType() != FieldType.ID_INTEGER && prop.getType() != FieldType.ID_STRING) {
						hasOnlyID = false;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		this.initialized = !hasOnlyID || this.initialized;
		if (this.id == null) {
			this.id = this.getModel().getEntityID(json, this.getEntityManager());
		}
		this.displayValue = this.getDisplayValue(json);
		this.modifiedProperties = new JSMap<>();
	}
	
	public void save(JsonRPCTransaction tran) {
		JsonRPCRequest req;
		if (this.getID() == null) {
			req = this.createCreateRequest();
		} else {
			req = this.createUpdateRequest();
		}
		tran.add(req);
		Promise<JSONValue, IError> p = req.getPromise();
		p.thenApply((JSONValue v) -> {
			if (v == null) {
				return null;
			}
			if (v.isObject() == null) {
				return null;
			}
			JSEntity.this.updateFromJson(v.isObject());
			return null;
		});
		
	}
	
	private JsonRPCRequest createCreateRequest() {
		JSMap<IJSObject> data = this.modifiedProperties.clone();
		HashMap<String, JSONValue> params = new HashMap<>();
		Map<String, String> createArgNames = this.getModel().getServiceCreateArgs();
		if (createArgNames != null && createArgNames.size() > 0) {
			for (String carg : createArgNames.keySet()) {
				String cname = createArgNames.get(carg);
				if (!data.containsKey(cname)) {
					//TODO: error!
					return null;
				}
				IJSObject v = data.get(cname);
				params.put(carg, v.getJSONValue());
				data.remove(cname);
			}
		}
		if (this.getModel().getTypeDescriminator() != null && this.getModel().isRequiredTypeOnUpdate()) {
			String td = this.getModel().getTypeDescriminator().trim();
			if (td.length() > 0) {
				String tdf = this.getModel().getTypeDiscriminatorField();
				if (tdf == null) {
					tdf = "#type";
				}
				data.put(tdf, new JSString(td));
			}
		}
		
		params.put(this.getModel().getServiceArgData(), data.getJSONValue());
		JsonRPCRequest ret = new JsonRPCRequest(this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodCreate(), params);
				
		/*
		Promise<JSONValue, IError> p = JsonRPCClient.makeCall(this.getModel().getRegistry().getJsonRPCUrl(), 
				this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodCreate(), params);
		
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		p.thenApply((JSONValue v) -> {
			if (v == null) {
				ret.error(new JSEntityError(JSEntityError.ERR_EMPTY_ENTITY_RESPONSE), null);
				return null;
			}
			if (v.isObject() == null) {
				ret.error(new JSEntityError(JSEntityError.ERR_INVALID_ENTITY_RESPONSE), null);
				return null;
			}
			JSEntity.this.updateFromJson(v.isObject());
			ret.complete(null);
			return null;
		});
		p.onError((IError e, Throwable t) -> {
			ret.error(e, t);
			return null;
		});
		*/
		return ret;
	}
	
	private JsonRPCRequest createUpdateRequest() {
		HashMap<String, JSONValue> params = new HashMap<>();

		params.put(this.getModel().getServiceArgId(), this.getID().getJSONValue());
		params.put(this.getModel().getServiceArgData(), this.modifiedProperties.getJSONValue());
		
		JsonRPCRequest ret = new JsonRPCRequest(this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodUpdate(), params);
		/*
		Promise<JSONValue,IError> p =  JsonRPCClient.makeCall(this.getModel().getRegistry().getJsonRPCUrl(), this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodUpdate(), params);
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		p.thenApply((JSONValue v) -> {
			if (v == null) {
				ret.error(new JSEntityError(JSEntityError.ERR_EMPTY_ENTITY_RESPONSE), null);
			} else if (v.isObject() != null) {
				updateFromJson(v.isObject());
				ret.complete(null);
			} else {
				ret.error(new JSEntityError(JSEntityError.ERR_INVALID_ENTITY_RESPONSE), null);
			}
			return null;
		});
		p.onError((IError err, Throwable t) -> {
			ret.error(err, t);
			return null;
		});
		*/
		return ret;
	}
	
	public Promise<Void, IError> load() {
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();

		HashMap<String, JSONValue> params = new HashMap<>();
		params.put(this.getModel().getServiceArgId(), id.getJSONValue());
		Promise<JSONValue,IError> p = JsonRPCRequest.makeCall(this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodGet(), params);
		p.thenApply((JSONValue v) -> {
			if (v == null) {
				ret.error(new JSEntityError(JSEntityError.ERR_ENTITY_NOT_FOUND), null);
			} else if (v.isObject() != null) {
				updateFromJson(v.isObject());
				ret.complete(null);
			} else {
				ret.error(new JSEntityError(JSEntityError.ERR_INVALID_ENTITY_RESPONSE), null);
			}
			return null;
		});
		p.onError((IError err, Throwable t) -> {
			ret.error(err, t);
			return null;
		});
		
		return ret;
	}
	
	/*
	public Promise<Void, IError> delete() {
		
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		JsonRPCRequest req = this.createDeleteRequest();
		Promise<JsonRPCResponse,IError> gp =  JsonRPCClient.makeCall(this.getModel().getRegistry().getJsonRPCUrl(), req);
		Promise<JSONValue,IError> p = req.getPromise();
		
		gp.onError((IError err, Throwable t) -> {
			ret.error(err, t);
			return null;
		});
		p.thenApply((JSONValue v) -> {
			this.entityManager.remove(this);
			this.id = null;
			this.isDeleted = true;
			ret.complete(null);
			return null;
		});
		
		p.onError((IError err, Throwable t) -> {
			ret.error(err, t);
			return null;
		});
		
		return ret;
	}
	*/
	public void delete(JsonRPCTransaction tran) {
		JsonRPCRequest req = this.createDeleteRequest();
		Promise<JSONValue,IError> p = req.getPromise();
		p.thenApply((JSONValue v) -> {
			this.entityManager.remove(this);
			this.id = null;
			this.isDeleted = true;
			return null;
		});
		
		tran.add(req);
	}
	
	private JsonRPCRequest createDeleteRequest() {
		HashMap<String, JSONValue> params = new HashMap<>();
		params.put(this.getModel().getServiceArgId(), this.id.getJSONValue());
		JsonRPCRequest ret = new JsonRPCRequest(this.getModel().getServiceName() + "#" + this.getModel().getServiceMethodDelete(), params);
		ret.setContext(this);
		return ret;
	}

	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return 1;
		}
		if (!TYPE.equals(other.getType())) {
			return 0;
		}
		JSEntity e = (JSEntity) other;
		if (e.initialized) {
			return this.getDisplayValue().compareTo(e.getDisplayValue());
		} else {
			return e.getID().compareTo(this.getID());
		}
	}

	@Override
	public JSONValue getJSONValue() {
		return this.id.getJSONValue();
	}
	
	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public boolean canDelete() {
		return this.getModel().canDelete();
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public boolean canEdit() {
		return true;
	}
	
}
