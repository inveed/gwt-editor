package net.inveed.gwt.editor.client.model;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.shared.ListViewAttributesDTO;

public class EntityListView {
	public static final class PropertyInView {
		public final IPropertyDesc<?> property;
		public final EntityListView view;
		public final int order;
		public final int width;
		
	
		private PropertyInView(IPropertyDesc<?> property, EntityListView view, ListViewAttributesDTO attr) {
			this.property = property;
			this.view = view;
			this.order = attr.order;
			this.width = attr.width;
		}
		public String getDisplayName() {
			return this.property.getDisplayName(this.view.getName());
		}
	}
	
	private final EntityModel model;
	private final String name;
	private List<PropertyInView> properties;
	
	public EntityListView(EntityModel model, String name) {
		this.model = model;
		this.name = name;
		this.init();
	}
	
	public List<PropertyInView> getProperties() {
		return this.properties;
	}
	
	private void init() {
		this.properties = new ArrayList<>();
		for (IPropertyDesc<?> p : this.model.getFields()) {
			ListViewAttributesDTO lvattr = p.isInListView(this.name);
			if (lvattr != null) {
				this.properties.add(new PropertyInView(p, this, lvattr));
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return this.model.getDisplayName(this.getName());
	}
	
	public String getPluralDisplayName() {
		return this.model.getPluralDisplayName(this.getName());
	}
}
