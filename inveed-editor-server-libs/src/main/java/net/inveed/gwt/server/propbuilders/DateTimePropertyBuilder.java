package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.DateTimePropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIDateTimeProperty;

public class DateTimePropertyBuilder extends AbstractPropertyBuilder<UIDateTimeProperty>{

	@Override
	public AbstractPropertyDTO build() {
		return new DateTimePropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				(this.getAnnotation().notAfterTimestampMs() == Long.MAX_VALUE ? null 
						: Utils.getNullOrValue(this.getAnnotation().notAfterTimestampMs())),
				(this.getAnnotation().notBeforeTimestampMs() == Long.MIN_VALUE ? null 
						: Utils.getNullOrValue(this.getAnnotation().notBeforeTimestampMs())),
				(this.getAnnotation().defaultValueMs() == Long.MIN_VALUE ? null 
						: Utils.getNullOrValue(this.getAnnotation().defaultValueMs())),
				(this.getAnnotation() == null ? UIConstants.FORMAT_TIMESTAMP_MILLS 
						: this.getAnnotation().format()),
				(this.getAnnotation() == null ? true 
						: this.getAnnotation().withTime()));
	}

	@Override
	protected Class<UIDateTimeProperty> getAnnotationType() {
		return UIDateTimeProperty.class;
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
