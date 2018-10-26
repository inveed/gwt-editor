package net.inveed.gwt.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.UIConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
@Repeatable(UIListViewFieldCollection.class)
public @interface UIListView {
	String name() default UIConstants.VIEWS_ALL;
	int order() default 100;
	int width() default 0;
}
