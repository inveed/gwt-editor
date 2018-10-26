package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.List;

import net.inveed.gwt.editor.client.UIRegistry;
import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorFieldsRowDTO;

public class AutoFieldsRow extends AbstractEditorRow<EditorFieldsRowDTO> {

	private final ArrayList<AutoFormFieldInfo> fields;
	
	public AutoFieldsRow(EditorFieldsRowDTO dto, EntityModel model,
			AbstractAutoFormContainer<?> parent) {
		super(dto, model, parent);
		this.fields = new ArrayList<>();
		this.prepare();
	}

	@Override
	public void bld() {
		for (AutoFormFieldInfo fld : this.fields) {
			this.addToParent2(fld.getEditor());
		}
	}
	private void prepare() {
		if (this.getDTO().fields == null) {
			return;
		}
		for (EditorFieldDTO fdto : this.getDTO().fields) {
			IPropertyDescriptor<?> prop = this.getEntityModel().getPropertyDescriptor(fdto.property);
			if (prop == null) {
				//TODO: LOG
				continue;
			}
			AbstractFormPropertyEditor<?, ?> editor = UIRegistry.INSTANCE.getPropertyEditor(prop, fdto);
			if (editor == null) {
				//TODO: LOG
				continue;
			}
			if (fdto.readonly) {
				editor.setReadonly(true);
			}
			AutoFormFieldInfo fld = new AutoFormFieldInfo(prop, editor);
			this.fields.add(fld);
		}
		if (this.fields.size() == 0) {
			//TODO: LOG
			return;
		}
		int gridSize = 12 / this.fields.size();
		for (AutoFormFieldInfo fld : this.fields) {
			fld.setGridWidth(gridSize);
		}
	}

	@Override
	public void findFields(List<AutoFormFieldInfo> flist) {
		for (AutoFormFieldInfo fld : this.fields) {
			flist.add(fld);
		}
	}
	
	@Override
	public void bind(JSEntity entity, String viewName) {
		for (AutoFormFieldInfo fld : this.fields) {
			fld.getEditor().bindGeneric(entity, fld.getPropertyDescriptor(), viewName);
		}
	}

	@Override
	public void setEnabled(boolean value) {
		for (AutoFormFieldInfo fld : this.fields) {
			fld.setEnabled(value);
		}
	}
	
	@Override
	public void applyChanges() {
		for (AutoFormFieldInfo fld : this.fields) {
			if (fld.getEditor().isModified()) {
				fld.getEditor().applyChanges();
			}
		}
	}
	
	@Override
	public boolean isModified() {
		for (AutoFormFieldInfo fld : this.fields) {
			if (fld.getEditor().isModified()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		for (AutoFormFieldInfo fld : this.fields) {
			fld.getEditor().save(transaction);
		}
	}
	
	@Override
	public boolean validate() {
		boolean ret = true;
		for (AutoFormFieldInfo fld : this.fields) {
			if (!fld.isEnabled())
				continue;
			ret = fld.getEditor().validate() & ret;
		}
		return ret;
	}

}
