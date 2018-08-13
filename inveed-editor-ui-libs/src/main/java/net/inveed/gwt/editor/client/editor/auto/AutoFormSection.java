package net.inveed.gwt.editor.client.editor.auto;

import org.gwtbootstrap3.client.ui.FieldSet;
import org.gwtbootstrap3.client.ui.Legend;

public abstract class AutoFormSection extends FieldSet {
	private final String name;
	private Legend legend;
	
	public AutoFormSection(String name) {
		this.name = name;
		this.legend = new Legend();
	}
	
	public abstract int getOrder();
	
	public void setTitleEnabled(boolean v) {
		this.legend.setVisible(v);
	}
	
	protected Legend getLegend() {
		return this.legend;
	}
	
	public abstract void build();
	
	public String getName() {
		return this.name;
	}
}
