package net.inveed.gwt.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.shared.FormFieldLocation;
import net.inveed.gwt.editor.shared.UIConstants;

@Repeatable(UIFormViews.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UIFormView {
	String name() default UIConstants.ALL;
	int order() default 0;
	String section() default "";
	FormFieldLocation location() default FormFieldLocation.BOTH;
	boolean readonly() default false;
}
