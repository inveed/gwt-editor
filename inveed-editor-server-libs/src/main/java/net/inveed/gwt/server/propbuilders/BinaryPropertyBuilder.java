package net.inveed.gwt.server.propbuilders;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.BinaryPropertyDTO;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIBinaryKeyProperty;

public class BinaryPropertyBuilder extends AbstractPropertyBuilder<UIBinaryKeyProperty>{
	@Override
	public AbstractPropertyDTO build() {
		return new BinaryPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				(this.getAnnotation() == null ? null
						: Utils.getNullOrValue(this.getAnnotation().defaultValue())),
				(this.getAnnotation() == null ? null
						: Utils.getNullOrValue(this.getAnnotation().maxLength())),
				(this.getAnnotation() == null ? null
						: Utils.getNullOrValue(this.getAnnotation().minLength())),
				(this.getAnnotation() == null ? true  : this.getAnnotation().emptyAsNull()),
				(this.getAnnotation() == null ? false : this.getAnnotation().allowGeneration()));
	}

	@Override
	protected Class<UIBinaryKeyProperty> getAnnotationType() {
		return UIBinaryKeyProperty.class;
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
