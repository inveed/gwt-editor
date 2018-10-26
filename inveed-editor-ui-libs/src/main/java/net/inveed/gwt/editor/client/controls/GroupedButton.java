package net.inveed.gwt.editor.client.controls;


import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialButton;

public class GroupedButton extends MaterialButton{
	public GroupedButton() {
		super();
		this.setHeight("34px");
		this.setMargin(1);
		this.setBackgroundColor(Color.TRANSPARENT);
		this.setPaddingLeft(5);
		this.setPaddingRight(5);
	}
}
