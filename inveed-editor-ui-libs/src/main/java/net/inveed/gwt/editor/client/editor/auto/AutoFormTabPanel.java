package net.inveed.gwt.editor.client.editor.auto;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.DOM;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTab;
import gwt.material.design.client.ui.MaterialTabItem;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorTabContainerDTO;

public class AutoFormTabPanel extends AbstractEditorRow<EditorTabContainerDTO> implements IContainer {
	private final MaterialRow self;
	private List<AutoFormTab> tabs;
	private MaterialTab mt;
	public AutoFormTabPanel(EditorTabContainerDTO dto, EntityModel model, AbstractAutoFormContainer<?> container) {
		super(dto, model, container);
		this.self = new MaterialRow();
		this.self.setShadow(1);
		this.mt = new MaterialTab();
		this.prepare();
	}
	
	public MaterialRow getWidget() {
		return this.self;
	}
	
	@Override
	public void bld() {
		this.addToParent2(this.getWidget());
		for (AutoFormTab tab : this.tabs) {
			tab.bld();
		}
	}
	
	private void prepare() {
		this.tabs = new ArrayList<>();		
		this.mt.setBackgroundColor(Color.WHITE);
		this.mt.setShadow(0);
		//mt.setMarginBottom(10);
		this.getWidget().add(mt);
		for (EditorSectionDTO tab : this.getDTO().tabs) {
			String href = DOM.createUniqueId();
			MaterialTabItem ti = new MaterialTabItem();
			MaterialLink mlink = new MaterialLink(tab.title);
			mlink.setHref("#" + href);
			ti.add(mlink);
			mt.add(ti);
			
			AutoFormTab ftab = new AutoFormTab(tab, this.getEntityModel(), this);
			ftab.getWidget().setId(href);
			this.tabs.add(ftab);
			this.getWidget().add(ftab.getColumn());
		}
	}

	@Override
	public void findFields(List<AutoFormFieldInfo> fields) {
		for (AutoFormTab tab : this.tabs) {
			tab.findFields(fields);
		}
	}
	
	@Override
	public void bind(JSEntity entity, String viewName) {
		for (AutoFormTab tab : this.tabs) {
			tab.bind(entity, viewName);
		}
	}

	@Override
	public void setEnabled(boolean value) {
		this.mt.setEnabled(value);
		
		for (AutoFormTab tab : this.tabs) {
			tab.setEnabled(value);
		}
	}
	
	@Override
	public void applyChanges() {
		for (AutoFormTab tab : this.tabs) {
			tab.applyChanges();
		}
	}
	
	@Override
	public boolean isModified() {
		for (AutoFormTab tab : this.tabs) {
			if (tab.isModified()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void persist(JsonRPCTransaction transaction) {
		for (AutoFormTab tab : this.tabs) {
			tab.persist(transaction);
		}
	}
	
	@Override
	public boolean validate() {
		boolean ret = true;
		for (AutoFormTab tab : this.tabs) {
			ret = tab.validate() & ret;
		}
		return ret;
	}
}
