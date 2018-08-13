package net.inveed.gwt.server.annotations.editor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.inveed.gwt.editor.shared.UIConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Repeatable(UIEditors.class)
public @interface UIEditor {
	String viewName() default UIConstants.ALL;
	int width() default 0;
	int heigh() default 0;
	
	UIEditorPanel[] tabContainers() default {};
	UIEditorSection[] containers() default {};
}
