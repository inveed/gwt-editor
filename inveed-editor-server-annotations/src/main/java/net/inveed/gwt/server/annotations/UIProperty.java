package net.inveed.gwt.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.shared.FieldType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UIProperty {
	enum TriBool {
		UNDEF,
		TRUE,
		FALSE
	}
	FieldType type() default FieldType.AUTO;
	String mappedBy() default "";
	boolean required() default false;
	String defaultValue() default "";
	TriBool readonly() default TriBool.UNDEF;
	String enabledWhen() default "";
}
