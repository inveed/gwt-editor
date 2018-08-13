package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;

//import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.BooleanPropertyModel;
import net.inveed.gwt.editor.client.types.JSBoolean;

public class BooleanPropertyEditor extends AbstractFormPropertyEditor<BooleanPropertyModel, JSBoolean> {
	//private static final Logger LOG = Logger.getLogger(BooleanPropertyEditor.class.getName());
	
	private ToggleSwitch checkbox;
	
	public BooleanPropertyEditor() {
		this.checkbox = new ToggleSwitch();
		this.checkbox.setOnIcon(IconType.CHECK);
		this.checkbox.setOffIcon(IconType.TIMES);
		this.checkbox.setSize(SizeType.MINI);
		
		this.checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				onValueChanged();
			}
		});
		this.add(this.checkbox);
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.checkbox;
	}
	@Override
	public void bind(JSEntity entity, BooleanPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		if (this.isReadonly()) {
			this.checkbox.setEnabled(false);
		}
		
		if (this.getOriginalValue() != null) {
			this.checkbox.setValue(this.getOriginalValue().getValue());
		}
	}
	
	@Override
	public void setValue(String v) {
		if (this.checkbox == null) {
			return;
		}
		if (v == null) {
			this.checkbox.setValue(false);
			return;
		}
		v = v.trim().toLowerCase();
		if (v.equals("true") || v.equals("yes") || v.equals("1")) {
			this.checkbox.setValue(true);
		} else {
			this.checkbox.setValue(false);
		}
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public JSBoolean getValue() {
		if (this.checkbox.getValue() == null) {
			return null;
		}
		if (this.checkbox.getValue() == true) {
			return JSBoolean.TRUE;
		} else {
			return JSBoolean.FALSE;
		}
	}

	@Override
	public void setId(String uid) {
		this.checkbox.setId(uid);
	}

	@Override
	public void setEnabled(boolean value) {
		this.checkbox.setEnabled(value);
	}
}
