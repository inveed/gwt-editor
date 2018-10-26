package net.inveed.gwt.server.annotations.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.FormFieldLocation;
import net.inveed.gwt.editor.commons.UIConstants;

@Repeatable(UIEditorsField.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UIEditorField {
	String forView() default UIConstants.VIEWS_ALL;
	String[] forViews() default {};
	
	String name() default "";
	int order() default 0;
	String container() default "";
	FormFieldLocation location() default FormFieldLocation.BOTH;
	boolean readonly() default false;
}
