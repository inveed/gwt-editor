package net.inveed.gwt.editor.client.controls;

import com.google.gwt.dom.client.Document;
import com.google.gwt.text.client.DoubleParser;
import com.google.gwt.text.client.DoubleRenderer;

public class SimpleDoubleBox extends SimpleNumberBox<Double> {

	public SimpleDoubleBox() {
		super(Document.get().createTextInputElement(), DoubleRenderer.instance(),
		        DoubleParser.instance());
	}
}
