package net.inveed.gwt.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UIRangeValidator {
	double min() default Double.MAX_VALUE;
	double max() default Double.MIN_VALUE;
}
