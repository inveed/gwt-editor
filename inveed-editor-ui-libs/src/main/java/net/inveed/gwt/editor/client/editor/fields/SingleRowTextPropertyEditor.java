package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.base.ValueBoxBase;
import org.gwtbootstrap3.client.ui.form.validator.RegExValidator;
import org.gwtbootstrap3.client.ui.form.validator.SizeValidator;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.TextPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.client.utils.StringFormatter;

public class SingleRowTextPropertyEditor extends AbstractFormPropertyEditor<TextPropertyModel, JSString> {
	
	private static final String ERR_LENGTH_BETWEEN = "txtLengthBetween";
	private static final String ERR_LENGTH_MORETHAN = "txtLengthMore";
	private static final String ERR_LENGTH_LESSTHAN = "txtLengthLess";
	private static final String ERR_LENGTH_LESSTHAN_NOEMPTY = "txtLengthLessNoEmpty";
	private ValueBoxBase<String> textBox;
	
	public SingleRowTextPropertyEditor(boolean multiline) {
		if (!multiline) {
			this.textBox = new Input();
		} else {
			this.textBox = new TextArea();
			
		}
		this.textBox.setValidateOnBlur(true);
		this.textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onValueChanged();
			}
		});
		
		this.textBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				onValueChanged();
			}
		});
		
		this.add(this.textBox);
	}
	
	@Override
	public void setId(String uid) {
		this.textBox.setId(uid);
	}
	public  void bind(JSEntity entity, TextPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.textBox.setReadOnly(this.isReadonly());
		String nregex = this.getProperty().getNativeRegex();
		if (nregex != null) {
			RegExValidator rv = new RegExValidator(nregex, this.getProperty().getNativeRegexError(this.getViewName()));
			this.textBox.addValidator(rv);
		}
		
		String regex = this.getProperty().getRegEx();
		if (regex != null) {
			RegExValidator rv = new RegExValidator(regex, this.getProperty().getRegexError(this.getViewName()));
			this.textBox.addValidator(rv);
		}
		
		if (this.getProperty().getMaxLength() != null && this.getProperty().getMinLength() != null) {
			SizeValidator<String> sv = new SizeValidator<>(this.getProperty().getMinLength(),
					this.getProperty().getMaxLength(),
					StringFormatter.format(this.getProperty().getError(ERR_LENGTH_BETWEEN, this.getViewName()), this.getProperty().getMinLength(), this.getProperty().getMaxLength()));
			this.textBox.addValidator(sv);
		} else if (this.getProperty().getMaxLength() != null ) {
			SizeValidator<String> sv = new SizeValidator<>(
					this.getProperty().isRequired() ? 1 : 0,
					this.getProperty().getMaxLength(),
					StringFormatter.format(this.getProperty().getError(
							this.getProperty().isRequired() ? ERR_LENGTH_LESSTHAN_NOEMPTY : ERR_LENGTH_LESSTHAN, 
									this.getViewName()), 
							this.getProperty().getMaxLength())
					);
			this.textBox.addValidator(sv);
		} else if (this.getProperty().getMinLength() != null) {
			SizeValidator<String> sv = new SizeValidator<>(this.getProperty().getMinLength(),
					Integer.MAX_VALUE,
					StringFormatter.format(this.getProperty().getError(ERR_LENGTH_MORETHAN, this.getViewName()), this.getProperty().getMinLength()));
			this.textBox.addValidator(sv);
		}
		
		if (this.getProperty().getMaxLength() != null) {
			this.textBox.setMaxLength(this.getProperty().getMaxLength());
		}
		
		if (this.getOriginalValue() != null) {
			this.textBox.setValue(this.getOriginalValue().getValue());
		}
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.textBox;
	}
	
	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		this.textBox.setValue(v);
	}
	
	
	@Override
	protected void onValueChanged() {
		super.onValueChanged();
		this.textBox.validate();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.textBox.getValue() == null) {
			return false;
		}
		if (this.textBox.getValue() == null) {
			return true;
		}
		if (!this.textBox.validate(false)) {
			return false;
		}
		return true;
	}

	@Override
	public JSString getValue() {
		String v = this.textBox.getValue();
		if (v == null) {
			return null;
		}
		return new JSString(v);
	}

	@Override
	public void setEnabled(boolean value) {
		this.textBox.setEnabled(value);
	}
}
