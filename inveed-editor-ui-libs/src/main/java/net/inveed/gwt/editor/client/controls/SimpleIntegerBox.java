package net.inveed.gwt.editor.client.controls;

import com.google.gwt.dom.client.Document;
import com.google.gwt.text.client.IntegerParser;
import com.google.gwt.text.client.IntegerRenderer;

public class SimpleIntegerBox extends SimpleNumberBox<Integer> {

	public SimpleIntegerBox() {
		super(Document.get().createTextInputElement(), IntegerRenderer.instance(),
		        IntegerParser.instance());
	}
}
