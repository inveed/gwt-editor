package net.inveed.gwt.editor.client.utils;

import gwt.material.design.client.ui.MaterialToast;

public class ErrorWindow {	
	public static void open(String s) {
		/*
		RootContainer.INSTANCE.modalContainer
		DialogOptions options = DialogOptions.newOptions(s);
		if (s == null) {
			s = "Error";
		}
		options.addButton("OK", ButtonType.DANGER.getCssName());
		Bootbox.dialog(options);
		*/
		MaterialToast.fireToast(s, 5 * 1000);
	}
}
