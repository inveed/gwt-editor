package net.inveed.gwt.editor.client;

import org.gwtbootstrap3.client.ui.html.Div;

public class RootContainer {
	public static final RootContainer INSTANCE = new RootContainer();
	public final Div modalContainer;
	
	public RootContainer() {
		this.modalContainer = new Div();
	}
}