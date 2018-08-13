package net.inveed.gwt.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface JsonRPCServiceRef {
	String value();
	
	String methodGet() default "get";
	String methodCreate() default "create";
	String methodUpdate() default "update";
	String methodList() default "list";
	String methodDelete() default "delete";
	
	String argData() default "data";
	String argID() default "id";
	String argPage() default "page";
	String argPageSize() default "pageSize";
}
