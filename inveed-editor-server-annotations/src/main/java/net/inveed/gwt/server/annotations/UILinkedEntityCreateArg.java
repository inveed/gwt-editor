package net.inveed.gwt.server.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UILinkedEntityCreateArg {
	
	/**
	 * Class of the linked object.
	 * @return
	 */
	Class<?> referencedEntity();
	
	/** 
	 * Property of the current object to be passed in "create" call with "createArg"
	 * @return
	 */
	String property() default "";
	
	/**
	 * Argument of the "create" call with linked object's ID.
	 * @return
	 */
	String createArg();
}
