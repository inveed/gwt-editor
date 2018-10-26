package net.inveed.gwt.server.editors;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.gwt.server.annotations.editor.UIEditorField;
import net.inveed.gwt.server.propbuilders.IPropertyBuiler;

public class FieldInView {
	public final String name;
	public final IPropertyBuiler<?> builder;
	public final BeanPropertyDesc property;
	public final UIEditorField annotation;
	public final AbstractPanelBuilder<?> container;
	
	public FieldInView(String name, IPropertyBuiler<?> builder, BeanPropertyDesc prop, UIEditorField annotation, AbstractPanelBuilder<?> container) {
		this.name = name;
		this.builder = builder;
		this.property = prop;
		this.annotation = annotation;
		this.container = container;
	}

}
