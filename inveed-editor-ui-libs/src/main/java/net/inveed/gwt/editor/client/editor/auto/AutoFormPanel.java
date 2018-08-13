package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.constants.FormType;

import com.google.gwt.user.client.ui.Composite;

public abstract class AutoFormPanel extends Composite {
	private final List<AutoFormSection> sections;
	private Form form;
	
	public AutoFormPanel() {
		this.sections = new ArrayList<>();
		this.form = new Form();
		this.form.setType(FormType.HORIZONTAL);
		
		this.initWidget();
	}
	
	public void addSection(AutoFormSection section) {
		this.sections.add(section);
	}
	
	protected abstract void initWidget();
	
	protected Form getForm() {
		return this.form;
	}
	
	public void build() {
		Collections.sort(this.sections, new Comparator<AutoFormSection>() {

			@Override
			public int compare(AutoFormSection o1, AutoFormSection o2) {
				return Integer.compare(o1.getOrder(), o2.getOrder());
			}
		
		});

		for (int i = 0; i < this.sections.size(); i++) {
			AutoFormSection section = this.sections.get(i);
			if (i > 0) {
				section.setTitleEnabled(true);
			}
			section.build();
			this.form.add(section);
		}
	}
}
