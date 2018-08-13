package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.FormControlStatic;

import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IntegerIDPropertyModel;
import net.inveed.gwt.editor.client.types.JSLong;

public class IntegerIDField extends AbstractFormPropertyEditor<IntegerIDPropertyModel, JSLong> {
	private FormControlStatic tbInteger;
	
	public IntegerIDField() {
		this.tbInteger = new FormControlStatic();
		//this.tbInteger.setReadOnly(true);
		this.add(this.tbInteger);
	}
	public  void bind(JSEntity entity, IntegerIDPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		if (this.getOriginalValue() != null) {
			this.setValue(this.getOriginalValue().getValue() + "");
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
		this.tbInteger.setText(v);
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public JSLong getValue() {
		return this.getOriginalValue();
	}
	

	@Override
	public boolean isModified() {
		return false;
	}
	@Override
	public void setEnabled(boolean value) {
	}
}
