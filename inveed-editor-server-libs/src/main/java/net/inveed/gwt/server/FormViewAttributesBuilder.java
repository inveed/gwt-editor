package net.inveed.gwt.server;

import net.inveed.gwt.editor.shared.FormFieldLocation;
import net.inveed.gwt.editor.shared.FormViewAttributesDTO;
import net.inveed.gwt.server.annotations.UIFormView;

public class FormViewAttributesBuilder {
	public String tabName;
	public int order;
	public FormFieldLocation location;
	public boolean readonly;
	
	public FormViewAttributesBuilder(UIFormView a) {
		this.location = a.location();
		this.readonly = a.readonly();
		this.order = a.order();
		this.tabName = a.section();
	}
	
	public FormViewAttributesDTO build() {
		return new FormViewAttributesDTO(tabName, location, order, readonly);
	}
}
