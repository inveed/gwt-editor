package net.inveed.gwt.server.propbuilders;

import net.inveed.commons.reflection.EnumTypeDesc;
import net.inveed.commons.reflection.JavaTypeDesc;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.EnumPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIEnumProperty;

public class EnumPropertyBuilder extends AbstractPropertyBuilder<UIEnumProperty>{

	@Override
	public AbstractPropertyDTO build() {
		JavaTypeDesc<?> td = this.getProperty().getType();
		String name;
		if (td instanceof EnumTypeDesc) {
			EnumTypeDesc<?> etd = (EnumTypeDesc<?>) td;
			name = etd.getName();
		} else {
			//TODO: WARN
			name = td.getType().getName();
		}
		String notsettext = null;
		if (this.getAnnotation() != null && !this.isRequired()) {
			if (this.getAnnotation().notSetText().length() > 0) {
				notsettext = this.getAnnotation().notSetText();
			}
		}
		return new EnumPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				name,
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().defaultValueCode())),
				notsettext);
	}

	@Override
	protected Class<UIEnumProperty> getAnnotationType() {
		return UIEnumProperty.class;
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
