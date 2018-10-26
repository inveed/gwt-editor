package net.inveed.gwt.editor.client.editor.fields;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import gwt.material.design.client.base.error.BasicEditorError;
import gwt.material.design.client.base.validator.Validator;
import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.MaterialDatePicker;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.TimestampPropertyModel;
import net.inveed.gwt.editor.client.types.JSTimestamp;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class DatePropertyEditor extends AbstractFormPropertyEditor<TimestampPropertyModel, JSTimestamp> {
	public static final IPropertyEditorFactory<TimestampPropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<TimestampPropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<TimestampPropertyModel, ?> createEditor(TimestampPropertyModel property, EditorFieldDTO dto) {
				return new DatePropertyEditor();
			}};
	}
	
	
	private MaterialDatePicker datePicker;

	public DatePropertyEditor() {
		this.datePicker = new MaterialDatePicker();
		this.datePicker.setFieldType(FieldType.OUTLINED);
		this.datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onValueChanged();
			}
		});
		this.datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				onValueChanged();
			}
		});
		this.datePicker.addValidator(new Validator<Date>() {
			
			@Override
			public List<EditorError> validate(Editor<Date> editor, Date value) {
				EditorError err = validateSingle(editor, value);
				ArrayList<EditorError> ret = new ArrayList<>();
				if (err != null) {
					ret.add(err);
				}
				return ret;
			}
			
			public EditorError validateSingle(Editor<Date> editor, Date value) {
				if (value == null) { 
					return null;
				}
				Long notBefore = getProperty().getNotBeforeMsec();
				Long notAfter = getProperty().getNotAfterMsec();
				if (notBefore != null & value.getTime() < notBefore) {
					return new BasicEditorError(editor, value, "NOT_BEFORE");
				} else if (notAfter != null && value.getTime() > notAfter) {
					return new BasicEditorError(editor, value, "NOT_AFTER");
				} else {
					return null;
				}
			}
			
			
			@Override
			public int getPriority() {
				return 0;
			}
		});
		this.initWidget(this.datePicker);
	}
	
	
	public void bind (JSEntity entity, TimestampPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		this.datePicker.setReadOnly(this.isReadonly());
		this.datePicker.setPlaceholder(this.getDisplayName());
		
		this.setInitialValue();
	}
	
	@Override
	public void setValue(JSTimestamp v) {
		if (v == null) {
			this.datePicker.setValue(null);
			return;
		}
		this.datePicker.setValue(v.getValue());
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
	public JSTimestamp getValue() {
		return new JSTimestamp(this.datePicker.getValue(), this.getProperty().getFormat()); //TODO: подумать, что делать с форматом!
	}

	@Override
	public void setEnabled(boolean value) {
		this.datePicker.setEnabled(value);
		if (!value) {
			this.datePicker.clearErrorText();
		} else {
			this.datePicker.validate();
		}
	}
	
	@Override
	public void setGrid(String grid) {
		this.datePicker.setGrid(grid);
	}
}
