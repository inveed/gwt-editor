package net.inveed.gwt.editor.client.editor;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import gwt.material.design.client.base.AbstractValueWidget;
import gwt.material.design.client.base.HasTitle;
import gwt.material.design.client.constants.HeadingSize;
import gwt.material.design.client.ui.html.Heading;
import gwt.material.design.client.ui.html.Paragraph;

public class EditorTitle extends AbstractValueWidget<String> implements HasTitle {

    private Heading header = new Heading(HeadingSize.H5);
    private Paragraph paragraph = new Paragraph();

    public EditorTitle(String title, String description) {
        this();
        setTitle(title);
        setDescription(description);
    }

    public EditorTitle(String title) {
        this();
        setTitle(title);
    }

    public EditorTitle() {
        super(DOM.createDiv());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        header.setFontWeight(300);
        add(header);
        add(paragraph);
    }

    @Override
    public void setDescription(String description) {
        paragraph.setText(description);
    }

    @Override
    public String getDescription() {
        return paragraph.getText();
    }

    @Override
    public void setTitle(String title) {
        setValue(title, true);
    }

    @Override
    public String getTitle() {
        return getValue();
    }

    public Heading getHeader() {
        return header;
    }

    public Paragraph getParagraph() {
        return paragraph;
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        header.getElement().setInnerSafeHtml(SafeHtmlUtils.fromString(value));
        super.setValue(value, fireEvents);
    }

    @Override
    public String getValue() {
        return SafeHtmlUtils.fromString(header.getElement().getInnerHTML()).asString();
    }
}