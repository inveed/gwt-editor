package net.inveed.gwt.editor.client.editor.auto;

import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialRow;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;

public class AutoFormTab extends AbstractAutoFormContainer<EditorSectionDTO> {
	private MaterialColumn self;
	private MaterialRow row;
	
	public AutoFormTab(EditorSectionDTO dto, EntityModel model, AutoFormTabPanel container) {
		super(dto, model, container);
	}

	public MaterialWidget getColumn() {
		if (this.self == null) {
			this.self = new MaterialColumn();
			this.self.setShadow(0);
			this.self.setGrid("s12");
		}
		return this.self;
	}
	
	@Override
	protected void addToParent2(Widget w) {
	}

	@Override
	public MaterialWidget getWidget() {
		if (this.row == null) {
			this.row = new MaterialRow();
			this.row.setPaddingTop(20);
			this.getColumn().add(this.row);
		}
		return this.row;
	}
}
