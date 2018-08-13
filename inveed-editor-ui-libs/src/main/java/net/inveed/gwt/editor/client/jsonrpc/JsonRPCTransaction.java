package net.inveed.gwt.editor.client.jsonrpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCRequest.RequestResult;
import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;

public class JsonRPCTransaction {
	public static class TransactionResult {
		private final List<RequestResult> responseItems;
		
		public TransactionResult(List<RequestResult> responseItems) {
			this.responseItems = Collections.unmodifiableList(responseItems);
		}
		
		public int getSize() {
			return this.responseItems.size();
		}
		
		public List<RequestResult> getItems() {
			return this.responseItems;
		}
		
		public RequestResult getFirst() {
			if (this.responseItems.size() > 0) {
				return this.responseItems.get(0);
			}
			return null;
		}
	}
	
	public static final class TransactionError implements IError {
		public static final String TYPE = "JsonRPCTransactionError";
		private final List<RequestResult> responses;
		
		public TransactionError(List<RequestResult> responses) {
			this.responses = Collections.unmodifiableList(responses);
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
		
		public List<RequestResult> getResponses() {
			return this.responses;
		}
	}
	private PromiseImpl<TransactionResult, IError> promise;
	private final HashMap<Long, JsonRPCRequest> calls = new HashMap<>();
	
	public void add(String methodName, Map<String, JSONValue> params) {
		JsonRPCRequest r = new JsonRPCRequest(methodName, params);
		this.calls.put(r.getId(), r);
	}
	
	public void add(JsonRPCRequest r) {
		this.calls.put(r.getId(), r);
	}
	
	public JSONValue toJSON() {
		if (this.calls.size() == 0) {
			return JSONNull.getInstance();
		}
		ArrayList<JsonRPCRequest> rlist = new ArrayList<>(this.calls.values());
		if (this.calls.size() == 1) {
			return rlist.get(0).toJSON();
		} else {
			JSONArray ret = new JSONArray();
			
			for (int i = 0; i < rlist.size(); i++) {
				JSONObject o = rlist.get(i).toJSON();
				ret.set(i, o);
			}
			return ret;
		}
	}
	
	public int getCount() {
		return this.calls.size();
	}

	public JsonRPCRequest get(long id) {
		return this.calls.get(id);
	}
	
	public Collection<JsonRPCRequest> getRequests() {
		return Collections.unmodifiableCollection(this.calls.values());
	}

	public Promise<TransactionResult, IError> commit() {
		if (this.promise != null) {
			//TODO: error!
			return null;
		}

		JsonRPCClient c = new JsonRPCClient(this.toJSON().toString());
		this.promise = new PromiseImpl<>();
		Promise<JSONValue, IError> p = c.call(ConfigurationRegistry.INSTANCE.getJsonRPCUrl());
		p.thenApply(this::onComplete);
		p.onError(this::onError);
		
		return this.promise;
	}
	
	private void onResponseError(IError err, Throwable e) {
		this.promise.error(err, e);
	}
	
	private RequestResult parseSingleResponse(JSONObject jo) {

		JSONValue idVal = jo.get("id");
		if (idVal == null) {
			return null;
		}
		if (idVal.isNumber() == null) {
			return null;
		}

		long id = (long) idVal.isNumber().doubleValue();
		JsonRPCRequest request = this.get(id);
		if (request == null) {
			return null;
		}
		return request.parseSingleResponse(jo);
	}
	
	private Void onError(IError err, Throwable e) {
		this.onResponseError(err, e);
		return null;
	}
	private Void onComplete(JSONValue resp) {
		ArrayList<RequestResult> responseItems = new ArrayList<>();
		if (resp.isArray() != null) {
			JSONArray ja = resp.isArray();
			for (int i = 0; i < ja.size(); i++) {
				JSONValue v = ja.get(i);
				if (v.isObject() != null) {
					RequestResult responseItem = this.parseSingleResponse(v.isObject());
					if (responseItem == null) {
						//TODO: WARN
					}
					responseItems.add(responseItem);
				} else {
					//TODO: Warn!
				}
			}
		} else if (resp.isObject() != null) {
			RequestResult responseItem = this.parseSingleResponse(resp.isObject());
			if (responseItem == null) {
				this.promise.error(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
				return null;
			}
			responseItems.add(responseItem);
		} else {
			this.promise.error(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
			return null;
		}
		
		if (responseItems.size() == 0) {
			this.onResponseError(new JsonRPCClientError(JsonRPCClientError.ERR_CANNOT_PARSE_RESPONSE), null);
			return null;
		}
		
		JsonRPCError globalError = null;
		for (RequestResult responseItem : responseItems) {
			if (responseItem.error != null) {
				globalError = new JsonRPCError(0, "Other request finished with error", responseItem.result);
				break;
			}
		}
		
		for (RequestResult responseItem : responseItems) {
			if (responseItem.error != null) {
				responseItem.request.onResponseError(responseItem.error, null);
			} else {
				if (globalError == null)
					responseItem.request.onResponse(responseItem);
				else {
					responseItem.request.onResponseError(globalError, null);
				}
			}
		}

		if (globalError != null) {
			this.promise.error(new TransactionError(responseItems), null);
		} else {
			TransactionResult jsonResponse = new TransactionResult(responseItems);
			this.promise.complete(jsonResponse);
		}
		return null;
	}
}
