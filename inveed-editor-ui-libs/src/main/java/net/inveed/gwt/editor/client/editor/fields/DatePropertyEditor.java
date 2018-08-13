package net.inveed.gwt.editor.client.editor.fields;

import java.util.Date;

import org.gwtbootstrap3.extras.datepicker.client.ui.DatePicker;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.DatePropertyModel;
import net.inveed.gwt.editor.client.types.JSDate;

public class DatePropertyEditor extends AbstractFormPropertyEditor<DatePropertyModel, JSDate> {
	private DatePicker datePicker;

	public DatePropertyEditor() {
		this.datePicker = new DatePicker();
		this.datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onValueChanged();
			}
		});
		/*this.datePicker.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});*/
		this.add(this.datePicker);
	}
	public void bind (JSEntity entity, DatePropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.datePicker.setReadOnly(this.isReadonly());
		if (this.getOriginalValue() != null) {
			this.datePicker.setValue(this.getOriginalValue().getValue());
		}
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.datePicker;
	}
	
	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		v = v.trim().toLowerCase();
		if (v.equals("now")) {
			this.datePicker.setValue(new Date());
			return;
		}
		//TODO: parse and set!
	}

	@Override
	protected void onValueChanged() {
		super.onValueChanged();
		this.datePicker.validate();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.datePicker.getValue() == null) {
			return false;
		}
		if (this.datePicker.getValue() == null) {
			return true;
		}
		if (!this.datePicker.validate(false)) {
			return false;
		}
		return true;
	}

	@Override
	public JSDate getValue() {
		return new JSDate(this.datePicker.getValue(), null); //TODO: подумать, что делать с форматом!
	}

	@Override
	public void setId(String uid) {
		this.datePicker.setId(uid);
	}
	@Override
	public void setEnabled(boolean value) {
		this.datePicker.setEnabled(value);
	}
}
