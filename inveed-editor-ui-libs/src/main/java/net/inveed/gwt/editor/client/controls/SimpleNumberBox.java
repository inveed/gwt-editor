package net.inveed.gwt.editor.client.controls;

import com.google.gwt.dom.client.Element;
import com.google.gwt.text.shared.Parser;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.ValueBoxBase;

public abstract class SimpleNumberBox<T> extends ValueBoxBase<T> {

	protected SimpleNumberBox(Element elem, Renderer<T> renderer, Parser<T> parser) {
		super(elem, renderer, parser);
		this.getElement().setAttribute("type", "number");
		this.setStyleName("form-control");
	}

	/**
     * Set step attribute to input element.
     *
     * @param step "any" or number like for example 1 or 2.5 or 100, etc...
     */
    public void setStep(String step) {
        this.getElement().setAttribute("step", step);
    }

    public String getStep() {
        return this.getElement().getAttribute("step");
    }

    public void setMin(String min) {
        this.getElement().setAttribute("min", min);
    }

    public String getMin() {
        return this.getElement().getAttribute("min");
    }

    public void setMax(String max) {
        this.getElement().setAttribute("max", max);
    }

    public String getMax() {
        return this.getElement().getAttribute("max");
    }
}
