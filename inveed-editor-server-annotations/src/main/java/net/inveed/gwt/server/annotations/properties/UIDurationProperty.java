package net.inveed.gwt.server.annotations.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.DurationPrecision;
import net.inveed.gwt.editor.commons.TriBool;
import net.inveed.gwt.server.annotations.UIPropertyAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@UIPropertyAnnotation(implementor="net.inveed.gwt.server.propbuilders.DurationPropertyBuilder")
public @interface UIDurationProperty {
	public static enum Format {
		/**
		 * Value will be parsed and serialized as ISO
		 */
		ISO,
		
		/** 
		 * Value will be parsed and serialized as a number of seconds
		 */
		NUMBER_SECONDS,
		
		/** 
		 * Value will be parsed and serialized as a number of milliseconds
		 */
		NUMBER_MSEC
	}
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
	 * 
	 * When maxItem == YEAR or MONTH value can contain years and months,
	 * in other case it can contain only days/hors/minutes/seconds
	 * @return
	 */
	String notLongerISO() default "";
	
	/**
	 * Minimum allowed value
	 * 
	 * When maxItem == YEAR or MONTH value can contain years and months,
	 * in other case it can contain only days/hors/minutes/seconds
	 */
	String notShorterISO() default "";
	
	/**
	 * Default value
	 * 
	 * When maxItem == YEAR or MONTH value can contain years and months,
	 * in other case it can contain only days/hors/minutes/seconds
	 */
	String defaultValueISO() default "";
	
	DurationPrecision precision() default DurationPrecision.SECOND;
	
	DurationPrecision maxItem()   default DurationPrecision.DAY;
	
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
	Format format() default Format.NUMBER_SECONDS;
}
