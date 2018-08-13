package net.inveed.gwt.editor.client.editor;

import java.util.HashMap;

public class EntityEditorRegistry {
	private final HashMap<String, IEntityEditorFactory> factories = new HashMap<>();
	public static final EntityEditorRegistry INSTANCE = new EntityEditorRegistry();
	
	private EntityEditorRegistry() {
	}
	
	public void register(String entityType, IEntityEditorFactory factory) {
		this.factories.put(entityType, factory);
	}
	
	public IEntityEditorFactory getFactory(String entityType) {
		return this.factories.get(entityType);
	}
}
