package net.inveed.gwt.editor.client.editor.fields;

import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.client.types.IJSObject;

public abstract class AbstractFormPropertyEditor<P extends IPropertyDesc<V>, V extends IJSObject> extends AbstractPropertyEditor<P, V> {
	public abstract void setId(String uid);
	
	public boolean isFormField() {
		return true;
	}

	public abstract void setEnabled(boolean value);
}
