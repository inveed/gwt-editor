package net.inveed.gwt.editor.client.editor.fields;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.base.validator.RegExValidator;
import gwt.material.design.client.base.validator.SizeValidator;
import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialValueBox;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.TextPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class SingleRowTextPropertyEditor extends AbstractFormPropertyEditor<TextPropertyModel, JSString> {
	
	//private static final String ERR_LENGTH_BETWEEN = "txtLengthBetween";
	//private static final String ERR_LENGTH_MORETHAN = "txtLengthMore";
	//private static final String ERR_LENGTH_LESSTHAN = "txtLengthLess";
	//private static final String ERR_LENGTH_LESSTHAN_NOEMPTY = "txtLengthLessNoEmpty";
	private  MaterialValueBox<String> textBox;
	
	public SingleRowTextPropertyEditor(boolean multiline) {
		if (!multiline) {
			this.textBox = new MaterialTextBox();
		} else {
			this.textBox = new MaterialTextArea();
			
		}
		this.textBox.setValidateOnBlur(true);
		this.textBox.setFieldType(FieldType.OUTLINED);
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
		
		
		this.initWidget(this.textBox);
	}
	
	
	public static final IPropertyEditorFactory<TextPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<TextPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<TextPropertyModel, ?> createEditor(TextPropertyModel property, EditorFieldDTO dto) {
				return new SingleRowTextPropertyEditor(property.isMultiline());
			}};
	}
	
	
	public  void bind(JSEntity entity, TextPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.textBox.setLabel(this.getDisplayName());
		this.textBox.setReadOnly(this.isReadonly());
		
		if (this.getProperty().getMinLength() != null || this.getProperty().getMaxLength() != null) {
			this.textBox.addValidator(new SizeValidator<>(this.getProperty().getMinLength(), this.getProperty().getMaxLength()));
		}

		if (this.getProperty().getRegEx() != null) {
			this.textBox.addValidator(new RegExValidator(this.getProperty().getRegEx(), this.getProperty().getRegexError(this.getViewName())));
		}
		
		/*
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
		*/
		
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
		if (!value) {
			this.textBox.clearErrorText();
			this.textBox.removeErrorModifiers();
		} else {
			this.textBox.validate();
		}
	}
	
	@Override
	public void setGrid(String grid) {
		this.textBox.setGrid(grid);
	}
}
