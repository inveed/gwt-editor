package net.inveed.gwt.server.annotations.properties;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface UISelectionFilter {
	String property();
	String value();
}
