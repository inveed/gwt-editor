package net.inveed.gwt.editor.client.model;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Logger;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;

import net.inveed.gwt.editor.client.model.EntityModel.EntityModelMapper;
import net.inveed.gwt.editor.client.model.EnumModel.EnumModelMapper;
import net.inveed.gwt.editor.client.utils.HttpClientError;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.UrlHelper;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.EnumModelDTO;
import net.inveed.gwt.editor.shared.PlatformModelDTO;

public final class ModelLoader {
	public static interface PlatformModelDTOMapper extends ObjectMapper<PlatformModelDTO> {}
	
	private static final Logger LOG = Logger.getLogger(ModelLoader.class.getName());
	
	private final HashMap<String, EntityModelDTO> entities;
	private final HashMap<String,EnumModelDTO>    enums;
	private PlatformModelDTO configurationDTO;
	
	private Queue<String> pendingLoadEntities;
	private Queue<String> pendingLoadEnums;
	
	private PromiseImpl<Void, IError> promise;
	private String baseUrl;
	
	public Map<String, EntityModelDTO> getEnitiyModelDTOs() {
		return this.entities;
	}
	
	public Map<String, EnumModelDTO> getEnumModelDTOs() {
		return this.enums;
	}
	
	public PlatformModelDTO getPlatformConfiguration() {
		return this.configurationDTO;
	}
	
	public ModelLoader() {
		this.entities = new HashMap<>();
		this.enums = new HashMap<>();
	}
	
	public Promise<Void, IError> load(String url) {
		this.promise = new PromiseImpl<Void, IError>();
		this.baseUrl = url;
		
		LOG.info("Loading model with URL: " + url);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, true);
		p.thenApply(this::onHttpResponse);
		p.onError((HTTPError req, Throwable t) -> {
			this.promise.error(req, t);
			return null;
		});
		return this.promise;
	}
	
	private Void onHttpResponse(RequestResult r) {
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
			
			PlatformModelDTOMapper mapper = GWT.create(PlatformModelDTOMapper.class);
			PlatformModelDTO dto = mapper.read(r.response.getText());
			
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
		
		String url = UrlHelper.concat(this.baseUrl, "entity", ename);
		LOG.fine("URL:" + url);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, true);
		p.thenApply(this::onEntityHttpResponse);
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
		
		String url = UrlHelper.concat(this.baseUrl, "enum", ename);
		LOG.fine("URL:" + url);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, true);
		p.thenApply(this::onEnumHttpResponse);
		p.onError((HTTPError r, Throwable t) -> {
			this.promise.error(r,t);
			return null;
		});
	}
	
	private void onModelLoadComplete() {
		this.promise.complete(null);
	}
	
	public static EntityModelDTO parseEntityModel(String json) {
		GWT.log("Parsing model for json: " + json);
		EntityModelMapper mapper = GWT.create(EntityModelMapper.class);
		return mapper.read(json);
	}
	
	private Void onEntityHttpResponse(RequestResult r) {
		LOG.info("Got entity model result:");
		if (r.response != null) {
			LOG.fine("Got ENTITY Response: ");
			LOG.fine("Code: " + r.response.getStatusCode());
			LOG.fine(r.response.getText());
			if (r.response.getStatusCode() == 200) {
				EntityModelDTO dto = parseEntityModel(r.response.getText());
				if (dto == null) {
					LOG.warning("Cannot parse entity model DTO");
					this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_CANNOT_PARSE_ENTITY_MODEL), null);
					return null;
				}
				//TODO: добавиь проверку IS Valid
				LOG.fine("Parsed model for entity " + dto.name);
				this.entities.put(dto.name, dto);
				
				//TODO: сюда нужно добавить хэндлеры <EntityModel, EntityModelDTO>
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
	
	private Void onEnumHttpResponse(RequestResult r) {
		LOG.fine("Got enum model result:");
		if (r.response != null) {
			LOG.fine("Got ENUM Response: ");
			LOG.fine("Code: " + r.response.getStatusCode());
			LOG.fine(r.response.getText());
			if (r.response.getStatusCode() == 200) {
				LOG.fine("Parsing enum model from json: {}" + r.response.getText());
				EnumModelMapper mapper = GWT.create(EnumModelMapper.class);
				EnumModelDTO model = mapper.read(r.response.getText());
				
				//GWT.log("Model parsed for enum " + model.name);
				if (model == null) {
					LOG.warning("Cannot parse enum model");
					this.promise.error(new ConfigurationRegistryError(ConfigurationRegistryError.ERR_CANNOT_PARSE_ENUM_MODEL), null);
					
					//return null;
				} else {
					LOG.info("Parsed model for enum " + model.name);
					this.enums.put(model.name, model);
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
}
