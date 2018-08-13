package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import net.inveed.gwt.editor.client.editor.AbstractEntityEditorForm;
import net.inveed.gwt.editor.client.editor.fields.AbstractFormPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor.ValueChangeListener;
import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityFormView;
import net.inveed.gwt.editor.client.model.EntityFormView.PropertyInView;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.shared.forms.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.EditorPanelDTO;

public class AutoEntityEditorForm extends AbstractEntityEditorForm {
	private static final Logger LOG = Logger.getLogger(AutoEntityEditorForm.class.getName());
	public static final String C_CONST_KEY_PREFIX = "tabs";
	//public static final String C_CONST_HINTS_PREFIX = "hints";
	//public static final String C_CONST_ERROR_PREFIX = "errors";
	
	private AutoFormRootPanel rootPanel;
	
	private final List<AutoFormField> fields;
	private final HashMap<String, AutoFormSimpleFieldsSection> sections;
	private final HashMap<String, AutoFormPanel> panels;
	
	int undefinedSectionsOrder = 65000;
	
	public AutoEntityEditorForm(EntityFormView view) {
		super(view);
		
		this.rootPanel = new AutoFormRootPanel();
		this.panels = new HashMap<>();
		this.sections = new HashMap<>();
		this.fields = new ArrayList<>();
		
		this.initWidget(this.rootPanel);
		
		this.buld();
	}
	
	@Override
	public void setEnabled(boolean value) {
		for (AutoFormField f : this.fields) {
			f.setEnabled(value);
		}
	}
	
	
	private void addField(AutoFormField p) {
		if (p.getEditor().isFormField()) {
			AutoFormSimpleFieldsSection c = this.getFieldsSection(p.getPropertyInView().attr.container);
			c.addField((AutoFormSimpleField) p);
		} else {
			AutoFormComplexFieldSection c = new AutoFormComplexFieldSection((AutoFormComplexField) p);
			AutoFormPanel panel = this.panels.get(p.getPropertyInView().attr.container);
			if (panel == null) {
				AutoFormSimplePanel tab = new AutoFormSimplePanel();
				tab.addSection(c);
				this.rootPanel.addTab(p.getPropertyInView().getDisplayName(), tab);
			} else {
				panel.addSection(c);
			}
		}
		
		this.fields.add(p);
		
		p.getEditor().addValueChangedListener(new ValueChangeListener() {
			@Override
			public void onValueChanged() {
				AutoEntityEditorForm.this.onValueChanged();
			}
		});
	}
	
	private AutoFormSimpleFieldsSection getFieldsSection(String name) {
		if (name == null) {
			name = "";
		}
		name = name.trim();
		AutoFormSimpleFieldsSection ret = this.sections.get(name);
		if (ret != null) {
			return ret;
		} else {
			ret = new AutoFormSimpleFieldsSection(name);
			ret.setOrder(undefinedSectionsOrder++);
			this.sections.put(name, ret);
			this.rootPanel.addSection(ret);
			return ret;
		}
	}
	
	private void buld() {
		List<PropertyInView> pvlist = this.getView().getProperties();
		this.createPanels();
		
		LOG.fine("Found " + pvlist.size() + " attributes for view");
		for (PropertyInView fm : pvlist) {
			AbstractPropertyEditor<?,?> e = fm.property.createEditor();
			if (e == null) {
				LOG.warning("Cannot create editor for property " + fm.property.getName());
				continue;
			}
			LOG.fine("Added field editor for property " + fm.property.getName());
			if (e.isFormField()) {
				AutoFormSimpleField p = new AutoFormSimpleField(fm, (AbstractFormPropertyEditor<?,?>) e);
				this.addField(p);
			} else {
				AutoFormComplexField p = new AutoFormComplexField(fm, e);
				this.addField(p);
			}
			
		}
		this.rootPanel.build();
	}

	private void createPanels() {
		if (this.getView().editorDTO == null) {
			return;
		}
		
		createSections(this.rootPanel, this.getView().editorDTO.rootSections);
		
		if (this.getView().editorDTO.panels != null) {
			for (EditorPanelDTO tc : this.getView().editorDTO.panels) {
				AutoFormSimplePanel panel = new AutoFormSimplePanel();
				panel.setOrder(tc.order);
				rootPanel.addTab(getTabDisplayName(tc.name), panel);
				createSections(panel, tc.sections);
			}
		}
	}
	
	public String getTabDisplayName(String tab) {
		String ret = null;
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getView().getModel().getKey() + "." + "views" + "." + this.getView().getName() + "." + C_CONST_KEY_PREFIX + "." + tab));
		if (ret != null) {
			return ret;
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getView().getModel().getKey() + "." + C_CONST_KEY_PREFIX + "." + tab));
		if (ret != null) {
			return ret;
		}
		
		return tab;
	}
	private void createSections(AutoFormPanel panel, EditorSectionDTO[] sections) {
		if (sections == null) {
			return;
		}
		for (EditorSectionDTO s : sections) {
			AutoFormSimpleFieldsSection fs = new AutoFormSimpleFieldsSection(s.name);
			fs.setOrder(s.order);
			panel.addSection(fs);
			this.sections.put(s.name, fs);
		}
	}
	
	@Override
	protected void bind() {
		for (AutoFormField fld : this.fields) {
			fld.getEditor().bindGeneric(this.getEntity(), fld.getPropertyInView().property, this.getView().getName());
		}
	}
	
	private void updateContitionFields() {
		HashMap<String, Boolean> executionResultsCache = new HashMap<>();
		for (AutoFormField fld : this.fields) {
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
		for (AutoFormField fld : this.fields) {
			if (!fld.getPropertyInView().property.getName().equals(fieldName)) {
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
		boolean ret = true;
		this.updateContitionFields();
		for (AutoFormField fld : this.fields) {
			if (!fld.isEnabled()) 
				continue;
			if (!fld.getEditor().validate()) {
				ret = false;
			}
		}
		return ret;
	}
	public void applyChanges() {
		for (AutoFormField fld : this.fields) {
			if (!fld.getEditor().isModified()) {
				continue;
			}
			fld.getEditor().applyChanges();
		}
	}
	
	@Override
	public boolean isModified() {
		for (AutoFormField fld : this.fields) {
			if (fld.getEditor().isModified()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		this.getEntity().save(transaction);
		for (AutoFormField fld : this.fields) {
			fld.getEditor().save(transaction);
		}
	}

	@Override
	public Integer getRequestedWidth() {
		if (this.getView().editorDTO == null) {
			return null;
		}
		Integer ret = this.getView().editorDTO.width;
		if (ret == null) 
			return null;
		if (ret == 0)
			return null;
		return ret;
	}

	@Override
	public Integer getRequestedHeight() {
		if (this.getView().editorDTO == null) {
			return null;
		}
		Integer ret = this.getView().editorDTO.heigh;
		if (ret == null) 
			return null;
		if (ret == 0)
			return null;
		return ret;
	}

}
