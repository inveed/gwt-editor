package net.inveed.gwt.server.annotations.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.TriBool;
import net.inveed.gwt.server.annotations.UIPropertyAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@UIPropertyAnnotation(implementor="net.inveed.gwt.server.propbuilders.BooleanPropertyBuilder")
public @interface UIBooleanProperty {
	/**
	 * Property name.
	 * 
	 * Will be detected from field name/json properties if not specified.
	 */
	String name() default "";
	
	/**
	 * Value should be set
	 */
	boolean required() default false;
	
	/**
	 * Property cannot be changed
	 * 
	 * This will be ignored if property is required 
	 * and the new object is creating
	 */
	TriBool readonly() default TriBool.UNDEF;
	
	
	/**
	 * Condition when property will be enabled.
	 * If no value set property will be enabled always.
	 * When property is disabled "required" will be ignored.
	 */
	String enabledWhen() default "";
	
	
	/**
	 * Default value
	 * @return
	 */
	TriBool defaultValue() default TriBool.UNDEF;
	
}
