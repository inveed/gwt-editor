package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.form.validator.RegExValidator;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.NetworkAddressPropertyModel;
import net.inveed.gwt.editor.client.types.JSNetworkAddress;

public class NetworkAddressPropertyEditor extends AbstractFormPropertyEditor<NetworkAddressPropertyModel, JSNetworkAddress> {
	private static final String IPv4Regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
	
	private Input textBox;
	
	public NetworkAddressPropertyEditor() {
		this.textBox = new Input();
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
		RegExValidator rv = new RegExValidator(IPv4Regex);
		this.textBox.addValidator(rv);
		
		this.add(this.textBox);
	}
	public  void bind(JSEntity entity, NetworkAddressPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.textBox.setReadOnly(this.isReadonly());
		
		if (this.getOriginalValue() != null) {
			this.textBox.setValue(this.getOriginalValue().getValue());
		}
		
	}
	
	@Override
	public void setId(String uid) {
		this.textBox.setId(uid);
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
	public JSNetworkAddress getValue() {
		String v = this.textBox.getValue();
		if (v == null) {
			return null;
		}
		return new JSNetworkAddress(v);
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
	public void setEnabled(boolean value) {
		this.textBox.setEnabled(value);
	}
}
