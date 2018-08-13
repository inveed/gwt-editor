package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.DoubleBox;
import org.gwtbootstrap3.client.ui.form.validator.DecimalMaxValidator;
import org.gwtbootstrap3.client.ui.form.validator.DecimalMinValidator;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.FloatPropertyModel;
import net.inveed.gwt.editor.client.types.JSDouble;

public class DoublePropertyEditor extends AbstractFormPropertyEditor<FloatPropertyModel, JSDouble> {
	
	private DoubleBox tbDouble;
	
	public DoublePropertyEditor() {
		this.tbDouble = new DoubleBox();
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
		this.add(this.tbDouble);
	}
	public void bind(JSEntity entity, FloatPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.tbDouble.setReadOnly(this.isReadonly());
		if (this.getProperty().getMaxValue() != null) {
			this.tbDouble.addValidator(new DecimalMaxValidator<Double>(this.getProperty().getMaxValue()));
		}
		if (this.getProperty().getMinValue() != null) {
			this.tbDouble.addValidator(new DecimalMinValidator<Double>(this.getProperty().getMinValue()));
		}
		
		if (this.getOriginalValue() != null) {
			this.tbDouble.setValue(this.getOriginalValue().getValue());
		}
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.tbDouble;
	}

	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		this.tbDouble.setValue(Double.parseDouble(v));
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
		if (this.getOriginalValue() == null && this.tbDouble.getValue() == null) {
			return false;
		}
		if (this.getOriginalValue() != null && this.tbDouble.getValue() != null) {
			return !this.getOriginalValue().equals(this.tbDouble.getValue());
		}
		return true;
	}
	@Override
	public void setId(String uid) {
		this.tbDouble.setId(uid);
	}
	@Override
	public void setEnabled(boolean value) {
		this.tbDouble.setEnabled(value);
	}
}
