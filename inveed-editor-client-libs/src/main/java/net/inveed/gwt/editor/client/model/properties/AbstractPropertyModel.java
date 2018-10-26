package net.inveed.gwt.editor.client.model.properties;

import java.util.ArrayList;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;

public abstract class AbstractPropertyModel<T extends IJSObject, D extends AbstractPropertyDTO> implements IPropertyDescriptor<T> {
	
	public static final String C_CONST_KEY_PREFIX = "properties";
	public static final String C_CONST_HINTS_PREFIX = "hints";
	public static final String C_CONST_ERROR_PREFIX = "errors";

	private final String      name;
	private final D           dto;
	private final EntityModel containerModel;
	
	public AbstractPropertyModel(D model, String name, EntityModel entity) {
		assert(model != null);
		this.dto = model;
		this.name = name;
		this.containerModel = entity;
	}
	
	protected D getDTO() {
		return this.dto;
	}
	
	@Override
	public EntityModel getEntityModelWrapper() {
		return this.containerModel;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public JSONValue getJSONValue(JSONObject entity) {
		if (entity.containsKey(this.getName())) {
			return entity.get(this.getName());
		} else {
			return JSONNull.getInstance();
		}
	}
	
	@Override
	public final T getValue(JSONObject entity, EntityManager em) {
		JSONValue v = this.getJSONValue(entity);
		return this.convertToJSObject(v, em);
	}
		
	private String[] getLocalizationPrefixes(String view, String ... prefixBase) {
		ArrayList<String> prefixes = new ArrayList<>();
		if (view != null) {
			view = view.trim();
			if (view.length() > 0) {
				prefixes.add(this.getEntityModelWrapper().getKey() + "." + "_view_" + view + "." + prefixBase);
			}
		}
		prefixes.add(this.getEntityModelWrapper().getKey() + "." + C_CONST_KEY_PREFIX);
		prefixes.add(C_CONST_KEY_PREFIX);
		return prefixes.toArray(new String[0]);
	}
	@Override
	public String getDisplayName(String viewName) {
		String ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1(this.getName(), getLocalizationPrefixes(viewName, C_CONST_KEY_PREFIX)));
		if (ret != null) {
			return ret;
		}
		
		return this.getName();
	}
	
	@Override
	public String getDisplayHint(String viewName) {
		return JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1(this.getName(), getLocalizationPrefixes(viewName, C_CONST_HINTS_PREFIX)));
	}
	
	@Override
	public boolean isId() {
		return false;
	}
	
	@Override
	public boolean isValid() {
		return true;
	}
}
