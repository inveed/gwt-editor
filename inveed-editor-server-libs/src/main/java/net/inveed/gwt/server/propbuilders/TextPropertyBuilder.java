package net.inveed.gwt.server.propbuilders;

import javax.persistence.Column;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.TextPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UITextProperty;

public class TextPropertyBuilder extends AbstractPropertyBuilder<UITextProperty>{
	private static final int MAX_SINGLELINE_AUTO = 512;
	@Override
	public AbstractPropertyDTO build() {
		return new TextPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().defaultValue())),
				this.getMaxLength(),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().minLength())),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().regexp())),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().regexpError())),
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().startWith())),
				(this.getAnnotation() == null ? false : this.getAnnotation().password()),
				(this.getAnnotation() == null ? (this.getMaxLength() == null ? false : this.getMaxLength() > MAX_SINGLELINE_AUTO) : this.getAnnotation().multiline()),
				(this.getAnnotation() == null ? false : this.getAnnotation().emptyAsNull()),
				(this.getAnnotation() == null ? true : this.getAnnotation().trim())
				);
	}
	
	private Integer getMaxLength() {
		if (this.getAnnotation() != null) {
			if (this.getAnnotation().maxLength() != Integer.MAX_VALUE) {
				return this.getAnnotation().maxLength();
			}
		}
		Column ca = this.getProperty().getAnnotation(Column.class);
		if (ca != null) {
			return ca.length();
		}
		return null;
	}
	
	@Override
	protected boolean canBeEmpty() {
		if (this.getAnnotation() == null) {
			return true;
		} else {
			return !this.getAnnotation().emptyAsNull();
		}
	}

	@Override
	protected Class<UITextProperty> getAnnotationType() {
		return UITextProperty.class;
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
