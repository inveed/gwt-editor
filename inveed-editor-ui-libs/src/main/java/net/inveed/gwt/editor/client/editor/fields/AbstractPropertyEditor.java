package net.inveed.gwt.editor.client.editor.fields;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.client.types.IJSObject;

public abstract class AbstractPropertyEditor<P extends IPropertyDesc<V>, V extends IJSObject> extends Composite {
	public static interface ValueChangeListener {
		void onValueChanged();
	}
	
	private static final Logger LOG = Logger.getLogger(AbstractFormPropertyEditor.class.getName());
	
	private P property;
	private boolean readonly = false;
	private String viewName;
	private JSEntity entity;
	
	private V originalValue;
	
	private String propertyName;
	
	private final List<ValueChangeListener> valueChangeListeners = new ArrayList<>();
	
	public boolean isFormField() {
		return false;
	}
	
	public void bind(JSEntity entity, String viewName) {
		if (this.property != null) {
			this.bind(entity, this.property, viewName);
		} else if (this.propertyName != null && entity != null) {
			IPropertyDesc<?> property = entity.getModel().findProperty(this.propertyName);
			if (property == null) {
				LOG.severe("Cannot find property '" + this.propertyName + "' in entity model '" + entity.getModel().getEntityName() + "'");
				return;
			}
			this.bindGeneric(entity, property, viewName);
			return;
		}
		LOG.severe("Cannot bind property");
		return;
	}
	
	@SuppressWarnings("unchecked")
	public void bindGeneric(JSEntity entity, IPropertyDesc<?> property, String viewName) {
		this.bind(entity, (P) property, viewName);
	}

	public void bind(JSEntity entity, P property, String viewName) {
		assert(entity != null);
		assert(property != null);
		assert(viewName != null);
		
		this.property = property;
		this.entity = entity;
		this.viewName = viewName;
		
		this.originalValue = property.getRawValue(entity);
		
		if (property.isRequired() && originalValue == null && entity.getID() == null) {
			LOG.info("Required property for new object with null value – doing editable");
			this.readonly = false;
		}
		else {
			this.readonly = property.isReadonly(this.isNewEntity());
		}
	}
	
	protected void add(Widget w) {
		this.initWidget(w);
	}

	protected abstract Widget getChildWidget();
		
	public String getDisplayName() {
		return this.getProperty().getDisplayName(this.getViewName());
	}
	
	public JSEntity getEntity() {
		return this.entity;
	}
	
	public String getViewName() {
		return this.viewName;
	}
	
	public P getProperty() {
		return this.property;
	}
	
	public boolean isReadonly() {
		return this.readonly;
	}

	public boolean isNewEntity() {
		return (this.entity == null? true : this.entity.getID() == null);
	}
	
	public boolean isModified() {
		if (this.isNewEntity()) {
			return true;
		}
		if (this.isReadonly()) {
			LOG.fine("Property " + this.getProperty().getName() + " was NOT changed – it's readonly");
			return false;
		}
		
		V v = this.getValue();
		if (this.originalValue == null && v == null) {
			LOG.fine("Property " + this.getProperty().getName() + " was NOT changed – NULL value");
			return false;
		}
		if (this.originalValue != null && v != null) {
			boolean ret = !this.originalValue.isEquals(v);
			if (ret) {
				LOG.fine("Property " + this.getProperty().getName() + " was changed");
			} else {
				LOG.fine("Property " + this.getProperty().getName() + " was NOT changed");
			}
		}
		LOG.fine("Property " + this.getProperty().getName() + " was changed (old or new value is NULL).");
		return true;
	}

	public void applyChanges() {
		LOG.fine("Updating value for property " + this.getProperty().getName());
		this.getEntity().setProperty(this.getProperty().getName(), this.getValue());
	}
	
	protected void onValueChanged() {
		for (ValueChangeListener l : this.valueChangeListeners) {
			l.onValueChanged();
		}
	}
	
	public void addValueChangedListener(ValueChangeListener l) {
		this.valueChangeListeners.add(l);
	}
	
	protected V getOriginalValue() {
		return originalValue;
	}
	
	public abstract V getValue();
	public abstract void setValue(String v);
	public abstract boolean validate();

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public void save(JsonRPCTransaction transaction) {
	}

	public abstract void setEnabled(boolean value);
}
