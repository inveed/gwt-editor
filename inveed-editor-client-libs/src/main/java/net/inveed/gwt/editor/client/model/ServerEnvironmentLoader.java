package net.inveed.gwt.editor.client.model;

import java.util.logging.Logger;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.HttpClientError;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;
import net.inveed.gwt.editor.shared.ServerEnvironmentDTO;

public class ServerEnvironmentLoader {
	public static interface ServerEnvironmentDTOMapper extends ObjectMapper<ServerEnvironmentDTO> {}
	
	private static final Logger LOG = Logger.getLogger(ServerEnvironmentLoader.class.getName());
	
	private PromiseImpl<Void, IError> promise;
	
	private String baseUrl;
	private String jsonRpcUrl;
	private ServerEnvironmentDTO dto;
	
	public Promise<Void, IError> load() {
		this.promise = new PromiseImpl<Void, IError>();
		
		String jsconfUrl = "config.json";
		Promise<RequestResult, HTTPError> loadConfigFilePromise = JsHttpClient.doGet(jsconfUrl, false);
		
		loadConfigFilePromise.thenApply((RequestResult v)->{
			LOG.fine("Default Configuration loaded. Trying to parse");
			JSONValue conf = JSONParser.parseStrict(v.response.getText()).isObject();
			LOG.info("Default localization data parsed");
			if (conf.isObject() != null) {
				JSONObject o = conf.isObject();
				String configUrl = JsonHelper.safeGetString(o, "configUrl");
				if (configUrl == null) {
					this.promise.error(null, null);
					return null;
				}
				String modelUrl = JsonHelper.safeGetString(o, "modelUrl");
				this.jsonRpcUrl = JsonHelper.safeGetString(o, "jsonRpcUrl");
				this.baseUrl = modelUrl;
				load(configUrl);
			}else {
				this.promise.error(null, null);
			}
			return null;
		});
	
		loadConfigFilePromise.onError((HTTPError err, Throwable t) -> {
			LOG.warning("Cannot load configuration");
			this.promise.error(null, null);
			return null;
		});
		return this.promise;
	}
	
	private void load(String confUrl) {	
		LOG.info("Loading model with URL: " + confUrl);
		
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(confUrl, true);
		p.thenApply(this::onHttpResponse);
		p.onError((HTTPError req, Throwable t) -> {
			this.promise.error(req, t);
			return null;
		});
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
			
			ServerEnvironmentDTOMapper mapper = GWT.create(ServerEnvironmentDTOMapper.class);
			this.dto = mapper.read(r.response.getText());
		}
		this.promise.complete(null);
		return null;
	}

	public ServerEnvironmentDTO getEnvironmentDTO() {
		return this.dto;
	}
	
	public String getJsonRPCUrl() {
		if (this.jsonRpcUrl != null) {
			return this.jsonRpcUrl;
		} else {
			return this.dto.jsonRpcUrl;
		}
	}

	public String getModelUrl() {
		return this.baseUrl;
	}
}
