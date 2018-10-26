package net.inveed.gwt.server.annotations.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.TriBool;
import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.server.annotations.UIPropertyAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@UIPropertyAnnotation(implementor="net.inveed.gwt.server.propbuilders.DateTimePropertyBuilder")
public @interface UIDateTimeProperty {
	
	
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
	 * Maximum allowed value
	 * @return
	 */
	long notAfterTimestampMs() default Long.MAX_VALUE;
	
	/**
	 * Minimum allowed value
	 * @return
	 */
	long notBeforeTimestampMs() default Long.MIN_VALUE;
	
	/**
	 * Default value
	 * @return
	 */
	long defaultValueMs() default Long.MIN_VALUE;
	
	/**
	 * DateTime Format for stored value
	 * 
	 * FORMAT_TIMESTAMP_SECONDS and 
	 * FORMAT_TIMESTAMP_MILLS 
	 * will serialize value to JSON number.
	 * 
	 * Other formats will serialize to JSON String.
	 * 
	 */
	String format() default UIConstants.FORMAT_TIMESTAMP_MILLS;
	
	boolean withTime() default true;
	
}
