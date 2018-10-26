package net.inveed.gwt.editor.client.model.properties;

import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.properties.MutablePropertyDTO;

public abstract class AbstractMutablePropertyModel<T extends IJSObject, D extends MutablePropertyDTO> extends AbstractPropertyModel<T, D> {	
	public AbstractMutablePropertyModel(D model, String name, EntityModel entity) {
		super (model, name, entity);
	}
	
	public boolean isRequired() {
		return this.getDTO().required;
	};

	@Override
	public Integer getAsNameIndex() {
		return this.getDTO().asNameIndex;
	}
	
	public boolean isReadonly() {
		return this.getDTO().readonly;
	}
	
	@Override
	public String getEnabledCondition() {
		return this.getDTO().enabledWhen;
	}
	
	@Override
	public boolean isReadonly(boolean isNewObject) {
		if (this.isRequired() && isNewObject) {
			return false;
		}
		return this.isReadonly();
	}
}
