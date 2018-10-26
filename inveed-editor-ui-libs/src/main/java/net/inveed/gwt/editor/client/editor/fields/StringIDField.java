package net.inveed.gwt.editor.client.editor.fields;

import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialTextBox;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.StringIDPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class StringIDField extends AbstractFormPropertyEditor<StringIDPropertyModel, JSString> {
	
	private MaterialTextBox textBox;
	
	public StringIDField() {
		this.textBox = new MaterialTextBox();
		this.textBox.setFieldType(FieldType.OUTLINED);
		this.initWidget(this.textBox);
	}
	
	public static final IPropertyEditorFactory<StringIDPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<StringIDPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<StringIDPropertyModel, ?> createEditor(StringIDPropertyModel property, EditorFieldDTO dto) {
				return new StringIDField();
			}};
	}
	
	public void bind(JSEntity entity, StringIDPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
	
		this.textBox.setLabel(this.getDisplayName());
		
		this.setInitialValue();
	}
		
	@Override
	public void setValue(JSString v) {
		if (v == null) {
			this.textBox.setValue(null);
		}
		this.textBox.setValue(v.getValue());
	}
	
	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public JSString getValue() {
		return this.getInitialValue();
	}

	@Override
	public void setEnabled(boolean value) {
	}
	
	@Override
	public void setGrid(String grid) {
		this.textBox.setGrid(grid);
	}
}
