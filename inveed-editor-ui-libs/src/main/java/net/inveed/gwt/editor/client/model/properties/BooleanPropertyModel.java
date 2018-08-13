package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.Column;

import net.inveed.gwt.editor.client.editor.fields.BooleanPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSBoolean;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class BooleanPropertyModel extends AbstractPropertyModel<JSBoolean> {
	private JSBoolean defaultValue;
	
	public BooleanPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.BOOLEAN) {
			//TODO: Exception!
		}
		this.defaultValue = JSBoolean.parse(field.defaultValue);
	}

	@Override
	public BooleanPropertyEditor createEditor() {
		return new BooleanPropertyEditor();
	}
	
	@Override
	public Column<JSEntity, ?> createTableColumn() {
		CheckboxCell cell = new CheckboxCell(false, false);
		
		Column<JSEntity, Boolean> ret = new Column<JSEntity, Boolean>(cell) {
			@Override
			public Boolean getValue(JSEntity row) {
				IJSObject val = row.getProperty(BooleanPropertyModel.this.getName());
				if (val == null) {
					return null;
				} else if (JSBoolean.TYPE.equals(val.getType())) {
					return ((JSBoolean) val).getValue();
				} else {
					return null;
				}
			}
		};
		return ret;
	}
	
	@Override
	public JSBoolean getRawValue(JSEntity entity) {
		JSBoolean ret = (JSBoolean) entity.getProperty(this.getName(), JSBoolean.TYPE);
		return ret;
	}
	
	@Override
	public JSBoolean convertToJSObject(JSONValue jsonValue, EntityManager em) {
		return JSBoolean.parse(jsonValue);
	}

	@Override
	public JSBoolean getDefaultValue() {
		return this.defaultValue;
	}
	
}
