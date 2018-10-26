package net.inveed.gwt.editor.client.model.properties;

import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;

public interface IPropertyInstantiator<D extends AbstractPropertyDTO, P extends IPropertyDescriptor<?>> {
	P create(D dto, String name, EntityModel entityModel);
}
