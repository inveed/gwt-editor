package net.inveed.gwt.editor.client.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.shared.lists.ListViewDTO;
import net.inveed.gwt.editor.shared.lists.PropertyInListViewDTO;

public class EntityListView {
	public static final class ListViewColumn<T extends IPropertyDescriptor<?>> {
		private final PropertyInListViewDTO dto;
		private final T property;
		private final String vname;
		
		public ListViewColumn(PropertyInListViewDTO dto, T property, String viewName) {
			this.dto = dto;
			this.property = property;
			this.vname = viewName;
		}
		
		public PropertyInListViewDTO getDto() {
			return this.dto;
		}
		public T getPropertyDescriptor() {
			return this.property;
		}

		public String getDisplayName() {
			return this.getPropertyDescriptor().getDisplayName(vname);
		}
	}
	
	private final EntityModel entityModel;
	private final ListViewDTO dto;
	private final String name;
	private List<ListViewColumn<?>> columns;
	
	
	public EntityListView(EntityModel entityModel, ListViewDTO dto, String name) {
		this.entityModel = entityModel;
		this.dto = dto;
		this.name = name;
		this.init();
	}
	
	public List<ListViewColumn<?>> getColumns() {
		return this.columns;
	}
	
	private boolean init() {
		List<ListViewColumn<?>> clist = new ArrayList<>();
		for (PropertyInListViewDTO d : this.dto.columns) {
			IPropertyDescriptor<?> pd = this.entityModel.getPropertyDescriptor(d.property);
			if (pd == null) {
				return false;
			}
			@SuppressWarnings({ "rawtypes", "unchecked" })
			ListViewColumn<?> col = new ListViewColumn(d, pd, this.name);
			clist.add(col);
		}
		this.columns = Collections.unmodifiableList(clist);
		return true;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return this.entityModel.getDisplayName(this.getName());
	}
	
	public String getPluralDisplayName() {
		return this.entityModel.getPluralDisplayName(this.getName());
	}
}
