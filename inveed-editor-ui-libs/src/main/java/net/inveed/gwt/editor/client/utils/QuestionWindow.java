package net.inveed.gwt.editor.client.utils;

import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.extras.bootbox.client.Bootbox;
import org.gwtbootstrap3.extras.bootbox.client.callback.SimpleCallback;
import org.gwtbootstrap3.extras.bootbox.client.options.DialogOptions;

public class QuestionWindow {
	private QuestionWindow() {}
	public static Promise<Boolean, IError> open(String s) {
		PromiseImpl<Boolean, IError> ret = new PromiseImpl<>();
		DialogOptions options = DialogOptions.newOptions(s);
		if (s == null) {
			s = "Error";
		}
		options.addButton("Yes", ButtonType.INFO.getCssName(), new SimpleCallback() {
			@Override
			public void callback() {
				ret.complete(true);
			}
		});
		options.addButton("No", ButtonType.INFO.getCssName(), new SimpleCallback() {
			@Override
			public void callback() {
				ret.complete(false);
			}
		});
		Bootbox.dialog(options);
	
		return ret;
	}
}
