package net.inveed.gwt.editor.client.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PromiseImpl<T, E extends IError> implements Promise<T, E> {
	private List<Function<? super T, ?>> fnListForApply = new ArrayList<>();
	private List<BiFunction<? super E, ? super Throwable, ?>> fnListForError = new ArrayList<>();
	
	private T resultValue;
	private E errorValue;
	private Throwable error;
	private boolean resultSet;
	private boolean errorSet;

	public Promise<T, E> thenApply(Function<? super T, ?> fn) {
		if (this.fnListForApply.contains(fn)) {
			return this;
		}
		this.fnListForApply.add(fn);
		if (this.resultSet) {
			fn.apply(this.resultValue);
		}
		return this;
	}
	
	@Override
	public Promise<T, E> onError(BiFunction<? super E, ? super Throwable, ?> fn) {
		if (this.fnListForError.contains(fn)) {
			return this;
		}
		this.fnListForError.add(fn);
		if (this.errorSet ) {
			fn.apply(this.errorValue, this.error);
		}
		return this;
	}

	public void complete(T value) {
		this.resultSet = true;
		this.resultValue = value;
		this.completeDeferredPromise();
	}
	
	public void error(E value, Throwable t) {
		this.errorSet = true;
		this.error = t;
		this.errorValue = value;
		this.completeDeferredError();
	}
	
	private void completeDeferredError() {
		for (BiFunction<? super E, ? super Throwable, ?> fn : this.fnListForError) {
			fn.apply(this.errorValue, this.error);
		}
	}
	private void completeDeferredPromise() {
		for (Function<? super T, ?> fn : this.fnListForApply) {
			fn.apply(this.resultValue);
		}
	}
}
