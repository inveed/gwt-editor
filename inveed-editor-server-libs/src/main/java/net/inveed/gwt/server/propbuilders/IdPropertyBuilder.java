package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.IntegerIdPropertyDTO;
import net.inveed.gwt.editor.shared.properties.StringIdPropertyDTO;
import net.inveed.gwt.server.annotations.properties.UIIdProperty;

public class IdPropertyBuilder extends AbstractPropertyBuilder<UIIdProperty>{

	@Override
	public AbstractPropertyDTO build() {
		Class<?> ptype = this.getProperty().getType().getType();
		if (ptype == int.class 
				|| ptype == Integer.class 
				|| ptype == long.class 
				|| ptype == Long.class
				|| ptype == short.class 
				|| ptype == Short.class 
				|| ptype == byte.class 
				|| ptype == Byte.class) {
			return new IntegerIdPropertyDTO(this.getAsNameIndex());
		} else {
			return new StringIdPropertyDTO(this.getAsNameIndex());
		}
	}

	@Override
	protected Class<UIIdProperty> getAnnotationType() {
		return UIIdProperty.class;
	}

	@Override
	protected String getForcedName() {
		return this.getAnnotation() == null ? null : this.getAnnotation().name();
	}

	@Override
	protected boolean isRequiredAnnotation() {
		return false;
	}

	@Override
	protected Boolean isReadonlyAnnotation() {
		return true;
	}
}
