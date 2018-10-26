package net.inveed.gwt.editor.client;


import java.util.HashMap;

import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.TextColumn;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.BooleanPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.DatePropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.DoublePropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.DurationPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.EntityRefListBoxSelector;
import net.inveed.gwt.editor.client.editor.fields.EnumItemSelector;
import net.inveed.gwt.editor.client.editor.fields.IntegerIDField;
import net.inveed.gwt.editor.client.editor.fields.IntegerPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.BinaryPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.SingleRowTextPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.StringIDField;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.BinaryPropertyModel;
import net.inveed.gwt.editor.client.model.properties.BooleanPropertyModel;
import net.inveed.gwt.editor.client.model.properties.DurationPropertyModel;
import net.inveed.gwt.editor.client.model.properties.EntityReferencePropertyModel;
import net.inveed.gwt.editor.client.model.properties.EnumPropertyModel;
import net.inveed.gwt.editor.client.model.properties.FloatPropertyModel;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.client.model.properties.IntegerFieldModel;
import net.inveed.gwt.editor.client.model.properties.IntegerIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.StringIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TextPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TimestampPropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class UIRegistry {
	public static final UIRegistry INSTANCE = new UIRegistry();
	private final HashMap<Class<? extends IPropertyDescriptor<?>>, IPropertyEditorFactory<?>> editorFactories;
	private final HashMap<Class<? extends IPropertyDescriptor<?>>, IColumnFactory<?>> columnFactories;
	
	private UIRegistry() {
		this.editorFactories = new HashMap<>();
		this.columnFactories = new HashMap<>();
		
		this.registerEditors();
	}
	
	public <T extends IPropertyDescriptor<?>> AbstractFormPropertyEditor<?, ?> getPropertyEditor(T property, EditorFieldDTO dto) {
		@SuppressWarnings("unchecked")
		IPropertyEditorFactory<T> factory = (IPropertyEditorFactory<T>) this.editorFactories.get(property.getClass());
		if (factory == null) {
			//TODO: LOG
			return null;
		}
		return factory.createEditor(property, dto);
	}
	
	public <T extends IPropertyDescriptor<?>> Column<JSEntity, ?> getColumn(ListViewColumn<T> col) {
		@SuppressWarnings("unchecked")
		IColumnFactory<T> factory = (IColumnFactory<T>) this.columnFactories.get(col.getPropertyDescriptor().getClass());
		if (factory == null) {
			//TODO: LOG
			return null;
		}
		return factory.createListViewColumn(col);
	}
	
	public <T extends IPropertyDescriptor<?>> void registerFactory(Class<T> ptype, IPropertyEditorFactory<T> factory) {
		this.editorFactories.put(ptype, factory);
	}
	
	public <T extends IPropertyDescriptor<?>> void registerFactory(Class<T> ptype, IColumnFactory<?> factory) {
		this.columnFactories.put(ptype, factory);
	}
	
	public Column<JSEntity, ?> createListViewColumn(ListViewColumn<?> col) {
		Column<JSEntity, ?> ret = this.getColumn(col);
		if (ret != null) {
			return ret;
		}

		return new TextColumn<JSEntity>() {
			@Override
			public String getValue(JSEntity object) {
				IJSObject rawValue = col.getPropertyDescriptor().getValue(object);
				if (rawValue == null) {
					return "-";
				} else {
					return rawValue.getDisplayValue();
				}
			}
		};
	}
	
	private void registerEditors() {
		this.registerFactory(BooleanPropertyModel.class, BooleanPropertyEditor.createEditorFactory());
		this.registerFactory(TimestampPropertyModel.class, DatePropertyEditor.createEditorFactory());
		this.registerFactory(FloatPropertyModel.class, DoublePropertyEditor.createEditorFactory());
		this.registerFactory(DurationPropertyModel.class, DurationPropertyEditor.createEditorFactory());
		this.registerFactory(EntityReferencePropertyModel.class, EntityRefListBoxSelector.createEditorFactory());
		this.registerFactory(EnumPropertyModel.class, EnumItemSelector.createEditorFactory());
		this.registerFactory(IntegerIDPropertyModel.class, IntegerIDField.createEditorFactory());
		this.registerFactory(IntegerFieldModel.class, IntegerPropertyEditor.createEditorFactory());
		this.registerFactory(TextPropertyModel.class, SingleRowTextPropertyEditor.createEditorFactory());
		this.registerFactory(StringIDPropertyModel.class, StringIDField.createEditorFactory());
		this.registerFactory(BinaryPropertyModel.class, BinaryPropertyEditor.createEditorFactory());
		
		this.registerFactory(BooleanPropertyModel.class, BooleanPropertyEditor.createColumnFactory());
		this.registerFactory(EntityReferencePropertyModel.class, EntityRefListBoxSelector.createColumnFactory());
		this.registerFactory(EnumPropertyModel.class, EnumItemSelector.createColumnFactory());
	}

}
