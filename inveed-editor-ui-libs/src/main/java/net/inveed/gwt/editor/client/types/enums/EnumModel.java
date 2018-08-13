package net.inveed.gwt.editor.client.types.enums;

import java.util.HashMap;
import java.util.Set;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.model.ConfigurationRegistry;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.shared.EnumModelDTO;

public class EnumModel {
	public static final class JSEnumValue implements IJSObject {
		public static final String TYPE = "ENUM";
		private final EnumModel model;
		private final String code;
		private final String value;

		private JSEnumValue(String code, String value, EnumModel model) {
			this.code = code;
			this.value = value;
			this.model = model;
		}
		
		@Override
		public int compareTo(IJSObject other) {
			if (other == null) {
				return 1;
			}
			if (other.getType() != TYPE) {
				return this.getType().compareTo(other.getType());
			}
			
			JSEnumValue e = (JSEnumValue) other;
			if (e.model != this.model) {
				return 0;
			}
			return this.code.compareTo(e.code);
		}

		@Override
		public String getType() {
			return TYPE;
		}
		
		public String getValue() {
			return this.value;
		}
		
		public EnumModel getModel() {
			return this.model;
		}
		
		public String getCode() {
			return this.code;
		}

		@Override
		public JSONValue getJSONValue() {
			return new JSONString(this.code);
		}
		
		@Override
		public boolean isEquals(IJSObject other) {
			if (other == null) {
				return false;
			}
			if (other.getType() != this.getType()) {
				return false;
			}
			JSEnumValue o = (JSEnumValue) other;
			if (o.model != this.model) {
				return false;
			}
			return o.code.equals(this.code);
		}
		
		@Override
		public String toString() {
			return this.getValue();
		}
	}
	
	public static interface EnumModelMapper extends ObjectMapper<EnumModelDTO> {}
	
	private HashMap<String, JSEnumValue> values = new HashMap<>();
	private String name;
	private ConfigurationRegistry registry;
	
	private EnumModel(EnumModelDTO dto, ConfigurationRegistry registry) {
		for (String code : dto.values.keySet()) {
			String val = dto.values.get(code);
			JSEnumValue v = new JSEnumValue(code, val, this);
			this.values.put(code, v);
		}
		this.name = dto.name;
		this.registry = registry;
	}
	
	public ConfigurationRegistry getRegistry() {
		return this.registry;
	}
	
	public Set<String> getCodes() {
		return this.values.keySet();
	}
	
	public JSEnumValue getByCode(String code) {
		if (code == null) {
			return null;
		}
		return this.values.get(code);
	}

	public static EnumModel parseModel(String json, ConfigurationRegistry registry) {
		GWT.log("Parsing enum model from json: " + json);
		EnumModelMapper mapper = GWT.create(EnumModelMapper.class);
		EnumModelDTO model = mapper.read(json);
		
		GWT.log("Model parsed for enum " + model.name);
		return new EnumModel(model, registry);
	}

	public String getName() {
		return this.name;
	}
}
