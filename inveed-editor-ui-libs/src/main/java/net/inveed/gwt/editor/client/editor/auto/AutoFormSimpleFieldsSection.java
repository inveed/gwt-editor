package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.gwtbootstrap3.client.ui.FormGroup;

import net.inveed.gwt.editor.shared.FormFieldLocation;

public class AutoFormSimpleFieldsSection extends AutoFormSection {
	private final List<AutoFormSimpleField> fields;
	
	private int order;
	
	public AutoFormSimpleFieldsSection(String name) {
		super(name);
		this.fields = new ArrayList<>();
		
		this.order = 0;
	}

	@Override
	public int getOrder() {
		return this.order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public void addField(AutoFormSimpleField fld) {
		this.fields.add(fld);
	}

	@Override
	public void build() {
		HashMap<Integer, ArrayList<AutoFormSimpleField>> fieldsByRow = new HashMap<>();
		int failedRows = 100000;
		for (AutoFormSimpleField fld : fields) {
			int rowNumber = fld.getPropertyInView().attr.order;
			ArrayList<AutoFormSimpleField> l = fieldsByRow.get(rowNumber);
			if (l == null) {
				l = new ArrayList<>();
				fieldsByRow.put(rowNumber, l);
			}
			if (l.size() > 1) {
				//В один ряд пытаемся запихнуть больше 2х элементов
				rowNumber = failedRows++;
				l = new ArrayList<>();
				fieldsByRow.put(rowNumber, l);
			}
			l.add(fld);
		}
		ArrayList<Integer> rowNumbers = new ArrayList<>(fieldsByRow.keySet());
		Collections.sort(rowNumbers);
		
		this.add(this.getLegend());
		
		for (Integer k : rowNumbers) {
			ArrayList<AutoFormSimpleField> r = fieldsByRow.get(k);
			if (r == null) {
				continue;
			}
			if (r.size() > 2) {
				//TODO: WTF??
				continue;
			} 
			FormGroup fg = new FormGroup();
			if (r.size() == 2) {
				Collections.sort(r, new Comparator<AutoFormField>() {
	
					@Override
					public int compare(AutoFormField o1, AutoFormField o2) {
						int ret = Integer.compare(o1.getPropertyInView().attr.order, o2.getPropertyInView().attr.order);
						if (ret == 0) {
							if (o1.getPropertyInView().attr.location == FormFieldLocation.LEFT && o2.getPropertyInView().attr.location == FormFieldLocation.RIGHT) {
								return -1;
							} else if (o2.getPropertyInView().attr.location == FormFieldLocation.LEFT && o1.getPropertyInView().attr.location == FormFieldLocation.RIGHT) {
								return 1;
							}
						}
						return o1.getPropertyInView().property.getName().compareTo(o2.getPropertyInView().property.getName());
					}
				});
				r.get(0).setFullWidth(false);
				r.get(1).setFullWidth(false);
				fg.add(r.get(0).getLabel());
				fg.add(r.get(0).getPanel());
				fg.add(r.get(1).getLabel());
				fg.add(r.get(1).getPanel());
			} else {
				r.get(0).setFullWidth(true);
				fg.add(r.get(0).getLabel());
				fg.add(r.get(0).getPanel());
			}
			this.add(fg);
		}
	}
}
