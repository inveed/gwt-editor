package net.inveed.gwt.server.propbuilders;

import javax.persistence.OneToMany;

import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.editor.shared.properties.EntityListPropertyDTO;
import net.inveed.gwt.server.EntityModelBuilder;
import net.inveed.gwt.server.annotations.properties.UILinkedListProperty;

public class EntityListPropertyBuilder extends AbstractPropertyBuilder<UILinkedListProperty> {

	@Override
	public AbstractPropertyDTO build() {
		Class<?> targetEntity = this.getAnnotation() == null ? void.class : this.getAnnotation().targetEntity();
		OneToMany otma = this.getProperty().getAnnotation(OneToMany.class);
		
		if (targetEntity == void.class) {
			if (otma == null) {
				return null;
			}
			targetEntity = otma.targetEntity();
		}
		if (targetEntity == void.class) {
			return null;
		}
		String refEntityName = EntityModelBuilder.getEntityName(targetEntity);
		
		String mappedBy = this.getAnnotation() == null ? "" : this.getAnnotation().mappedBy();
		if (mappedBy.length() == 0 ) {
			if (otma == null) {
				return null;
			}
			mappedBy = otma.mappedBy();
		}
		if (mappedBy.length() == 0) {
			return null;
		}
		return new EntityListPropertyDTO(refEntityName, mappedBy);
	}

	@Override
	protected Class<UILinkedListProperty> getAnnotationType() {
		return UILinkedListProperty.class;
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
		return false;
	}
}
