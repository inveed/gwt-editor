package net.inveed.gwt.editor.client;


import java.util.HashMap;

import gwt.material.design.client.ui.MaterialLoader;

public class ProgressBarController {
	private final HashMap<Object, Boolean> loadingMap = new HashMap<>();
	
	public static final ProgressBarController INSTANCE = new ProgressBarController();
	
	private ProgressBarController() {}
	
	public void add(Object o) {
		this.loadingMap.put(o, true);
		this.showProgressBar();
	}
	
	public void remove(Object o) {
		this.loadingMap.remove(o);
		if (this.loadingMap.size() == 0) {
			this.hideProgressBar();
		}
	}
	
	private void showProgressBar() {
		MaterialLoader.loading(true);
	}
	
	private void hideProgressBar() {
		MaterialLoader.loading(false);
	}
}
