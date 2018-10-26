package net.inveed.gwt.editor.client.editor.fields;

import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialIntegerBox;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IntegerIDPropertyModel;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class IntegerIDField extends AbstractFormPropertyEditor<IntegerIDPropertyModel, JSLong> {
	public static final IPropertyEditorFactory<IntegerIDPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<IntegerIDPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<IntegerIDPropertyModel, ?> createEditor(IntegerIDPropertyModel property, EditorFieldDTO dto) {
				return new IntegerIDField();
			}};
	}
	
	private MaterialIntegerBox tbInteger;
	
	public IntegerIDField() {
		this.tbInteger = new MaterialIntegerBox();
		this.tbInteger.setFieldType(FieldType.OUTLINED);
		this.tbInteger.setReadOnly(true);
		
		this.initWidget(this.tbInteger);
	}
	
	public  void bind(JSEntity entity, IntegerIDPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		this.tbInteger.setLabel(this.getDisplayName());
		this.tbInteger.setReadOnly(true);
		
		this.setInitialValue();
	}
	
	@Override
	public void setValue(JSLong v) {
		this.tbInteger.setText(v.getDisplayValue());
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public JSLong getValue() {
		return this.getInitialValue();
	}
	

	@Override
	public boolean isModified() {
		return false;
	}
	@Override
	public void setEnabled(boolean value) {
	}
	
	@Override
	public void setGrid(String grid) {
		this.tbInteger.setGrid(grid);
	}
}
