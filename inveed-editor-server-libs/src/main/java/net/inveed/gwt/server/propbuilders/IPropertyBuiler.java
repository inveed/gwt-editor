package net.inveed.gwt.server.propbuilders;

import java.lang.annotation.Annotation;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;

public interface IPropertyBuiler<T extends Annotation> {

	boolean prepare(BeanPropertyDesc prop);

	String getPropertyName();

	AbstractPropertyDTO build();
	
	BeanPropertyDesc getProperty();

}
