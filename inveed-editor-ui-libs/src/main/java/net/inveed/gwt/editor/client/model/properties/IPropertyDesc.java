package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.Column;

import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.FormViewAttributesDTO;
import net.inveed.gwt.editor.shared.ListViewAttributesDTO;

public interface IPropertyDesc<T extends IJSObject> {
	FieldType getType();
	
	ListViewAttributesDTO isInListView(String viewName);
	FormViewAttributesDTO isInFormView(String viewName);

	String getName();

	AbstractPropertyEditor<?,?> createEditor();
	Column<JSEntity, ?> createTableColumn();

	boolean isReadonly(boolean isNewObject);
	boolean isRequired();
	
	EntityModel getEntityModelWrapper();

	String getDisplayName(String viewName);
	JSONValue getValue(JSONObject entity);
	T getValue(JSONObject entity, EntityManager em);
	T getRawValue(JSEntity entity);
	T convertToJSObject(JSONValue jsonValue, EntityManager em);
	int getOrderInListView(String viewName);
	String getDisplayHint(String viewName);
	
	T getDefaultValue();
	String getEnabledCondition();

	Integer getAsNameIndex();
	
	
}
