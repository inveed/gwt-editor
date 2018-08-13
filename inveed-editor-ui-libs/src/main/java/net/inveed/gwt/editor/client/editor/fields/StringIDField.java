package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.client.ui.html.Div;
import org.gwtbootstrap3.client.ui.html.Text;

import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.StringIDPropertyModel;
import net.inveed.gwt.editor.client.types.JSString;

public class StringIDField extends AbstractFormPropertyEditor<StringIDPropertyModel, JSString> {
	
	private Div textBox;
	
	public StringIDField() {
		this.textBox = new Div();
		this.add(this.textBox);
	}
	
	public void bind(JSEntity entity, StringIDPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		
		if (this.getOriginalValue() != null) {
			this.setValue(this.getOriginalValue().getValue());
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
		this.textBox.clear();
		this.textBox.add(new Text(v));
	}
	
	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public JSString getValue() {
		return this.getOriginalValue();
	}

	@Override
	public void setEnabled(boolean value) {
	}
}
