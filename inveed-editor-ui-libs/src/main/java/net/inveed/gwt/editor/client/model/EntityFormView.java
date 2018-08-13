package net.inveed.gwt.editor.client.model;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.shared.EntityEditorDTO;
import net.inveed.gwt.editor.shared.FormViewAttributesDTO;
import net.inveed.gwt.editor.shared.UIConstants;

public class EntityFormView {
	public static final class PropertyInView {
		public final IPropertyDesc<?> property;
		public final EntityFormView view;
		public final FormViewAttributesDTO attr;

		private PropertyInView(IPropertyDesc<?> property, EntityFormView view, FormViewAttributesDTO attr) {
			this.property = property;
			this.view = view;
			this.attr = attr;
		}
		
		public String getDisplayName() {
			return this.property.getDisplayName(this.view.getName());
		}
	}
	
	private final EntityModel model;
	private final String name;
	
	private List<PropertyInView> properties;
	
	public final EntityEditorDTO editorDTO;

	public EntityModel getModel() {
		return this.model;
	}
	
	public EntityFormView(EntityModel model, String name) {
		this.model = model;
		this.name = name;
		if (model.getEditorsDTO() != null) {
			EntityEditorDTO eedto = model.getEditorsDTO().views.get(this.name);
			EntityEditorDTO eedtoAll = model.getEditorsDTO().views.get(UIConstants.ALL);
			
			if (eedto == null && eedtoAll != null) {
				eedto = eedtoAll;
			} else if (eedto != null && eedtoAll != null) {
				eedto = eedto.merge(eedtoAll);
			}
			this.editorDTO = eedto;
		} else {
			this.editorDTO = null;
		}
		this.init();
	}
	
	public List<PropertyInView> getProperties() {
		return this.properties;
	}
	
	private void init() {
		this.properties = new ArrayList<>();
		for (IPropertyDesc<?> p : this.model.getFields()) {
			FormViewAttributesDTO attr = p.isInFormView(this.name);
			if (attr != null) {
				this.properties.add(new PropertyInView(p, this, attr));
			}
		}
	}

	public String getDisplayName() {
		return this.model.getDisplayName(this.getName());
	}
	
	public String getPluralDisplayName() {
		return this.model.getPluralDisplayName(this.getName());
	}

	public String getName() {
		return this.name;
	}
}
