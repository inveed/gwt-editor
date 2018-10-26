package net.inveed.gwt.editor.client.utils;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.addins.client.overlay.MaterialOverlay;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;
import net.inveed.gwt.editor.client.RootContainer;

public class QuestionWindow {
	interface QuestionWindowBinder extends UiBinder<Widget, QuestionWindow> {
	}

	private static final QuestionWindowBinder uiBinder = GWT.create(QuestionWindowBinder.class);
	
	@UiField MaterialOverlay overlay;
	@UiField MaterialLabel lblTitle;
	@UiField MaterialLabel lblText;
	@UiField MaterialButton btnOK;
	@UiField MaterialButton btnCancel;
	
	public QuestionWindow() {
		uiBinder.createAndBindUi(this);
	}
	public static Promise<Boolean, IError> open(String title, String s, Color color) {
		
		PromiseImpl<Boolean, IError> ret = new PromiseImpl<>();
		QuestionWindow w = new QuestionWindow();
		w.overlay.setBackgroundColor(color);
		w.overlay.setDuration(1000);
		if (title == null) {
			title = "Error";
		}
				
		w.lblTitle.setText(title);
		
		if (s != null) {
			w.lblText.setText(s);
		}
		
		
		w.btnOK.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				w.overlay.close();
				w.overlay.removeFromParent();
				ret.complete(true);
			}
		});
		w.btnCancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				w.overlay.close();
				w.overlay.removeFromParent();
				ret.complete(false);
			}
		});
		
		RootContainer.INSTANCE.modalContainer.add(w.overlay);
		w.overlay.open();
		w.overlay.getElement().getStyle().setZIndex(5000);
		return ret;
	}
}
