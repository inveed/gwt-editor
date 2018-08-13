package net.inveed.gwt.editor.client.jsonrpc;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;

public class JsonRPCRequest {
	private static long __idCounter = 1;
	private static final Logger LOG = Logger.getLogger(JsonRPCRequest.class.getName());


	public static class RequestResult {
		public final JSONValue result;
		public final IError error;
		public final JsonRPCRequest request;
		
		public RequestResult(JSONValue result, IError error, JsonRPCRequest r) {
			this.result = result;
			this.error = error;
			this.request = r;
		}
	}
	
	public static final class InvalidResponseError implements IError {
		public static final String TYPE = "JsonRPCInvalidResponseError";
		private final String message;
		public InvalidResponseError(String message) {
			this.message = message;
		}
		@Override
		public String getType() {
			return TYPE;
		}

		@Override
		public String getMessage() {
			return this.message;
		}

		@Override
		public String getLocalizedMessage() {
			return this.message;
		}
		
	}
	
	private final long id;
	private final String methodName;
	private final Map<String, JSONValue> params;
	
	private Object context;
	
	private PromiseImpl<JSONValue, IError> promise;
	
	public JsonRPCRequest(String methodName, Map<String, JSONValue> params) {
		this.id = __idCounter++;
		this.methodName = methodName;
		this.params = params;
	}
	
	public long getId() {
		return id;
	}

	public String getMethodName() {
		return methodName;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}
	
	public JSONObject toJSON() {
		JSONObject req = new JSONObject();
		req.put("method", new JSONString(getMethodName()));
		req.put("jsonrpc", new JSONString("2.0"));
		req.put("id", new JSONNumber(this.getId()));
		if (params != null && params.size() > 0) {
			JSONObject po = new JSONObject();
			for (String k : params.keySet()) {
				po.put(k, params.get(k));
			}
			req.put("params", po);
		}
		return req;
	}

	RequestResult parseSingleResponse(JSONObject jo) {
		JSONValue idVal = jo.get("id");
		if (idVal == null) {
			return new RequestResult(null, new InvalidResponseError("Request ID not found in response"), this);
		}
		if (idVal.isNumber() == null) {
			return new RequestResult(null, new InvalidResponseError("ID not number found in response: '" + idVal.toString() + "'"), this);
		}
		
		long id = (long) idVal.isNumber().doubleValue();
		if (id != this.getId()) {
			return new RequestResult(null, new InvalidResponseError("Response ID '" + id + "' doesn't match with request ID'\" + this.id + \"' "), this);
		}
		
		if (jo.containsKey("error")) {
			JSONValue error = jo.get("error");
			if (error.isObject() != null) {
				JsonRPCError je = new JsonRPCError(error.isObject());
				return new RequestResult(null, je, this);
			} else {
				LOG.warning("Got invalid error response with error: '" + error.toString() + "'");
				return new RequestResult(null, new JsonRPCClientError(JsonRPCClientError.ERR_ERROR_NOT_OBJECT), this);
			}
		}
		
		JSONValue resultVal = jo.get("result");
		return new RequestResult(resultVal, null, this);
	}
	
	public Promise<JSONValue, IError> getPromise() {
		if (this.promise == null) {
			this.promise = new PromiseImpl<>();
		}
		return this.promise;
	}
	
	void onResponseError(IError err, Throwable e) {
		if (this.promise == null) {
			return;
		}
		this.promise.error(err, e);
	}
	
	void onResponse(RequestResult response) {
		if (this.promise == null) {
			return;
		}
		if (response.error != null) {
			this.promise.error(response.error, null);
		} else {
			this.promise.complete(response.result);
		}
	}
	
	public static final Promise<JSONValue, IError> makeCall(String methodName, Map<String, JSONValue> params) {
		JsonRPCRequest req = new JsonRPCRequest(methodName, params);
		return req.send();
	}
	
	public Promise<JSONValue, IError> send() {
		if (this.promise == null) {
			this.promise = new PromiseImpl<>();
			JsonRPCClient c = new JsonRPCClient(this.toJSON().toString());
			Promise<JSONValue, IError> p = c.call(ConfigurationRegistry.INSTANCE.getJsonRPCUrl());
			p.thenApply(this::onComplete);
			p.onError(this::onError);
		}
		
		return this.promise;
	}

	private Void onError(IError err, Throwable e) {
		this.onResponseError(err, e);
		return null;
	}

	private Void onComplete(JSONValue resp) {
		RequestResult item;
		if (resp.isObject() != null) {
			JSONObject jo = resp.isObject();
			item = this.parseSingleResponse(jo);
		} else {
			LOG.warning("Cannot parse JSON-RPC response:" + resp.toString());
			this.promise.error(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
			return null;
		}
		if (item == null) {
			LOG.warning("Unexpected NULL value from response parsing:" + resp.toString());
			this.promise.error(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
			return null;
		}

		if (item.error != null) {
			this.onResponseError(item.error, null);
		} else {
			this.onResponse(item);
		}
		return null;
	}
}
