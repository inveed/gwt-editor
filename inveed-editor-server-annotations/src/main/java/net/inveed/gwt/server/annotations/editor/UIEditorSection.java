package net.inveed.gwt.server.annotations.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Inherited
@Repeatable(UIEditorSections.class)
public @interface UIEditorSection {
	String parent() default "";
	
	String name();
	int order() default 0;
	boolean showTitle() default false;
}
