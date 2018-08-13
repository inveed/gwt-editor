package net.inveed.gwt.editor.client.editor;

import net.inveed.gwt.editor.client.model.EntityFormView;

public interface IEntityEditorFactory {
	public AbstractEntityEditorForm create(EntityFormView view);
}
