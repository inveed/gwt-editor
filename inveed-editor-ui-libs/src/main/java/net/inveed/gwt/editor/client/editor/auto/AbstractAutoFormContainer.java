package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.shared.forms.panels.AbstractEditorPanelDTO;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorFieldsRowDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorListRowDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorTabContainerDTO;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;

public abstract class AbstractAutoFormContainer<T extends AbstractEditorPanelDTO & IEditorRowDTO> extends AbstractEditorRow<T> implements IContainer{
	private final List<AbstractEditorRow<?>> rows;
	
	public AbstractAutoFormContainer(T dto, EntityModel model, IContainer container) {
		super(dto, model, container);
		this.rows = new ArrayList<>();
		this.prepare();
		
	}
	
	@Override
	public void findFields(List<AutoFormFieldInfo> fields) {
		for (AbstractEditorRow<?> row : this.rows) {
			row.findFields(fields);
		}
	}
	
	public void bld() {
		this.addToParent2(this.getWidget());
		for (AbstractEditorRow<?> row : this.rows) {
			row.bld();
		}
	}
	
	@Override
	public void bind(JSEntity entity, String viewName) {
		for (AbstractEditorRow<?> row : this.rows) {
			row.bind(entity, viewName);
		}
	}
	
	public void setEnabled(boolean value) {
		for (AbstractEditorRow<?> row : this.rows) {
			row.setEnabled(value);
		}
	}
	
	public boolean validate() {
		boolean ret = true;
		for (AbstractEditorRow<?> row : this.rows) {
			ret = row.validate() & ret;
		}
		return ret;
	}
	
	@Override
	public boolean isModified() {
		for (AbstractEditorRow<?> row : this.rows) {
			if (row.isModified()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void applyChanges() {
		for (AbstractEditorRow<?> row : this.rows) {
			row.applyChanges();
		}
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		for (AbstractEditorRow<?> row : this.rows) {
			row.persist(transaction);
		}
	}
	
	private AbstractEditorRow<?> createChild(IEditorRowDTO rdto) {
		if (rdto.getClass() == EditorFieldsRowDTO.class) {
			return new AutoFieldsRow((EditorFieldsRowDTO) rdto, this.getEntityModel(), this);
		} else if (rdto.getClass() == EditorListRowDTO.class) {
			return new AutoListRow((EditorListRowDTO) rdto, this.getEntityModel(), this);
		} else if (rdto.getClass() == EditorSectionDTO.class) {
			return new AutoFormSection((EditorSectionDTO) rdto, this.getEntityModel(), this);
		} else if (rdto.getClass() == EditorTabContainerDTO.class) {
			return new AutoFormTabPanel((EditorTabContainerDTO) rdto, this.getEntityModel(), this);
		} else {
			return null;
		}
	}
	
	
	
	private void prepare() {
		for (IEditorRowDTO rdto : this.getDTO().rows) {
			AbstractEditorRow<?> row = this.createChild(rdto);
			if (row == null) {
				//TODO: LOG ERROR
				continue;
			}
			this.rows.add(row);
		}
	}
}
