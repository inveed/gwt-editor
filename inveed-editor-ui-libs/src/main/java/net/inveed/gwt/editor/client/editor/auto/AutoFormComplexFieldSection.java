package net.inveed.gwt.editor.client.editor.auto;

import org.gwtbootstrap3.client.ui.FieldSet;

public class AutoFormComplexFieldSection extends AutoFormSection {
	private AutoFormComplexField field;
	
	public AutoFormComplexFieldSection(AutoFormComplexField fld) {
		super(fld.getPropertyInView().property.getName());
		this.field = fld;
	}
	
	@Override
	public int getOrder() {
		return this.field.getOrder();
	}

	@Override
	public void build() {
		FieldSet ls = new FieldSet();
		ls.add(this.getLegend());
		ls.add(this.field.getEditor());
		this.add(ls);
	}

}
