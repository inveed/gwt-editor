package net.inveed.gwt.server;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import net.inveed.gwt.editor.shared.lists.ListViewDTO;
import net.inveed.gwt.editor.shared.lists.PropertyInListViewDTO;
import net.inveed.gwt.server.annotations.UIListView;
import net.inveed.gwt.server.propbuilders.IPropertyBuiler;

public class EntityListViewBuilder {
	private static final class ListViewProperty {
		private IPropertyBuiler<?> pb;
		private UIListView v;
		
		public ListViewProperty(IPropertyBuiler<?> pb, UIListView v) {
			this.pb = pb;
			this.v = v;
		}
	}
	
	private final HashMap<String, ListViewProperty> properties = new HashMap<>();
	public ListViewDTO build() {
		ArrayList<ListViewProperty> list = new ArrayList<>(properties.values());
		list.sort(new Comparator<ListViewProperty>() {

			@Override
			public int compare(ListViewProperty o1, ListViewProperty o2) {
				int ret = Integer.compare(o1.v.order(), o2.v.order());
				if (ret != 0) {
					return ret;
				}
				return o1.pb.getPropertyName().compareTo(o2.pb.getPropertyName());
			}
		});
		
		PropertyInListViewDTO[] ret = new PropertyInListViewDTO[list.size()];
		for (int i = 0 ; i < ret.length; i++) {
			ListViewProperty lvp = list.get(i);
			ret[i] = new PropertyInListViewDTO(lvp.v.order(), lvp.v.width() == 0 ? null : lvp.v.width(), lvp.pb.getPropertyName());
		}
		return new ListViewDTO(ret);
	}
	
	public void registerProperty(IPropertyBuiler<?> pb, UIListView v) {
		String pname = pb.getPropertyName();
		if (pname == null) {
			return;//TODO: LOG
		}
		pname = pname.trim();
		if (pname.length() < 1) {
			return;//TODO: LOG
		}
		this.properties.put(pname, new ListViewProperty(pb, v));
	}

}
