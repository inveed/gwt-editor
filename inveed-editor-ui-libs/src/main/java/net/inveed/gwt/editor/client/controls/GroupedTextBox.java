package net.inveed.gwt.editor.client.controls;

import com.google.gwt.user.client.ui.TextBox;

public class GroupedTextBox extends TextBox {
	public GroupedTextBox() {
		super();
		this.setStyleName("form-control");
	}
	
	public void setType(String type) {
		this.getElement().setAttribute("type", type);
	}
}
