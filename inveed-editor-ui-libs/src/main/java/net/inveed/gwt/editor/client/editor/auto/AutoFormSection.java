package net.inveed.gwt.editor.client.editor.auto;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.MaterialRow;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;

public class AutoFormSection extends AbstractAutoFormContainer<EditorSectionDTO> {
	private MaterialRow self;
	
	public AutoFormSection(EditorSectionDTO dto, EntityModel model, AbstractAutoFormContainer<?> container) {
		super(dto, model, container);
	}

	@Override
	public MaterialWidget getWidget() {
		if (this.self == null) {
			this.self = new MaterialRow();
		}
		return this.self;
	}
}
