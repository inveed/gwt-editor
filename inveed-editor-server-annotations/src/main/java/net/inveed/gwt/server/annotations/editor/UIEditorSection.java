package net.inveed.gwt.server.annotations.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.shared.UIConstants;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UIEditorSection {
	String name();
	String parent() default "";
	int order() default 0;
	String viewName() default UIConstants.ALL;
}
