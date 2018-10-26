package net.inveed.gwt.editor.client.i18n;

import java.util.logging.Logger;

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.utils.JsHttpClient;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.client.utils.JsHttpClient.HTTPError;
import net.inveed.gwt.editor.client.utils.JsHttpClient.RequestResult;

public class Localizer {
	private static final Logger LOG = Logger.getLogger(Localizer.class.getName());
	
	public static final Localizer INSTANCE = new Localizer();
	
	private static final String prefix = "i18n/messages";
	private Localizer() {
	}
	
	public Promise<Void, IError> init() {
		LOG.info("Initializing Localizer");
		Promise<RequestResult, HTTPError> promise = this.loadLocaleFile(prefix + ".json");
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		
		promise.thenApply((RequestResult v)->{
			LOG.fine("Default Localization data loaded. Trying to parse");
			this.defaultRoot = JSONParser.parseStrict(v.response.getText()).isObject();
			LOG.info("Default localization data parsed");
			ret.complete(null);
			return null;
		});
	
		promise.onError((HTTPError err, Throwable t) -> {
			LOG.warning("Cannot load default localization data");
			ret.error(err, t);
			return null;
		});
		return ret;
	}
	
	private LocaleInfo currentLocale;
	
	private JSONObject defaultRoot;
	private JSONObject shortRoot;
	private JSONObject longRoot;
	
	public void setLocale(LocaleInfo loc) {
		LOG.info("Setting locale: " + loc.getLocaleName());
		
		if (this.currentLocale != null && loc.getLocaleName().equals(this.currentLocale.getLocaleName())) {
			LOG.info("Already installed. Nothing to change.");
			return;
		}
		String [] lparts = loc.getLocaleName().split("_");
		if (this.defaultRoot == null) {
			Promise<RequestResult, HTTPError> promise = this.loadLocaleFile(prefix + ".json");
			promise.thenApply((RequestResult v)->{
				this.defaultRoot = JSONParser.parseStrict(v.response.getText()).isObject();
				return null;
			});
			
			promise.onError((HTTPError err, Throwable t) -> {
				LOG.warning("Cannot load locale file '" + prefix + ".json'");
				return null;
			});
		}
		if (lparts.length == 2) {
			Promise<RequestResult, HTTPError> lPromise = this.loadLocaleFile(prefix + "_" + loc + ".json");
			lPromise.thenApply((RequestResult v)->{
				this.longRoot = JSONParser.parseStrict(v.response.getText()).isObject();
				return null;
			});
			
			lPromise.onError((HTTPError err, Throwable t) -> {
				LOG.warning("Cannot load locale file '" + prefix + "_" + loc + ".json'");
				return null;
			});
			
			Promise<RequestResult, HTTPError> sPromise = this.loadLocaleFile(prefix + "_" + lparts[0] + ".json");
			sPromise.thenApply((RequestResult v)->{
				this.shortRoot = JSONParser.parseStrict(v.response.getText()).isObject();
				return null;
			});
			
			sPromise.onError((HTTPError err, Throwable t) -> {
				LOG.warning("Cannot load locale file '" + prefix + "_" + lparts[0] + ".json'");
				return null;
			});
		} else {
			Promise<RequestResult, HTTPError> sPromise = this.loadLocaleFile(prefix + "_" + lparts[0] + ".json");
			sPromise.thenApply((RequestResult v)->{
				this.longRoot = JSONParser.parseStrict(v.response.getText()).isObject();
				return null;
			});
			
			sPromise.onError((HTTPError err, Throwable t) -> {
				LOG.warning("Cannot load locale file '" + prefix + "_" + lparts[0] + ".json'");
				return null;
			});
			this.longRoot = null;
		}
	}
	
	private Promise<RequestResult, HTTPError> loadLocaleFile(String url) {
		Promise<RequestResult, HTTPError> p = JsHttpClient.doGet(url, false);
		return p;
	}
	
	public JSONValue getMessage1(String code, String ... prefixes) {
		if (prefixes.length == 0) {
			return this.getMessage(code);
		}
		for (String prefix : prefixes) {
			String c = prefix + "." + code;
			JSONValue ret  = this.getMessage(c);
			if (ret.isNull() == null) {
				return ret;
			}
		}
		return JSONNull.getInstance();
	}

	private JSONValue getMessage(String code) {
		LOG.fine("Requested localized message for code '" + code + "'");
		String[] codeParts = code.split("\\.");
	
		if (longRoot != null) {
			JSONValue ret = getFromRoot(longRoot, codeParts);
			if (ret != null && ret.isNull() == null) {
				LOG.fine("Localized message for code '" + code + "' found with long locale");
				return ret;
			}
		}
		if (shortRoot != null) {
			JSONValue ret = getFromRoot(shortRoot, codeParts);
			if (ret != null && ret.isNull() == null) {
				LOG.fine("Localized message for code '" + code + "' found with short locale");
				return ret;
			}
		}
		if (defaultRoot != null) {
			JSONValue ret = getFromRoot(defaultRoot, codeParts);
			if (ret != null && ret.isNull() == null) {
				LOG.fine("Localized message for code '" + code + "' found with default locale");
				return ret;
			}
		}
		LOG.warning("Localized message for code '" + code + "' not found");
		return JSONNull.getInstance();
	}
	
	private static final JSONValue getFromRoot(JSONObject root, String[] parts) { 
		for (int i = 0; i < parts.length; i++) {
			String code = parts[i];
			if (!root.containsKey(code)) {
				return null;
			}
			JSONValue v = root.get(code);
			if (i == parts.length - 1) {
				return v;
			}
			if (v.isObject() != null) {
				root = v.isObject();
				continue;
			}
			LOG.warning("Unknown error");
			return null;
		}
		return null;
	}

	public boolean isInitialized() {
		return this.defaultRoot != null;
	}
}
