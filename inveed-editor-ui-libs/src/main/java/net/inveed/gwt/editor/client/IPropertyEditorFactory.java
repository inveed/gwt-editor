package net.inveed.gwt.editor.client;

import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public interface IPropertyEditorFactory<T extends IPropertyDescriptor<?>> {
	
	public AbstractFormPropertyEditor<T, ?> createEditor(T property, EditorFieldDTO dto);

}
