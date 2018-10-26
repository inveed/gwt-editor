package net.inveed.gwt.server.annotations.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.commons.UIConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Repeatable(UIAutoEditorViews.class)
public @interface UIAutoEditorView {
	String viewName();
	String inheritFrom() default UIConstants.VIEWS_ALL;
	int width() default 0;
	int heigh() default 0;
}
