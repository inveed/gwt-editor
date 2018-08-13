package net.inveed.gwt.editor.client.editor.fields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.ProgressBarController;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.EntityReferencePropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;

public class EntityRefListBoxSelector extends AbstractFormPropertyEditor<EntityReferencePropertyModel, JSEntity> {
	private static final Logger LOG = Logger.getLogger(EntityRefListBoxSelector.class.getName());
	private Select listBox;
	
	private JSEntity value;
	private EntityModel targetEntity;
	
	private HashMap<String, JSEntity> idToEntityMap = new HashMap<>();
	
	public EntityRefListBoxSelector() {
		this.listBox = new Select();
		this.listBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onValueChanged();
			}
		});
		this.add(this.listBox);
	}
	public  void bind(JSEntity entity, EntityReferencePropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.targetEntity = field.getTargetEntityModel();
		this.listBox.setEnabled(!this.isReadonly());
		
		if (this.isReadonly()) {
			this.setReadonlyOriginalValue();
		} else {
			this.fill();
		}
	}
	
	@Override
	public void setId(String uid) {
		this.listBox.setId(uid);
	}
		
	@Override
	protected Widget getChildWidget() {
		return this.listBox;
	}
	
	
	private Map<String, JSONValue> buildListFilters() {
		if (this.getProperty().getFilters() == null) {
			return null;
		}
		if (this.getProperty().getFilters().size() == 0) {
			return null;
		}
		Map<String, JSONValue> ret = new HashMap<>();
		for (String key : this.getProperty().getFilters().keySet()) {
			String filter = this.getProperty().getFilters().get(key);
			if (filter.startsWith("$((") && filter.endsWith("))")) {
				String attr = filter.substring(3, filter.length() - 2);
				JSONValue v = this.getAttributeValue(attr);
				if (v != null) 
					ret.put(key, v);
			}
		}
		return ret;
	}
	
	private JSONValue getAttributeValue(String attr) {
		String[] path = attr.split("\\.");
		JSEntity currentEntity = this.getEntity();
		for (int i = 0; i < path.length; i++) {
			String p = path[i];
			IJSObject v = currentEntity.getProperty(p);
			if (v == null) {
				return null;
			}
			if (v.getType() == JSEntity.TYPE) {
				currentEntity = (JSEntity) v;
			} else if (i == path.length - 1) {
				return v.getJSONValue();
			} else {
				//TODO: LOG
			}
		}
		return currentEntity.getJSONValue();
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.listBox.getSelectedItem() == null) {
			return false;
		}
		return true;
	}
	
	private void fill() {
		this.listBox.setEnabled(false);
		if (!this.getProperty().isRequired()) {
			Option o = new Option();
			o.setText("-- NOT SET --");
			o.setValue("");
			this.listBox.add(o);
		}

		Promise<JSEntityList, IError> result = this.getEntity().getEntityManager().listEntities(this.targetEntity, 0, Integer.MAX_VALUE, this.buildListFilters());
		ProgressBarController.INSTANCE.add(result);
	
		result.thenApply((JSEntityList v) -> {
			try {
				LOG.fine("Got selection response");
				if (v == null) {
					LOG.fine("Got empty response");
					return null;
				}
				List<JSEntity> list = v.getValue();
				this.idToEntityMap.clear();
				IJSObject origId = null;
				if (this.getOriginalValue() != null) {
					origId = this.getOriginalValue().getID();
				}
				for (int j = 0; j < list.size(); j++){
					JSEntity entity = list.get(j);
					LOG.fine("Processing next item ");
					
					IJSObject oid = entity.getID();
					if (oid == null) {
						//TODO: error
						continue;
					}
					String stringOid = oid.toString();
					String oname = entity.getDisplayValue();
	
					
					LOG.fine("Adding item: " + oid + " / " + oname);
					Option o = new Option();
					o.setValue(stringOid);
					o.setText(oname);
					listBox.add(o);
					this.idToEntityMap.put(stringOid, entity);
					if (origId != null) {
						if (origId.isEquals(oid)) {
							o.setSelected(true);
						}
					}
				}
				this.listBox.setEnabled(!this.isReadonly());
				
				this.onValueChanged();
				//this.listBox.setReadOnly(this.isReadonly());
				return null;
			} finally {
				ProgressBarController.INSTANCE.remove(result);
			}
		});

		result.onError((e,t) -> {
			ProgressBarController.INSTANCE.remove(result);
			return null;
		});
	}
	
	private void setReadonlyOriginalValue() {
		this.listBox.setEnabled(false);
		JSEntity ov = this.getOriginalValue();
		if (ov == null) {
			return;
		}
		if (ov.isInitialized()) {
			Option o = new Option();
			o.setValue("");
			o.setText(ov.getDisplayValue());
			o.setSelected(true);
			this.listBox.add(o);
		} else {
			Promise<Void, IError> p = ov.load();
			p.thenApply((v)->{
				listBox.setEnabled(true);
				Option o = new Option();
				o.setValue("");
				o.setText(ov.getDisplayValue());
				o.setSelected(true);
				listBox.add(o);
				listBox.setEnabled(false);
				return null;
			});
			p.onError((v, e)->{
				listBox.setEnabled(true);
				Option o = new Option();
				o.setValue("");
				o.setText("CANNOT LOAD ENTITY");
				o.setSelected(true);
				listBox.add(o);
				listBox.setEnabled(false);
				return null;
			});
		}
		
	}

	@Override
	public boolean isModified() {
		if (this.isReadonly()) {
			return false;
		}
		this.updateCurrentValue();
		return super.isModified();
	}

	
	private void updateCurrentValue() {
		LOG.fine("Value changed");
		Option sopt = this.listBox.getSelectedItem();
		
		if (sopt == null) {
			LOG.fine("Current value is null");
			this.value = null;
		} else {
			JSEntity entity = this.idToEntityMap.get(sopt.getValue());
		
			if (entity == null) {
				LOG.fine("Current value is null");
				this.value = null;
			} else {
				LOG.fine("Current value ID is " + entity.toString());
				this.value = entity;
			}
		}
	}
	
	@Override
	public void setValue(String value) {
		for (int i = 0 ; i < this.listBox.getItemCount(); i++) {
			Option o = this.listBox.getItem(i);
			if (o.getValue().equals(value)) {
				o.setSelected(true);
			} else {
				o.setSelected(false);
			}
		}
	}

	@Override
	public JSEntity getValue() {
		return this.value;
	}
	@Override
	public void setEnabled(boolean value) {
		this.listBox.setEnabled(value);
	}
}
