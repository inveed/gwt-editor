package net.inveed.gwt.editor.client.editor.auto;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.MaterialRow;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;

public class AutoFormRoot extends AbstractAutoFormContainer<AutoFormViewDTO> {
	private MaterialRow self;
	public AutoFormRoot(AutoFormViewDTO dto, EntityModel model) {
		super(dto, model, null);
	}

	@Override
	public MaterialWidget getWidget() {
		if (this.self == null) {
			this.self = new MaterialRow();
		}
		return this.self;
	}
}
