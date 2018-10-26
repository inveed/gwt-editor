package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.html.Option;
import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.TextColumn;
import net.inveed.gwt.editor.client.IColumnFactory;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.model.EnumModel;
import net.inveed.gwt.editor.client.model.EnumModel.JSEnumValue;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.EnumPropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class EnumItemSelector extends AbstractFormPropertyEditor<EnumPropertyModel, JSEnumValue> {
	public static final IPropertyEditorFactory<EnumPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<EnumPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<EnumPropertyModel, ?> createEditor(EnumPropertyModel property, EditorFieldDTO dto) {
				return new EnumItemSelector();
			}};
	}
	
	public static final IColumnFactory<?> createColumnFactory() {
		return new IColumnFactory<EnumPropertyModel>() {
			
			@Override
			public Column<JSEntity, ?> createListViewColumn(ListViewColumn<EnumPropertyModel> col) {
				TextColumn<JSEntity> ret = new TextColumn<JSEntity>() {
					
					@Override
					public String getValue(JSEntity row) {
						IJSObject v = col.getPropertyDescriptor().getValue(row);
						if (v == null) {
							if (col.getPropertyDescriptor().getNotSetText() == null) {
								return "-- NOT SET --";
							} else {
								return col.getPropertyDescriptor().getNotSetText();
							}
						}
						return v.getDisplayValue();
					}
				};
				return ret;
			}
		};
	}
	
	private MaterialListBox list;
	private EnumModel enumModel;
	
	public EnumItemSelector() {
		this.list = new MaterialListBox();
		this.list.setFieldType(FieldType.OUTLINED);
		
		this.initWidget(this.list);
	}
	
	
	public  void bind(JSEntity entity, EnumPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.enumModel = field.getEnumModel();
		
		this.list.setPlaceholder(this.getDisplayName());
		this.list.setEnabled(false);
		
		if (!this.getProperty().isRequired()) {
			Option o = new Option();
			o.setValue("");
			if (this.getProperty().getNotSetText() != null) {
				o.setText(this.getProperty().getNotSetText());
			} else {
				o.setText("-- NOT SET --");
			}
			if (field.getDefaultValue() == null) {
				o.setSelected(true);
			}
			this.list.add(o);
		}
		for (String k : this.enumModel.getCodes()) {
			JSEnumValue v = this.enumModel.getByCode(k);
			if (v == null) {
				continue;
			}
			Option o = new Option();
			o.setValue(v.getCode());
			o.setText(v.getValue());
			if (field.getDefaultValue() != null && field.getDefaultValue().getCode().equals(v.getCode())) { 
				o.setSelected(true);
			} else {
				o.setSelected(false);
			}
			this.list.add(o);
		}
		
		if (this.enumModel.getCodes().size() > 10) {
			//this.list.setLiveSearch(true);
			//this.list.setLiveSearchNormalize(true);
		}
		
		if (this.getInitialValue() != null) {
			this.select(this.getInitialValue().getCode());
		}
		
		this.list.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onValueChanged();
			}
		});
		this.list.setEnabled(!this.isReadonly());
	}
	
	@Override
	public void setValue(JSEnumValue v) {
		if (v == null) {
			return;
		}
		this.select(v.getCode());
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.list.getSelectedValue() == null) {
			return false;
		}
		return true;
	}

	@Override
	public JSEnumValue getValue() {
 		String sv = this.list.getSelectedValue();
		if (sv == null || sv.length() == 0) {
			return null;
		}
		return this.enumModel.getByCode(sv);
	}
	
	private void select(String value) {
		int si = this.list.getIndex(value);
		if (si > 0) {
			this.list.setSelectedIndex(si);
		}
	}
	@Override
	public void setEnabled(boolean value) {
		this.list.setEnabled(value);
	}
	
	@Override
	public void setGrid(String grid) {
		this.list.setGrid(grid);
	}
}
