package net.inveed.gwt.editor.client.editor.fields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.addins.client.combobox.MaterialComboBox;
import gwt.material.design.client.constants.FieldType;
import gwt.material.design.client.ui.html.Span;
import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.WidgetColumn;
import net.inveed.gwt.editor.client.IColumnFactory;
import net.inveed.gwt.editor.client.IPropertyEditorFactory;
import net.inveed.gwt.editor.client.ProgressBarController;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.EntityReferencePropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;

public class EntityRefListBoxSelector extends AbstractFormPropertyEditor<EntityReferencePropertyModel, JSEntity> {
	private static final Logger LOG = Logger.getLogger(EntityRefListBoxSelector.class.getName());
	public static final IPropertyEditorFactory<EntityReferencePropertyModel> createEditorFactory() {
		return new IPropertyEditorFactory<EntityReferencePropertyModel>() {
			@Override
			public AbstractFormPropertyEditor<EntityReferencePropertyModel, ?> createEditor(EntityReferencePropertyModel property, EditorFieldDTO dto) {
				return new EntityRefListBoxSelector();
			}};
	}
	
	public static final IColumnFactory<?> createColumnFactory() {
		return new IColumnFactory<EntityReferencePropertyModel>() {
			
			@Override
			public Column<JSEntity, ?> createListViewColumn(ListViewColumn<EntityReferencePropertyModel> col) {
				WidgetColumn<JSEntity, Widget> ret = new WidgetColumn<JSEntity, Widget>() {

					@Override
					public Widget getValue(JSEntity row) {
						Span ret = new Span();
						JSEntity v = col.getPropertyDescriptor().getValue(row);
						if (v == null) {
							if (col.getPropertyDescriptor().getNotSetText() == null) {
								ret.setText("-- NOT SET --");
							} else {
								ret.setText(col.getPropertyDescriptor().getNotSetText());
							}
							return ret;
						}
						if (v.isInitialized()) {
							ret.setText(v.getDisplayValue());
							return ret;
						}
						Promise<Void, IError> p = v.load();
						p.thenApply((o)->{
							ret.setText(v.getDisplayValue());
							return null;
						});
						p.onError((e,t)->{
							ret.setText("|ERROR LOADING ENTITY|");
							return null;
						});
						return ret;
					}
				};
				return ret;
			}
		};
	}
	
	private MaterialComboBox<JSEntity> listBox;
	
	private JSEntity value;
	private EntityModel targetEntity;
		
	public EntityRefListBoxSelector() {
		this.listBox = new MaterialComboBox<>();
		this.listBox.setFieldType(FieldType.OUTLINED);
		this.listBox.addValueChangeHandler(new ValueChangeHandler<List<JSEntity>>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<List<JSEntity>> event) {
				onValueChanged();
			}
		});
		this.initWidget(this.listBox);
	}
	
	public  void bind(JSEntity entity, EntityReferencePropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.targetEntity = field.getTargetEntityModel();
		this.listBox.setEnabled(!this.isReadonly());
		this.listBox.setLabel(this.getDisplayName());
		
		if (this.isReadonly()) {
			this.setReadonlyOriginalValue();
		} else {
			this.fill();
		}
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
		JSEntity selected = this.listBox.getSingleValue();
		if (this.getProperty().isRequired() && selected == null) {
			return false;
		}
		return true;
	}
	
	private void fill() {
		this.listBox.setEnabled(false);
		
		if (!this.getProperty().isRequired()) {
			if (this.getProperty().getNotSetText() != null) {
				this.listBox.addItem(this.getProperty().getNotSetText(), JSEntity.EMPTY);
			} else {
				this.listBox.addItem("--- NOT SET ---", JSEntity.EMPTY);
			}
			
		}
		
		Promise<JSEntityList, IError> result = this.getEntity().getEntityManager().listEntities(this.targetEntity, 0, Integer.MAX_VALUE, this.buildListFilters());
		ProgressBarController.INSTANCE.add(result);
	
		result.thenApply((JSEntityList v) -> {
			int idx = this.getProperty().isRequired() ? 0 : 1;
			try {
				LOG.fine("Got selection response");
				if (v == null) {
					LOG.fine("Got empty response");
					return null;
				}
				List<JSEntity> list = v.getValue();
				IJSObject origId = null;
				if (this.getInitialValue() != null) {
					origId = this.getInitialValue().getID();
				}
				for (int j = 0; j < list.size(); j++){
					JSEntity entity = list.get(j);
					LOG.fine("Processing next item ");
					
					IJSObject oid = entity.getID();
					if (oid == null) {
						//TODO: error
						continue;
					}
					String oname = entity.getDisplayValue();
					
					LOG.fine("Adding item: " + oid + " / " + oname);
					this.listBox.addItem(oname, entity);
					if (origId != null) {
						if (origId.isEquals(oid)) {
							this.listBox.setSelectedIndex(idx);
						}
					}
					idx++;
				}
				this.listBox.setEnabled(!this.isReadonly());
				
				this.onValueChanged();
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
		JSEntity ov = this.getInitialValue();
		if (ov == null) {
			return;
		}
		if (ov.isInitialized()) {
			this.listBox.addItem(ov.getDisplayValue(), ov);
			this.listBox.setSelectedIndex(0);
		} else {
			Promise<Void, IError> p = ov.load();
			p.thenApply((v)->{
				this.listBox.setEnabled(true);
				this.listBox.addItem(ov.getDisplayValue(), ov);
				this.listBox.setSelectedIndex(0);
				this.listBox.setEnabled(false);
				return null;
			});
			p.onError((v, e)->{
				this.listBox.setEnabled(true);
				this.listBox.addItem("CANNOT LOAD ENTITY", ov);
				this.listBox.setSelectedIndex(0);
				this.listBox.setEnabled(false);
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
		JSEntity entity = this.listBox.getSingleValue();
		
		if (entity == null || !entity.isValid()) {
			LOG.fine("Current value is null");
			this.value = null;
		} else {
			LOG.fine("Current value ID is " + entity.toString());
			this.value = entity;
		}
	}
	
	@Override
	public void setValue(JSEntity value) {
		int idx = this.listBox.getValueIndex(value);
		if (idx > 0 ) {
			this.listBox.setSelectedIndex(idx);
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
	
	@Override
	public void setGrid(String grid) {
		this.listBox.setGrid(grid);
	}
}
