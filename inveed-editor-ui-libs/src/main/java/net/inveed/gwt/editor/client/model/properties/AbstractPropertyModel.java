package net.inveed.gwt.editor.client.model.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;

import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.FormViewAttributesDTO;
import net.inveed.gwt.editor.shared.ListViewAttributesDTO;
import net.inveed.gwt.editor.shared.PropertyModelDTO;
import net.inveed.gwt.editor.shared.UIConstants;

public abstract class AbstractPropertyModel<T extends IJSObject> implements IPropertyDesc<T> {
	public static final String C_CONST_KEY_PREFIX = "properties";
	public static final String C_CONST_HINTS_PREFIX = "hints";
	public static final String C_CONST_ERROR_PREFIX = "errors";

	private static final Logger LOG = Logger.getLogger(AbstractPropertyModel.class.getName());

	private final String name;
	private final EntityModel containerModel;

	private boolean required;
	private boolean readonly;
	private FieldType type;

	private Integer asNameIndex;
	
	private String enabledCondition;

	private Map<String, ListViewAttributesDTO> listViews;
	private Map<String, FormViewAttributesDTO> formViews;
	
	public AbstractPropertyModel(PropertyModelDTO model, String name, EntityModel entity) {
		//this.nativeModel = model;
		assert(model != null);
		this.name = name;
		this.containerModel = entity;
		
		this.enabledCondition = model.enabledWhen;
		
		this.type = model.type;
		
		if (model.attributes != null) {
			if (model.attributes.required != null) 
				this.required = model.attributes.required;
			if (model.attributes.readonly != null) {
				this.readonly = model.attributes.readonly;
			}
			this.asNameIndex = model.attributes.asNameIndex;
		}
		if (model.listViews != null) {
			this.listViews = Collections.unmodifiableMap(model.listViews);
		} else {
			this.listViews = Collections.unmodifiableMap(new HashMap<>());
		}
		
		if (model.formViews != null) {
			this.formViews = Collections.unmodifiableMap(model.formViews);
		} else {
			this.formViews = Collections.unmodifiableMap(new HashMap<>());
		}
	}
	
	public boolean isRequired() {
		return this.required;
	};
	
	@Override
	public JSONValue getValue(JSONObject entity) {
		if (entity.containsKey(this.getName())) {
			return entity.get(this.getName());
		} else {
			return JSONNull.getInstance();
		}
	}
	
	@Override
	public final T getValue(JSONObject entity, EntityManager em) {
		JSONValue v = this.getValue(entity);
		return this.convertToJSObject(v, em);
	}

	@Override
	public Integer getAsNameIndex() {
		return asNameIndex;
	}
	
	@Override
	public EntityModel getEntityModelWrapper() {
		return this.containerModel;
	}
	
	@Override
	public FieldType getType() {
		return this.type;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public boolean isReadonly() {
		return this.readonly;
	}
	
	@Override
	public boolean isReadonly(boolean isNewObject) {
		if (this.isRequired() && isNewObject) {
			return false;
		}
		return this.isReadonly();
	}

	@Override
	public ListViewAttributesDTO isInListView(String viewName) {
		if (this.listViews == null) {
			LOG.fine("Field " + this.getName() + " has no list views");
			return null;
		}
		
		if (this.listViews.containsKey(viewName)) {
			LOG.fine("Field " + this.getName() + " is in view " + viewName);
			return this.listViews.get(viewName);
		}
		
		if (this.listViews.containsKey(UIConstants.ALL)) {
			LOG.fine("Field " + this.getName() + " is in all views ('" + viewName +"')");
			return this.listViews.get(UIConstants.ALL);
		}
		
		//TODO: убрать
		for (String v : this.listViews.keySet()) {
			LOG.fine("Field " + this.getName() + " is defined for list view '" + v + "'");
		}
		LOG.fine("Field " + this.getName() + " is not in view " + viewName);
		return null;
	}
	
	@Override
	public FormViewAttributesDTO isInFormView(String viewName) {
		if (this.formViews == null) {
			LOG.fine("Field " + this.getName() + " has no form views");
			return null;
		}
		if (this.formViews.containsKey(viewName)) {
			LOG.fine("Field " + this.getName() + " is in view " + viewName);
			return this.formViews.get(viewName);
		}
		if (this.formViews.containsKey(UIConstants.ALL)) {
			LOG.fine("Field " + this.getName() + " is in all views ('" + viewName +"')");
			return this.formViews.get(UIConstants.ALL);
		}
		
		//TODO: убрать
		for (String v : this.formViews.keySet()) {
			LOG.fine("Field " + this.getName() + " is defined for form view '" + v + "'");
		}
		LOG.fine("Field " + this.getName() + " is not in view " + viewName);
		return null;
	}
	
	public int getOrderInListView(String viewName) {
		if (this.listViews == null) {
			return -1;
		}
		if (this.listViews.containsKey(viewName)) {
			ListViewAttributesDTO props = this.listViews.get(viewName);
			return props.order;
		}
		if (this.listViews.containsKey(UIConstants.ALL)) {
			ListViewAttributesDTO props = this.listViews.get(UIConstants.ALL);
			return props.order;
		}
		return -1;
	}
	
	@Override
	public Column<JSEntity, ?> createTableColumn() {
		Column<JSEntity, ?> ret = new TextColumn<JSEntity>() {
			@Override
			public String getValue(JSEntity jo) {
				LOG.fine("Getting value of field " + getName());
				IJSObject value = jo.getProperty(AbstractPropertyModel.this.getName());
				if (value == null) {
					return "";
				} else {
					return value.toString();
				}
			}
		};
		ret.setSortable(true);
		return ret;
	}

	@Override
	public String getDisplayName(String viewName) {
		String ret = null;
		if (viewName != null) {
			viewName = viewName.trim();
			if (viewName.length() > 0) {
				ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() + "." + "views" + "." + viewName + "." + C_CONST_KEY_PREFIX + "." + this.getName()));
				if (ret != null) {
					return ret;
				}
			}
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() + "." + C_CONST_KEY_PREFIX + "." + this.getName()));
		if (ret != null) {
			return ret;
		}
		
		return this.getName();
	}
	
	@Override
	public String getDisplayHint(String viewName) {
		String ret = null;
		if (viewName != null) {
			viewName = viewName.trim();
			if (viewName.length() > 0) {
				ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() + "." + "views" + "." + viewName + "." + C_CONST_HINTS_PREFIX + "." + this.getName()));
				if (ret != null) {
					return ret;
				}
			}
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() + "." + C_CONST_HINTS_PREFIX + "." + this.getName()));
		if (ret != null) {
			return ret;
		}
		
		return null;
	}
	
	public String getError(String code, String viewName) {
		String ret = null;
		if (viewName != null) {
			viewName = viewName.trim();
			if (viewName.length() > 0) {
				ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() 
						+ "." + "views" 
						+ "." + viewName 
						+ "." + C_CONST_ERROR_PREFIX 
						+ "." + code
						+ "." + C_CONST_KEY_PREFIX 
						+ "." + this.getName() 
						));
				if (ret != null) {
					return ret;
				}
			}
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() 
				
				+ "."+ C_CONST_ERROR_PREFIX 
				+ "." + code
				+ "." + C_CONST_KEY_PREFIX 
				+ "." + this.getName()
				));
		if (ret != null) {
			return ret;
		}
		
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getEntityModelWrapper().getKey() 
				
				+ "."+ C_CONST_ERROR_PREFIX 
				+ "." + code
				));
		if (ret != null) {
			return ret;
		}
		
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(C_CONST_ERROR_PREFIX + "." + code));
		if (ret != null) {
			return ret;
		}
		
		return this.getName();
	}

	public String getEnabledCondition() {
		return enabledCondition;
	}
}
