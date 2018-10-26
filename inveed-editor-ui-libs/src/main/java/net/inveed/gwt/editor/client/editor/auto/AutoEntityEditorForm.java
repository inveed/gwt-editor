package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.inveed.gwt.editor.client.editor.AbstractEntityEditorForm;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;

public class AutoEntityEditorForm extends AbstractEntityEditorForm {
	private static final Logger LOG = Logger.getLogger(AutoEntityEditorForm.class.getName());
	
	public static final String C_CONST_KEY_PREFIX = "tabs";

	private AutoFormRoot rootPanel;
	
	private final List<AutoFormFieldInfo> fields;
	
	int undefinedSectionsOrder = 65000;
	
	private AutoFormViewDTO dto;
	
	public AutoEntityEditorForm(AutoFormViewDTO dto, EntityModel entityModel) {
		super(dto.viewName);
		this.dto = dto;
		this.fields = new ArrayList<>();
		this.rootPanel = new AutoFormRoot(this.dto, entityModel);
		this.rootPanel.bld();
		this.initWidget(this.rootPanel.getWidget());
		
		this.build();
	}
	
	@Override
	public void setEnabled(boolean value) {
		this.rootPanel.setEnabled(value);
	}
	
	protected void build() {
		this.rootPanel.findFields(this.fields);
		for (AutoFormFieldInfo fld : this.fields) {
			fld.getEditor().addValueChangedListener(new AbstractPropertyEditor.ValueChangeListener() {
				@Override
				public void onValueChanged() {
					AutoEntityEditorForm.this.onValueChanged();
				}});
		}
	}
		
	@Override
	protected void bind() {
		this.rootPanel.bind(this.getEntity(), this.getViewName());
	}
	
	private void updateContitionFields() {
		HashMap<String, Boolean> executionResultsCache = new HashMap<>();
		for (AutoFormFieldInfo fld : this.fields) {
			if (fld.getEditor().getProperty().getEnabledCondition() != null) {
				String cond = fld.getEditor().getProperty().getEnabledCondition();
				boolean res;
				if (executionResultsCache.containsKey(cond)) {
					res = executionResultsCache.get(cond);
				} else {
					res = evaluateCondition(cond);
					executionResultsCache.put(cond, res);
				}
				fld.setEnabled(res);
			}
		}
	}
	
	private boolean evaluateCondition(String c) {
		String fieldName;
		String value;
		if (c.contains("==")) {
			String[] parts = c.split("==");
			if (parts.length != 2) {
				return true;
			}
			parts[0] = parts[0].trim();
			parts[1] = parts[1].trim();
			if (parts[0].startsWith("{{") && parts[0].endsWith("}}")) {
				fieldName = parts[0].substring(2, parts[0].length()-2);
				value = parts[1];
			} else if (parts[1].startsWith("{{") && parts[1].endsWith("}}")) {
				fieldName = parts[1].substring(2, parts[1].length()-2);
				value = parts[0];
			} else {
				return true;
			}
		} else {
			fieldName=c;
			value="YES";
		}
		for (AutoFormFieldInfo fld : this.fields) {
			if (!fld.getPropertyDescriptor().getName().equals(fieldName)) {
				continue;
			}
			if (fld.getEditor().getValue() == null && "$NULL$".equals(value)) {
				return true;
			} else if (fld.getEditor().getValue() == null) {
				return false;
			} else if (fld.getEditor().getValue().toString().equals(value)) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean validate() {
		this.updateContitionFields();
		return this.rootPanel.validate();
	}
	public void applyChanges() {
		this.rootPanel.applyChanges();
	}
	
	@Override
	public boolean isModified() {
		return this.rootPanel.isModified();
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		this.getEntity().save(transaction);
		this.rootPanel.persist(transaction);
	}

	@Override
	public Integer getRequestedWidth() {
		if (this.dto.width == null) {
			return null;
		}
		return this.dto.width == 0 ? null : this.dto.width;
	}

	@Override
	public Integer getRequestedHeight() {
		if (this.dto.heigh == null) {
			return null;
		}
		return this.dto.heigh == 0 ? null : this.dto.heigh;
	}
}
