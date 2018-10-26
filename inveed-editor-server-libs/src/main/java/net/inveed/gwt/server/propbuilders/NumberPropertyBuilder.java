package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.FloatPropertyDTO;
import net.inveed.gwt.editor.shared.properties.IntegerPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UINumberProperty;

public class NumberPropertyBuilder extends AbstractPropertyBuilder<UINumberProperty>{

	@Override
	public AbstractPropertyDTO build() {
		boolean floating = false;
		Class<?> type = this.getProperty().getType().getType();
		if (type == float.class || type == double.class || type == Float.class || type == Double.class) {
			floating = true;
		}
		
		if (floating) {
			return new FloatPropertyDTO(
					this.getAsNameIndex(),
					this.isRequired(),
					this.isReadonly(),
					this.getEnabledWhen(),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue(getAnnotation().defaultValue())),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue(getAnnotation().max())),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue(getAnnotation().min()))
					);
		} else {
			return new IntegerPropertyDTO(
					this.getAsNameIndex(),
					this.isRequired(),
					this.isReadonly(),
					this.getEnabledWhen(),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue((long) getAnnotation().defaultValue())),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue((long) getAnnotation().max())),
					(this.getAnnotation() == null ? null : Utils.getNullOrValue((long) getAnnotation().min()))
					);
		}
	}

	@Override
	protected Class<UINumberProperty> getAnnotationType() {
		return UINumberProperty.class;
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
