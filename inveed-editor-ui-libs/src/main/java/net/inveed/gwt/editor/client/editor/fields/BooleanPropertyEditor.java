package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.constants.CheckBoxType;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.WidgetColumn;
import net.inveed.gwt.editor.client.IColumnFactory;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.BooleanPropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSBoolean;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class BooleanPropertyEditor extends AbstractFormPropertyEditor<BooleanPropertyModel, JSBoolean> {	
	public static final IPropertyEditorFactory<BooleanPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<BooleanPropertyModel>() {

			@Override
			public AbstractFormPropertyEditor<BooleanPropertyModel, ?> createEditor(BooleanPropertyModel property, EditorFieldDTO dto) {
				return new BooleanPropertyEditor();
			}};
	}
	
	public static final IColumnFactory<?> createColumnFactory() {
		return new IColumnFactory<BooleanPropertyModel>() {
			@Override
			public Column<JSEntity, ?> createListViewColumn(ListViewColumn<BooleanPropertyModel> col) {
				WidgetColumn<JSEntity, MaterialIcon> ret = new WidgetColumn<JSEntity, MaterialIcon>() {
					@Override
					public MaterialIcon getValue(JSEntity row) {
		                
						IJSObject val = row.getProperty(col.getPropertyDescriptor().getName());
						if (val == null) {
							return new MaterialIcon(IconType.REMOVE_CIRCLE_OUTLINE);
						} else if (JSBoolean.TYPE.equals(val.getType())) {
							JSBoolean v = (JSBoolean) val;
							if (v.getValue() == null) {
								return new MaterialIcon(IconType.REMOVE_CIRCLE_OUTLINE);
							} else if (v.getValue()) {
								return new MaterialIcon(IconType.RADIO_BUTTON_CHECKED);
							} else {
								return new MaterialIcon(IconType.RADIO_BUTTON_UNCHECKED);
							}
							
						} else {
							return new MaterialIcon(IconType.REMOVE_CIRCLE_OUTLINE);
						}
					}
				};

				return ret;
			}};
	}
	
	private MaterialCheckBox checkbox;
	
	public BooleanPropertyEditor() {
		this.checkbox = new MaterialCheckBox();
		this.checkbox.setType(CheckBoxType.FILLED);
		this.checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				onValueChanged();
			}
		});
		this.initWidget(this.checkbox);
	}
	
	@Override
	public void bind(JSEntity entity, BooleanPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		if (this.isReadonly()) {
			this.checkbox.setEnabled(false);
		}

		this.checkbox.setText(this.getDisplayName());
		
		this.setInitialValue();
	}
	
	@Override
	public void setValue(JSBoolean v) {
		if (this.checkbox == null) {
			return;
		}
		if (v == null) {
			this.checkbox.setValue(false);
			return;
		} else if (v.getValue() == null) {
			this.checkbox.setValue(false);
			return;
		} else {
			this.checkbox.setValue(v.getValue());
		}
		
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public JSBoolean getValue() {
		if (this.checkbox.getValue() == null) {
			return null;
		}
		if (this.checkbox.getValue() == true) {
			return JSBoolean.TRUE;
		} else {
			return JSBoolean.FALSE;
		}
	}

	@Override
	public void setEnabled(boolean value) {
		this.checkbox.setEnabled(value);
	}

	@Override
	public void setGrid(String grid) {
		this.checkbox.setGrid(grid);
	}
}
