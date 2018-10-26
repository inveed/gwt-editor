package net.inveed.gwt.editor.client.editor;

import net.inveed.gwt.editor.client.model.EntityModel;

public interface IEntityEditorFactory {
	public AbstractEntityEditorForm create(String viewName);
	boolean reopenAfterCreate(String createView, String editView, EntityModel model);
}
