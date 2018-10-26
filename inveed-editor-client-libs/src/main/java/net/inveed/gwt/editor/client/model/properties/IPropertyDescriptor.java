package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;

public interface IPropertyDescriptor<T extends IJSObject> {
	//ListViewAttributesDTO isInListView(String viewName);
	//FormViewAttributesDTO isInFormView(String viewName);

	String getName();

	//Column<JSEntity, ?> createTableColumn();

	boolean isReadonly(boolean isNewObject);
	boolean isRequired();
	
	EntityModel getEntityModelWrapper();

	String getDisplayName(String viewName);
	JSONValue getJSONValue(JSONObject entity);
	T getValue(JSONObject entity, EntityManager em);
	T getValue(JSEntity entity);
	T convertToJSObject(JSONValue jsonValue, EntityManager em);
	String getDisplayHint(String viewName);
	
	T getDefaultValue();
	String getEnabledCondition();

	Integer getAsNameIndex();
	
	boolean isValid();
	
	boolean isId();
}
