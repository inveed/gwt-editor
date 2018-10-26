package net.inveed.gwt.editor.client.utils;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface Promise<T, E extends IError> {
	public Promise<T, E> thenApply(Function<? super T, ?> fn);
	public Promise<T, E> onError(BiFunction<? super E, ? super Throwable, ?> fn);

	
}
