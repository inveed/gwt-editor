package net.inveed.gwt.server;

import net.inveed.gwt.editor.shared.ListViewAttributesDTO;
import net.inveed.gwt.server.annotations.UIListView;

public class ListViewAttributesBuilder {
	public int order;
	public int width;
	
	public ListViewAttributesBuilder(UIListView a) {
		this.order = a.order();
		this.width = a.width();
	}
	public ListViewAttributesDTO build() {
		return new ListViewAttributesDTO(order, width);
	}
}
