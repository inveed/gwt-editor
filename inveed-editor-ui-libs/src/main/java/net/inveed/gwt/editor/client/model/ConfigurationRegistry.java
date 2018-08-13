package net.inveed.gwt.editor.client.model;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Logger;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import net.inveed.gwt.editor.client.types.enums.EnumModel;
import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.HttpClientError;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;
import net.inveed.gwt.editor.shared.PlatformConfigurationDTO;

public class ConfigurationRegistry {
	public static final class ConfigurationRegistryError implements IError {
		public static final String TYPE = "ConfigurationRegistryError";
		public static final int ERR_INVALID_STATE = 10001;
		public static final int ERR_CANNOT_PARSE_ENTITY_MODEL = 10010;
		public static final int ERR_CANNOT_PARSE_ENUM_MODEL = 10011;

		public ConfigurationRegistryError(int code) {
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
	
	public static interface PlatformConfigurationMapper extends ObjectMapper<PlatformConfigurationDTO> {}
	public static final ConfigurationRegistry INSTANCE = new ConfigurationRegistry();
	private static final Logger LOG = Logger.getLogger(ConfigurationRegistry.class.getName());
	
	private PlatformConfigurationDTO configurationDTO;
	private Queue<String> pendingLoadEntities;
	private Queue<String> pendingLoadEnums;
	private HashMap<String, EntityModel> entityModels = new HashMap<>();
	private HashMap<String, EnumModel> enums = new HashMap<>();
	private String baseUrl;
	private String jsonRPCUrl;
	private PromiseImpl<Void, IError> promise;
	
	private ConfigurationRegistry() {}
	
	public EntityModel getModel(String name) {
		return this.entityModels.get(name);
	}
	
	public Collection<EntityModel> getModels() {
		return this.entityModels.values();
	}
	
	public EnumModel getEnum(String name) {
		return this.enums.get(name);
	}
	
	public Promise<Void, IError> loadModel(String baseUrl, String jsonRPCUrl) {
		if (baseUrl == null) {
			LOG.fine("BaseURL not set. Using default");
			baseUrl = "http://127.0.0.1:8082/rest/model";
		}
		
		LOG.info("Loading model with URL: " + baseUrl);
		this.jsonRPCUrl = jsonRPCUrl;
		this.baseUrl = baseUrl;
		this.promise = new PromiseImpl<Void, IError>();
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(this.baseUrl, true);
		p.thenApply(this::onConfiguration);
		p.onError((HTTPError req, Throwable t) -> {
			this.promise.error(req, t);
			return null;
		});
		return this.promise;
	}
	
	private Void onConfiguration(RequestResult r) {
		if (r.response == null) {
			LOG.warning("Got EMPTY configuration response");
			this.promise.error(new HttpClientError(HttpClientError.ERR_EMPTY_RESPONSE), null);
			return null;
		} else {
			LOG.fine("Got Response: ");
			LOG.fine(" Code: " + r.response.getStatusCode());
			LOG.fine(r.response.getText());
			if (r.response.getStatusCode() != 200) {
				this.promise.error(new HttpClientError(HttpClientError.ERR_STATUS), null);
				return null;
			}
			
			PlatformConfigurationMapper mapper = GWT.create(PlatformConfigurationMapper.class);
			PlatformConfigurationDTO dto = mapper.read(r.response.getText());
			
			
			this.pendingLoadEntities = new ArrayDeque<>();
			this.pendingLoadEnums = new ArrayDeque<>();
			
			for (String e : dto.entities) {
				this.pendingLoadEntities.add(e);
			}
			
			for (String e : dto.enums) {
				this.pendingLoadEnums.add(e);
			}
			
			this.configurationDTO = dto;
			this.loadNextEntityModel();
		}
		return null;
	}
	
	private void loadNextEntityModel(){
		if (this.pendingLoadEntities == null) {
			this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_INVALID_STATE), null);
			return;
		}
		if (this.pendingLoadEntities.size() < 1) {
			this.loadNextEnumModel();
			return;
		}
		
		String ename = this.pendingLoadEntities.poll();
		if (ename == null) {
			this.loadNextEnumModel();
			return;
		}
		LOG.fine("Loading entity for name " + ename);
		
		String url = this.baseUrl + "/entity/" + ename;
		LOG.fine("URL:" + url);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, true);
		p.thenApply(this::onEntityResult);
		p.onError((HTTPError r, Throwable t) -> {
			this.promise.error(r, t);
			return null;
		});
	}
	
	private void loadNextEnumModel(){
		if (this.pendingLoadEnums == null) {
			this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_INVALID_STATE), null);
			return;
		}
		if (this.pendingLoadEnums.size() < 1) {
			this.onModelLoadComplete();
			return;
		}
		
		String ename = this.pendingLoadEnums.poll();
		if (ename == null) {
			this.promise.complete(null);
			return;
		}
		LOG.fine("Loading enum for name " + ename);
		
		String url = this.baseUrl + "/enum/" + ename;
		LOG.fine("URL:" + url);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, true);
		p.thenApply(this::onEnumResult);
		p.onError((HTTPError r, Throwable t) -> {
			this.promise.error(r,t);
			return null;
		});
	}
	
	private void onModelLoadComplete() {
		for (EntityModel em : this.entityModels.values()) {
			em.initialize();
		}
		this.promise.complete(null);
	}
	
	private Void onEntityResult(RequestResult r) {
		LOG.info("Got entity model result:");
		if (r.response != null) {
			LOG.fine("Got ENTITY Response: ");
			LOG.fine("Code: " + r.response.getStatusCode());
			LOG.fine(r.response.getText());
			if (r.response.getStatusCode() == 200) {
				EntityModel model = EntityModel.parseModel(r.response.getText(), this);
				if (model == null) {
					LOG.warning("Cannot parse entity model");
					this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_CANNOT_PARSE_ENTITY_MODEL), null);
					return null;
				} else {
					LOG.fine("Parsed model for entity " + model.getEntityName());
					this.entityModels.put(model.getEntityName(), model);
				}
			} else {
				this.promise.error(new HttpClientError(HttpClientError.ERR_STATUS), null);
			}
		} else {
			LOG.warning("Got null entity result");
			this.promise.error(new HttpClientError(HttpClientError.ERR_EMPTY_RESPONSE), null);
			return null;
		}
		this.loadNextEntityModel();
		return null;
	}
	
	private Void onEnumResult(RequestResult r) {
		LOG.fine("Got enum model result:");
		if (r.response != null) {
			LOG.fine("Got ENUM Response: ");
			LOG.fine("Code: " + r.response.getStatusCode());
			LOG.fine(r.response.getText());
			if (r.response.getStatusCode() == 200) {
				EnumModel model = EnumModel.parseModel(r.response.getText(), this);
				if (model == null) {
					LOG.warning("Cannot parse enum model");
					this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_CANNOT_PARSE_ENUM_MODEL), null);
					
					//return null;
				} else {
					LOG.info("Parsed model for enum " + model.getName());
					this.enums.put(model.getName(), model);
				}
			} else {
				this.promise.error(new HttpClientError(HttpClientError.ERR_STATUS), null);
			}
		} else {
			LOG.warning("Got null enum result");
			this.promise.error(new HttpClientError(HttpClientError.ERR_EMPTY_RESPONSE), null);
			return null;
		}
		this.loadNextEntityModel();
		return null;
	}
	
	public DateTimeFormat getTimestampFormat() {
		return DateTimeFormat.getFormat(this.configurationDTO.timestampFormat);
	}
	
	public DateTimeFormat getDateFormat() {
		return DateTimeFormat.getFormat(this.configurationDTO.dateFormat);
	}
	public String getJsonRPCUrl() {
		return jsonRPCUrl;
	}
}
