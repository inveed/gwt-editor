package net.inveed.gwt.editor.client.editor.fields;

import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.EnumPropertyModel;
import net.inveed.gwt.editor.client.types.enums.EnumModel;
import net.inveed.gwt.editor.client.types.enums.EnumModel.JSEnumValue;

public class EnumItemSelector extends AbstractFormPropertyEditor<EnumPropertyModel, JSEnumValue> {
	//private static final Logger LOG = Logger.getLogger(EnumItemSelector.class.getName());

	private Select list;
	private EnumModel enumModel;
	
	public EnumItemSelector() {
		this.list = new Select();
		
		this.add(this.list);
	}
	public  void bind(JSEntity entity, EnumPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.enumModel = field.getEnumModel();
		this.list.setEnabled(false);
		if (!this.getProperty().isRequired()) {
			Option o = new Option();
			o.setValue("");
			o.setText("-- NOT SET --");
			if (field.getDefaultValue() == null) {
				o.setSelected(true);
			}
			this.list.add(o);
		}
		for (String k : this.enumModel.getCodes()) {
			JSEnumValue v = this.enumModel.getByCode(k);
			if (v == null) {
				continue;
			}
			Option o = new Option();
			o.setValue(v.getCode());
			o.setText(v.getValue());
			if (field.getDefaultValue() != null && field.getDefaultValue().getCode().equals(v.getCode())) { 
				o.setSelected(true);
			} else {
				o.setSelected(false);
			}
			this.list.add(o);
		}
		
		if (this.enumModel.getCodes().size() > 10) {
			this.list.setLiveSearch(true);
			this.list.setLiveSearchNormalize(true);
		}
		
		if (this.getOriginalValue() != null) {
			this.select(this.getOriginalValue().getCode());
		}
		
		this.list.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				onValueChanged();
			}
		});
		this.list.setEnabled(!this.isReadonly());
	}
	
	@Override
	public void setId(String uid) {
		this.list.setId(uid);
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.list;
	}
	
	@Override
	public void setValue(String v) {
		if (v == null) {
			return;
		}
		this.select(v.trim());
	}
	
	@Override
	public boolean validate() {
		if (this.getProperty().isRequired() && this.list.getSelectedItem() == null) {
			return false;
		}
		return true;
	}

	@Override
	public JSEnumValue getValue() {
		Option o = this.list.getSelectedItem();
		if (o == null) {
			return null;
		}
 		String sv = o.getValue();
		if (sv == null || sv.length() == 0) {
			return null;
		}
		return this.enumModel.getByCode(sv);
	}
	
	private void select(String value) {
		for (int i = 0 ; i < this.list.getItemCount(); i++) {
			Option o = this.list.getItem(i);
			if (o.getValue().equals(value)) {
				o.setSelected(true);
			} else {
				o.setSelected(false);
			}
		}
	}
	@Override
	public void setEnabled(boolean value) {
		this.list.setEnabled(value);
	}
}
