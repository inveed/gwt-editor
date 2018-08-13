package net.inveed.gwt.editor.client.editor.fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ButtonSize;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.constants.HeadingSize;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.html.Div;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.editor.AbstractEntityEditorForm;
import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityFormView;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.shared.UIConstants;

public class InPlaceEntitiesListEditor extends AbstractPropertyEditor<LinkedEntitiesListPropertyModel, JSEntityList> {
	private static final Logger LOG = Logger.getLogger(InPlaceEntitiesListEditor.class.getName());
	
	private Div container;
	private ButtonGroup bgRight;
	private PanelGroup pg;
	private String pgId;
	private Heading lblTitle;
	
	private JSEntityList value;
	
	private JSEntity entity;
	private EntityModel model;
	private LinkedEntitiesListPropertyModel property;
	
	private HashMap<String,AbstractEntityEditorForm> editors;
	
	public InPlaceEntitiesListEditor() {
		this.editors = new HashMap<>();
		this.pgId = "pg" + UUID.randomUUID().toString().replaceAll("-", "");
		this.container = new Div();
		Row r1 = new Row();
		Column r1col1 = new Column(ColumnSize.LG_11);
		Column r1col2 = new Column(ColumnSize.LG_1);
		r1.add(r1col1);
		r1.add(r1col2);
		
		this.lblTitle = new Heading(HeadingSize.H4);
		r1col1.add(lblTitle);
		
		this.bgRight = new ButtonGroup();
		r1col2.add(bgRight);
	
		Row r2 = new Row();
		Column r2c1 = new Column(ColumnSize.LG_12);
		this.pg = new PanelGroup();
		this.pg.setId(pgId);
		r2c1.add(pg);
		r2.add(r2c1);
		
		this.container.add(r1);
		this.container.add(r2);
		this.add(this.container);
	}
	
	public void bind(JSEntity entity, LinkedEntitiesListPropertyModel field, String viewName) {
		super.bind(entity, field, viewName);
		this.entity = entity;
		this.model = field.getTargetEntityType();
		this.property = field;
		if (this.getOriginalValue() != null) {
			this.setValue(this.getOriginalValue());
		}
		
		this.addCreateIcon();
	}
	
	private void addCreateIcon() {
		Button btnAdd = new Button();
		btnAdd.setIcon(IconType.PLUS);
		btnAdd.setMarginBottom(5);
		btnAdd.setMarginTop(5);
		btnAdd.setSize(ButtonSize.SMALL);
		
		List<EntityModel> validTypes = this.model.getInstantiableTypes();
		if (validTypes.size() == 0) {
			return;
		}

		if (validTypes.size() == 1) {
			btnAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					openNewItemEditor(validTypes.get(0));
				}
			});
			
			this.bgRight.add(btnAdd);
		} else {
			ButtonGroup ddg = new ButtonGroup();
			bgRight.insert(ddg, 0);
			ddg.add(btnAdd);
			btnAdd.setDataToggle(Toggle.DROPDOWN);
			DropDownMenu ddm = new DropDownMenu();
			ddg.add(ddm);
			for (EntityModel vm : validTypes) {
				AnchorListItem i = new AnchorListItem(vm.getDisplayName(null));
				i.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						openNewItemEditor(vm);
					}
				});
				ddm.add(i);
			}
		}
	}

	protected void openNewItemEditor(EntityModel model) {
		JSEntity entity = new JSEntity(model, this.entity.getEntityManager());
		entity.setProperty(this.property.getMappedByProperty(), this.entity);
		EntityEditorDialog dialog = new EntityEditorDialog(entity);
		
		Promise<Boolean, IError> p = dialog.show(UIConstants.FORM_EMBEDDED_CREATE);
		p.thenApply((Boolean v) -> {
			if (v != null) {
				if (v) {
					this.getValue().add(entity);
					this.fill(this.getValue().getValue());
					//this.refresh();
				}
			}
			return null;
		});
	}

	
	@Override
	public void setTitle(String title) {
		this.lblTitle.setText(title);
	}
	
	@Override
	protected Widget getChildWidget() {
		return this.container;
	}

	@Override
	public void setValue(String v) {
	}
	
	@Override
	public boolean isModified() {
		for (AbstractEntityEditorForm e : this.editors.values()) {
			if (e.isModified()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void applyChanges() {
		for (AbstractEntityEditorForm e : this.editors.values()) {
			e.applyChanges();
		}
	}
	
	@Override
	public void save(JsonRPCTransaction transaction) {
		super.save(transaction);
		for (AbstractEntityEditorForm e : this.editors.values()) {
			e.persist(transaction);
		}
	}
	
	
	@Override
	public boolean validate() {
		for (AbstractEntityEditorForm e : this.editors.values()) {
			if (!e.validate()) {
				return false;
			}
		}
		return true;
	}
	
	private void fill(List<JSEntity> data) {
		this.pg.clear();
		for (JSEntity e : data) {
			if (e.getID() == null) {
				continue;
			}
			String id = e.getID().toString();
			
			
			AbstractEntityEditorForm f;
			if (this.editors.containsKey(id)) {
				f = this.editors.get(id);
			} else {
				EntityFormView v = e.getModel().getFormView(UIConstants.FORM_EMBEDDED_EDIT);
				if (v == null) {
					continue;
				}
				f = EntityEditorDialog.getFormWidget(e.getModel(), v);
				this.editors.put(id, f);
				if (e.isInitialized()) {
					f.bind(e);
				} else {
					//TODO: Delayed init
				}
			}
			
			Panel p = new Panel();
			PanelHeader ph = new PanelHeader();
			PanelCollapse pc = new PanelCollapse();
			p.add(ph);
			p.add(pc);
			
			Heading h = new Heading(HeadingSize.H5);
			h.setText(e.getDisplayValue());
			ph.add(h);
			
			ph.setDataTargetWidget(pc);
			ph.setDataParent(this.pgId);
			ph.setDataToggle(Toggle.COLLAPSE);
			
			PanelBody pb = new PanelBody();
			pc.add(pb);
			pb.add(f);
			
			this.pg.add(p);
		}
	}

	public void setValue(JSEntityList v) {
		this.value = v;
		if (v == null) {
			this.fill(new ArrayList<>());
		}
		
		LOG.fine("Setting non-null list");
		List<JSEntity> list = v.getValue();
		this.fill(list);
		LOG.fine("Fill finished");
	}
	@Override
	public JSEntityList getValue() {
		return this.value;
	}

	@Override
	public void setEnabled(boolean value) {
		for (AbstractEntityEditorForm e : this.editors.values()) {
			e.setEnabled(value);
		}
	}
}
