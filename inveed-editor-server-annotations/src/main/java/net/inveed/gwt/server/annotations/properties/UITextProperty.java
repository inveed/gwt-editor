package net.inveed.gwt.server.annotations.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.TriBool;
import net.inveed.gwt.server.annotations.UIPropertyAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@UIPropertyAnnotation(implementor="net.inveed.gwt.server.propbuilders.TextPropertyBuilder")
public @interface UITextProperty {
	public static final String IPv4_REGEXP = "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public static final String IPv6_REGEXP = "(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";
	public static final String HOSTNAME_REGEX = "(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])";
	public static final String IP_OR_HOST_REGEX = "(" + IPv4_REGEXP + ")|(" + IPv6_REGEXP + ")|(" + HOSTNAME_REGEX + ")";
	public static final String PORTNUM_REGEX = "[0-9]{1,4}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]";
	
	public static final String HTTP_URL_REGEX = "https?://(" + IP_OR_HOST_REGEX + ")(:(" + PORTNUM_REGEX + "))?((\\/([~0-9a-zA-Z\\#\\+\\%@\\.\\/_-]+))?(\\?[0-9a-zA-Z\\+\\%@\\/&\\[\\];=_-]+)?)";
	public static final String HTTP_URL_REGEX_ERR = "invalidUrl";
	
	public static final String ALPHANUM_REGEX = "[a-zA-Z][0-9a-zA-Z_\\-]*";
	public static final String ALPHANUM_REGEX_ERR = "onlyAlphaNum";
	
	
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
	 * Maximum allowed string length
	 * @return
	 */
	int maxLength() default Integer.MAX_VALUE;
	
	/**
	 * Minimum allowed string length
	 * @return
	 */
	int minLength() default Integer.MIN_VALUE;
	
	/**
	 * Default value
	 * @return
	 */
	String defaultValue() default "";
	
	/**
	 * Regexp validation string
	 */
	String regexp() default "";
	
	/**
	 * Regexp validation string
	 */
	String regexpError() default "";
	
	/**
	 * Value should start with specified string.
	 * 
	 * Property will be ignored when REGEXP property is set.
	 */
	String startWith() default "";
	
	/**
	 * Text field will be password-hidden
	 * @return
	 */
	boolean password() default false;
	
	boolean multiline() default false;
	
	boolean emptyAsNull() default true;
	
	boolean trim() default true;
}
