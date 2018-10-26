package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.commons.DurationFormat;
import net.inveed.gwt.editor.commons.DurationPrecision;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.DurationPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIDurationProperty;

public class DurationPropertyBuilder extends AbstractPropertyBuilder<UIDurationProperty>{

	@Override
	public AbstractPropertyDTO build() {
		DurationFormat format = DurationFormat.INTEGER_SECONDS;
		if (this.getAnnotation() != null) {
			boolean floating = false;
			Class<?> type = this.getProperty().getType().getType();
			if (type == float.class || type == double.class || type == Float.class || type == Double.class) {
				floating = true;
			}
			switch (this.getAnnotation().format()) {
			case ISO:
				format = DurationFormat.ISO;
				break;
			case NUMBER_SECONDS:
				format = floating ? DurationFormat.FLOAT_SECONDS : DurationFormat.INTEGER_SECONDS;
				break;
			case NUMBER_MSEC:
				format = floating ? DurationFormat.FLOAT_MSEC : DurationFormat.INTEGER_MSEC;
				break;
			default:
				//TODO: LOG
				return null;
			}
		}
		return new DurationPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().notLongerISO())),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().notShorterISO())),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().defaultValueISO())),
				(this.getAnnotation() == null ? DurationPrecision.SECOND 
						: this.getAnnotation().precision()), 
				(this.getAnnotation() == null ? DurationPrecision.DAY
						: this.getAnnotation().maxItem()),
				format
				);
	}

	@Override
	protected Class<UIDurationProperty> getAnnotationType() {
		return UIDurationProperty.class;
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
