package net.inveed.gwt.editor.client.controls;

import com.google.gwt.dom.client.Document;
import com.google.gwt.text.client.LongParser;
import com.google.gwt.text.client.LongRenderer;

public class SimpleLongBox extends SimpleNumberBox<Long> {

	public SimpleLongBox() {
		super(Document.get().createTextInputElement(), LongRenderer.instance(),
		        LongParser.instance());
	}
}
