package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.BooleanPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIBooleanProperty;

public class BooleanPropertyBuilder extends AbstractPropertyBuilder<UIBooleanProperty>{

	@Override
	public AbstractPropertyDTO build() {
		return new BooleanPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				(this.getAnnotation() == null ? null 
						: this.getAnnotation().defaultValue().toBoolean()));
	}

	@Override
	protected Class<UIBooleanProperty> getAnnotationType() {
		return UIBooleanProperty.class;
	}
	
	private String getEnabledWhen() {
		return this.getAnnotation() == null ? null : Utils.getNullOrValue(this.getAnnotation().enabledWhen());
	}

	@Override
	protected String getForcedName() {
		return this.getAnnotation() == null ? null : this.getAnnotation().name();
	}

	@Override
	protected boolean isRequiredAnnotation() {
		return this.getAnnotation() == null ? false : this.getAnnotation().required();
	}

	@Override
	protected Boolean isReadonlyAnnotation() {
		return this.getAnnotation() == null ? null : this.getAnnotation().readonly().toBoolean();
	}
}
