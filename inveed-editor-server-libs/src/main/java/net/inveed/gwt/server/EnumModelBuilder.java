package net.inveed.gwt.server;

import java.util.HashMap;
import java.util.Map;

import net.inveed.typeutils.EnumTypeDesc;
import net.inveed.gwt.editor.shared.EnumModelDTO;

public class EnumModelBuilder {
	 
	 public static EnumModelDTO build(EnumTypeDesc<?> type) {
		 Map<String, String> values = new HashMap<>();
		 
		 for (Enum<?> e : type.getDeclaredValues()) {
			 if (e == null) {
				 continue;
			 }
			 String displayValue = type.getDisplayValue(e);
			 String v = e.toString();
			 if (displayValue == null) {
				 displayValue = v;
			 }
			 values.put(v, displayValue);			 
		 }
		 return new EnumModelDTO(values, type.getName());
	 }
}
