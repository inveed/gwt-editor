package net.inveed.gwt.editor.client;

import gwt.material.design.client.ui.MaterialPanel;

public class RootContainer {
	public static final RootContainer INSTANCE = new RootContainer();
	public final MaterialPanel modalContainer;
	
	public RootContainer() {
		this.modalContainer = new MaterialPanel();
	}
}