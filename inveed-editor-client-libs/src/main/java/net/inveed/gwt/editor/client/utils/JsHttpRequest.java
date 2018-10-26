package net.inveed.gwt.editor.client.utils;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.inveed.gwt.editor.client.auth.AuthHelper;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;
import net.inveed.gwt.editor.commons.UIConstants;

public class JsHttpRequest {
	private String url;
	private boolean authenticate;
	private String data;
	private Method method;
	
	public JsHttpRequest(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public boolean isAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	private static final void addAuthCookie(RequestBuilder builder) {
		if (AuthHelper.INSTANCE.getToken() == null) {
			return;
		}
		Cookies.setCookie("udrmui-token", AuthHelper.INSTANCE.getToken());
	}
	private static final void addAuthHeader(RequestBuilder builder) {
		if (AuthHelper.INSTANCE.getToken() == null) {
			return;
		}
		builder.setHeader(UIConstants.AUTH_HEADER, AuthHelper.INSTANCE.getToken());
	}
	
	private void processResonse(PromiseImpl<RequestResult, HTTPError> promise, Request request, Response response) {
		if (response.getStatusCode() == 401) {
			if (AuthHelper.INSTANCE.getAuthCallback() != null) {
				AuthHelper.INSTANCE.getAuthCallback().authenticate().thenApply((v)-> {
					this.execute(promise);
					return null;
				}).onError((v,e)->{
					promise.complete(new RequestResult(request, response));
					return null;
				});
				return;
			}
		}
		promise.complete(new RequestResult(request, response));
	}
	
	public Promise<RequestResult, HTTPError> execute() {
		PromiseImpl<RequestResult, HTTPError> ret = new PromiseImpl<>();
		this.execute(ret);
		return ret;
	}
	private void execute(PromiseImpl<RequestResult, HTTPError> promise) {
		XMLHttpRequest req = XMLHttpRequest.create();
		RequestBuilder builder = new RequestBuilder(this.getMethod(), this.getUrl());
		builder.setIncludeCredentials(this.isAuthenticate());
		if (this.isAuthenticate()) {
			addAuthCookie(builder);
			addAuthHeader(builder);
		}
		try {
			builder.sendRequest(this.getData(), new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					promise.error(new HTTPError(request), exception);
				}

				public void onResponseReceived(Request request, Response response) {
					processResonse(promise, request, response);
				}
			});
			
		} catch (RequestException e) {
			promise.error(null, null);
		}
	}
}
