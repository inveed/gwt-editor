package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.IntegerBox;
import org.gwtbootstrap3.client.ui.form.validator.DecimalMaxValidator;
import org.gwtbootstrap3.client.ui.form.validator.DecimalMinValidator;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IntegerFieldModel;
import net.inveed.gwt.editor.client.types.JSLong;

public class IntegerPropertyEditor extends AbstractFormPropertyEditor<IntegerFieldModel, JSLong> {
	private IntegerBox tbInteger;
	
	public IntegerPropertyEditor() {
		this.tbInteger = new IntegerBox();
		this.tbInteger.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				onValueChanged();
			}
		});
		
		this.tbInteger.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
		this.add(this.tbInteger);
	}
	public  void bind(JSEntity entity, IntegerFieldModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.tbInteger.setReadOnly(this.isReadonly());
		if (this.getProperty().getMaxValue() != null) {
			this.tbInteger.addValidator(new DecimalMaxValidator<Integer>(this.getProperty().getMaxValue().intValue()));
		}
		if (this.getProperty().getMinValue() != null) {
			this.tbInteger.addValidator(new DecimalMinValidator<Integer>(this.getProperty().getMinValue().intValue()));
		}
		
		if (this.getOriginalValue() != null) {
			this.tbInteger.setValue(this.getOriginalValue().getValue().intValue());
		}
	}
	
	@Override
	public void setId(String uid) {
		this.tbInteger.setId(uid);
	}
	@Override
	protected Widget getChildWidget() {
		return this.tbInteger;
	}
	
	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		this.tbInteger.setValue(Integer.parseInt(v));
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
		Integer v = this.tbInteger.getValue();
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
		if (this.getOriginalValue() == null && this.tbInteger.getValue() == null) {
			return false;
		}
		if (this.getOriginalValue() != null && this.tbInteger.getValue() != null) {
			return !this.getOriginalValue().equals(this.tbInteger.getValue());
		}
		return true;
	}
	@Override
	public void setEnabled(boolean value) {
		this.tbInteger.setEnabled(value);
	}
}
