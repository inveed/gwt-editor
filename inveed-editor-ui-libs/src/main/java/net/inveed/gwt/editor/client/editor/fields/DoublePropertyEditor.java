package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.base.validator.DecimalMaxValidator;
import gwt.material.design.client.base.validator.DecimalMinValidator;
import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialDoubleBox;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.FloatPropertyModel;
import net.inveed.gwt.editor.client.types.JSDouble;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class DoublePropertyEditor extends AbstractFormPropertyEditor<FloatPropertyModel, JSDouble> {
	public static final IPropertyEditorFactory<FloatPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<FloatPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<FloatPropertyModel, ?> createEditor(FloatPropertyModel property, EditorFieldDTO dto) {
				return new DoublePropertyEditor();
			}};
	}
	
	private MaterialDoubleBox tbDouble;
	
	public DoublePropertyEditor() {
		this.tbDouble = new MaterialDoubleBox();
		this.tbDouble.setFieldType(FieldType.OUTLINED);
		this.tbDouble.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				onValueChanged();
			}
		});
		this.tbDouble.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
		
		this.initWidget(this.tbDouble);
	}
	
	public void bind(JSEntity entity, FloatPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		this.tbDouble.setReadOnly(this.isReadonly());
		this.tbDouble.setLabel(this.getDisplayName());
		
		if (this.getProperty().getMaxValue() != null) {
			this.tbDouble.addValidator(new DecimalMaxValidator<>(this.getProperty().getMaxValue()));
		}
		if (this.getProperty().getMinValue() != null) {
			this.tbDouble.addValidator(new DecimalMinValidator<>(this.getProperty().getMinValue()));
		}
		
		this.setInitialValue();
	}

	@Override
	public void setValue(JSDouble v) {
		if (v == null){
			this.tbDouble.setValue(null);
		}
		this.tbDouble.setValue(v.getValue());
	}
	
	@Override
	protected void onValueChanged() {
		super.onValueChanged();
		this.tbDouble.validate();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.tbDouble.getValue() == null) {
			return false;
		}
		if (this.tbDouble.getValue() == null) {
			return true;
		}
		if (!this.tbDouble.validate(false)) {
			return false;
		}
		return true;
	}
		
	@Override
	public JSDouble getValue() {
		Double v = this.tbDouble.getValue();
		if (v == null) {
			return null;
		}
		return new JSDouble(v);
	}
	

	@Override
	public boolean isModified() {
		if (this.isReadonly()) {
			return false;
		}
		if (this.getInitialValue() == null && this.tbDouble.getValue() == null) {
			return false;
		}
		if (this.getInitialValue() != null && this.tbDouble.getValue() != null) {
			return !this.getInitialValue().equals(this.tbDouble.getValue());
		}
		return true;
	}

	@Override
	public void setEnabled(boolean value) {
		this.tbDouble.setEnabled(value);
		if (!value) {
			this.tbDouble.clearErrorText();
			this.tbDouble.removeErrorModifiers();
		} else {
			this.tbDouble.validate();
		}
	}
	
	@Override
	public void setGrid(String grid) {
		this.tbDouble.setGrid(grid);
	}
}
