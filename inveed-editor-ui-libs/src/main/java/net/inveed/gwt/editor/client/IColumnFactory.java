package net.inveed.gwt.editor.client;

import gwt.material.design.client.ui.table.cell.Column;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;

public interface IColumnFactory<T extends IPropertyDescriptor<?>> {
	Column<JSEntity, ?> createListViewColumn(ListViewColumn<T> col);
}
