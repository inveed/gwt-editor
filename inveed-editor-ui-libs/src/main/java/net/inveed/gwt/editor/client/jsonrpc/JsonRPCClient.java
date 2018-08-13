package net.inveed.gwt.editor.client.jsonrpc;


import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.HttpClientError;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;

public class JsonRPCClient {	
	private static final Logger LOG = Logger.getLogger(JsonRPCClient.class.getName());
	
	private PromiseImpl<JSONValue, IError> promise;
	private final String json;
	
	public JsonRPCClient(String json) {
		this.json = json;
	}
	
	Promise<JSONValue, IError> call(String url) {
		this.promise = new PromiseImpl<>();
		Promise<RequestResult, HTTPError> promise = JsHttpClient.doPost(url, this.json, true);
		promise.onError(this::onError);
		promise.thenApply(this::onComplete);
		return this.promise;
	}

	
	private Void onError(IError err, Throwable e) {
		LOG.log(Level.WARNING, "Got error while making a JSON-RPC call: " + err.getMessage(), e);
		this.promise.error(err, e);
		return null;
	}
	
	private Void onComplete(RequestResult result) {
		if (result.response == null) {
			LOG.warning("Response is absent");
			this.onError(new HttpClientError(HttpClientError.ERR_EMPTY_RESPONSE), null);
			return null;
		}
		if (result.response.getStatusCode() != 200) {
			LOG.warning("Response has invalid code (non-200)");
			this.onError(new HttpClientError(HttpClientError.ERR_STATUS), null);
			return null;
		}
		try {
			JSONValue resp = JSONParser.parseStrict(result.response.getText());
			if (resp == null) {
				LOG.warning("Cannot parse response");
				this.onError(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
				return null;
			}
			this.promise.complete(resp);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Cannot parse response", e);
			this.promise.error(new JsonRPCClientError(JsonRPCClientError.ERR_UNKNOWN), e);
			return null;
		}
		return null;
	}
}
