package net.inveed.gwt.editor.client.utils;

import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.options.DialogOptions;

public class ErrorWindow {	
	public static void open(String s) {
		DialogOptions options = DialogOptions.newOptions(s);
		if (s == null) {
			s = "Error";
		}
		options.addButton("OK", ButtonType.DANGER.getCssName());
		Bootbox.dialog(options);
	}
}
