package net.inveed.gwt.editor.client.controls;


import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.CssName;

import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.html.Div;
import gwt.material.design.client.ui.html.Label;

public class InputGroup extends Composite {
	private Div root;
	private Div dialogRow;
	private Label label = new Label();
    private MaterialLabel errorLabel = new MaterialLabel();
    
	public InputGroup() {
		this.root = new Div();
		this.root.addStyleName("input-group-root");
		this.root.addStyleName("outlined");
		this.root.addStyleName("input-field");
		
		this.initWidget(this.root);
		this.dialogRow = new Div();
		this.dialogRow.addStyleName("input-group");
		this.root.add(this.dialogRow);
	}
	
	public InputGroup(Widget ... widgets) {
		this();
		for (Widget w : widgets) {
			this.add(w);
		}
	}
	
	public Div getGroupWidget() {
		return this.dialogRow;
	}
	public void add(Widget w) {
		this.dialogRow.add(w);
	}

	public void setGrid(String grid) {
		this.root.setGrid(grid);
	}
	   
    @Override
    protected void onLoad() {
        super.onLoad();

        String id = DOM.createUniqueId();
        this.dialogRow.getElement().setId(id);
        this.label.getElement().setAttribute("for", id);
        this.label.addStyleName(CssName.ACTIVE);
        this.root.add(this.label);
    }
    
    public void removeErrorModifiers() {
        this.dialogRow.getElement().removeClassName(CssName.VALID);
        this.dialogRow.getElement().removeClassName(CssName.INVALID);
    }

    /**
     * Set the label of this field.
     * <p>
     * This will be displayed above the field when values are
     * assigned to the box, otherwise the value is displayed
     * inside the box.
     * </p>
     */
    public void setLabel(String label) {
        this.label.setText(label);
    }

    /*
    public void setFocus(final boolean focused) {
        Scheduler.get().scheduleDeferred(() -> {
            valueBoxBase.setFocus(focused);
            if (focused) {
                label.addStyleName(CssName.ACTIVE);
            } else {
                updateLabelActiveStyle();
            }
        });
    }*/

    
    public Label getLabel() {
        return label;
    }

    public MaterialLabel getErrorLabel() {
        return errorLabel;
    }
}
