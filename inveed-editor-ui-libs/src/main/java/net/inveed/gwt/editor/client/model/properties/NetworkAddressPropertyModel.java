package net.inveed.gwt.editor.client.model.properties;

import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.editor.fields.NetworkAddressPropertyEditor;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSNetworkAddress;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public class NetworkAddressPropertyModel extends AbstractPropertyModel<JSNetworkAddress> {
	private JSNetworkAddress defaultValue;
	
	public NetworkAddressPropertyModel(PropertyModelDTO field, String name, EntityModel entity) {
		super(field, name, entity);
		if (field.type != FieldType.ADDR_HOST && field.type != FieldType.ADDR_IP) {
			//TODO: Exception!
		}
		if (field.defaultValue != null) {
			this.defaultValue = new JSNetworkAddress(field.defaultValue);
		}
	}

	@Override
	public NetworkAddressPropertyEditor createEditor() {
		return new NetworkAddressPropertyEditor();
	}

	@Override
	public JSNetworkAddress convertToJSObject(JSONValue v, EntityManager em) {
		if (v == null) {
			return null;
		} else if (v.isString() != null) {
			return new JSNetworkAddress(v.isString().stringValue());
		} else {
			return null;
		}
	}
	
	@Override
	public JSNetworkAddress getRawValue(JSEntity entity) {
		return (JSNetworkAddress) entity.getProperty(this.getName(), JSNetworkAddress.TYPE);
	}

	@Override
	public JSNetworkAddress getDefaultValue() {
		return this.defaultValue;
	}

}
