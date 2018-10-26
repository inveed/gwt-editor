package net.inveed.gwt.editor.client.utils;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;


public class JsHttpClient {
	public static final class RequestResult {
		public final Request request;
		public final Response response;

		public RequestResult(Request request, Response response) {
			this.request = request;
			this.response = response;
		}
	}
	
	public static final class HTTPError implements IError {
		public static final String TYPE = "HTTPError";
		private Request request;
		public HTTPError(Request request) {
			this.request = request;
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
		
		public Request getRequest() {
			return this.request;
		}
		
		@Override
		public String getType() {
			return TYPE;
		}
	}
	
	public static Promise<RequestResult, HTTPError> doGet(String url, boolean auth) {
		JsHttpRequest req = new JsHttpRequest(url);
		req.setAuthenticate(auth);
		req.setMethod(RequestBuilder.GET);
		return req.execute();
	}

	public static Promise<RequestResult, HTTPError> doPost(String url, String data, boolean auth) {
		JsHttpRequest req = new JsHttpRequest(url);
		req.setAuthenticate(auth);
		req.setData(data);
		req.setMethod(RequestBuilder.POST);
		return req.execute();
	}
}
