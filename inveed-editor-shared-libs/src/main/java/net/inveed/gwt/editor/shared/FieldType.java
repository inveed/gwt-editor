package net.inveed.gwt.editor.shared;

import java.util.HashMap;

public enum FieldType {
	AUTO(null),
	ID_INTEGER("id"),
	ID_STRING("stringId"),
	INTEGER("integer"),
	FLOAT("number"),
	OBJECT_REF("objectRef"),
	TEXT("text"),
	TEXT_LONG("textlong"),
	URL("url"),
	PASSWORD("password"),
	DATE("date"),
	TIMESTAMP("timestamp"),
	TIMESTAMP_MS("timestampMs"),
	DURATION_ISO("durationISO"),
	DURATION_MIN("durationMinutes"),
	DURATION_SECONDS("durationSeconds"),
	DURATION_MS("durationMs"),
	BOOLEAN("boolean"),
	ADDR_IP("ipAddress"),
	ADDR_HOST("hostname"),
	ENUM("enum"),
	SECRET_KEY("secretKey"),
	BINARY_KEY("binaryKey"),
	LINKED_ENTITIES_LIST("linkedList");
	
	private final String value;
	private static HashMap<String, FieldType> nameMap;
	
	private FieldType(String v){
		this.value = v;
		this.register();
	}
	
	private void register() {
		if (nameMap == null) {
			nameMap = new HashMap<>();
		}
		nameMap.put(this.value, this);
	}
	
	public static final FieldType byName(String v) {
		return nameMap.get(v);
	}
	public String getUIValue() {
		return this.value;
	}
}
