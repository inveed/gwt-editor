package net.inveed.gwt.server.propbuilders;

import java.util.HashMap;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.ObjectRefPropertyDTO;
import net.inveed.gwt.server.EntityModelBuilder;
import net.inveed.gwt.server.Utils;
import net.inveed.gwt.server.annotations.properties.UIObjectRefProperty;
import net.inveed.gwt.server.annotations.properties.UISelectionFilter;

public class ObjectRefPropertyBuilder extends AbstractPropertyBuilder<UIObjectRefProperty>{

	@Override
	public AbstractPropertyDTO build() {
		HashMap<String, String> filters = new HashMap<>();
		if (this.getAnnotation() != null) {
			for (int i = 0; i < this.getAnnotation().filters().length; i ++) {
				UISelectionFilter fa = this.getAnnotation().filters()[i];
				filters.put(fa.property(), fa.value());
			}
		}
		
		String refEntityName = EntityModelBuilder.getEntityName(this.getProperty().getType());
		String notsettext = null;
		if (this.getAnnotation() != null && !this.isRequired()) {
			if (this.getAnnotation().notSetText().length() > 0) {
				notsettext = this.getAnnotation().notSetText();
			}
		}
		return new ObjectRefPropertyDTO(
				this.getAsNameIndex(),
				this.isRequired(),
				this.isReadonly(),
				this.getEnabledWhen(),
				refEntityName,
				(this.getAnnotation() == null ? null 
						: Utils.getNullOrValue(this.getAnnotation().defaultValueCode())),
				filters.size() == 0 ? null : filters,
				notsettext);
	}

	@Override
	protected Class<UIObjectRefProperty> getAnnotationType() {
		return UIObjectRefProperty.class;
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
