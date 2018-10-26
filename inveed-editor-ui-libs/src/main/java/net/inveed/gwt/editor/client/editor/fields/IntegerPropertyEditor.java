package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.base.validator.DecimalMaxValidator;
import gwt.material.design.client.base.validator.DecimalMinValidator;
import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialLongBox;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IntegerFieldModel;
import net.inveed.gwt.editor.client.types.JSLong;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class IntegerPropertyEditor extends AbstractFormPropertyEditor<IntegerFieldModel, JSLong> {
	public static final IPropertyEditorFactory<IntegerFieldModel> createEditorFactory() {
		return new IPropertyEditorFactory<IntegerFieldModel>() {
			@Override
			public AbstractFormPropertyEditor<IntegerFieldModel, ?> createEditor(IntegerFieldModel property, EditorFieldDTO dto) {
				return new IntegerPropertyEditor();
			}};
	}
	
	private MaterialLongBox tbInteger;
	
	public IntegerPropertyEditor() {
		this.tbInteger = new MaterialLongBox();
		this.tbInteger.setFieldType(FieldType.OUTLINED);
		
		this.tbInteger.addValueChangeHandler(new ValueChangeHandler<Long>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Long> event) {
				onValueChanged();
			}
		});
		
		this.tbInteger.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
		this.initWidget(this.tbInteger);
	}
	
	
	
	public  void bind(JSEntity entity, IntegerFieldModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		this.tbInteger.setLabel(this.getDisplayName());
		this.tbInteger.setReadOnly(this.isReadonly());
		
		if (this.getProperty().getMaxValue() != null) {
			this.tbInteger.addValidator(new DecimalMaxValidator<>(this.getProperty().getMaxValue()));
		}
		if (this.getProperty().getMinValue() != null) {
			this.tbInteger.addValidator(new DecimalMinValidator<>(this.getProperty().getMinValue()));
		}
		
		this.setInitialValue();
	}
	
	@Override
	public void setValue(JSLong v) {
		if (v == null) {
			this.tbInteger.setValue(null);
		}
		this.tbInteger.setValue(v.getValue());
	}
	
	@Override
	protected void onValueChanged() {
		super.onValueChanged();
		this.tbInteger.validate();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.tbInteger.getValue() == null) {
			return false;
		}
		if (this.tbInteger.getValue() == null) {
			return true;
		}
		if (!this.tbInteger.validate(false)) {
			return false;
		}
		return true;
	}

	@Override
	public JSLong getValue() {
		Long v = this.tbInteger.getValue();
		if (v == null) {
			return null;
		}
		return new JSLong(v);
	}

	

	@Override
	public boolean isModified() {
		if (this.isReadonly()) {
			return false;
		}
		if (this.getInitialValue() == null && this.tbInteger.getValue() == null) {
			return false;
		}
		if (this.getInitialValue() != null && this.tbInteger.getValue() != null) {
			return !this.getInitialValue().equals(this.tbInteger.getValue());
		}
		return true;
	}
	@Override
	public void setEnabled(boolean value) {
		this.tbInteger.setEnabled(value);
		if (!value) {
			this.tbInteger.clearErrorText();
			this.tbInteger.removeErrorModifiers();
		} else {
			this.tbInteger.validate();
		}
	}
	
	@Override
	public void setGrid(String grid) {
		this.tbInteger.setGrid(grid);
	}
}
