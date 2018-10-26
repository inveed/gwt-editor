package net.inveed.gwt.editor.client.editor.fields;

import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.client.types.IJSObject;

public abstract class AbstractFormPropertyEditor<P extends IPropertyDescriptor<V>, V extends IJSObject> extends AbstractPropertyEditor<P, V> {
	public boolean isFormField() {
		return true;
	}
	
	public abstract void setGrid(String grid);

	public abstract void setEnabled(boolean value);
}
