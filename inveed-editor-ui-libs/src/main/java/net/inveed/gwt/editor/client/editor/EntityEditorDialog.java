package net.inveed.gwt.editor.client.editor;

import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.constants.DialogType;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialDialog;
import gwt.material.design.client.ui.MaterialDialogContent;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialToast;
import net.inveed.gwt.editor.client.ProgressBarController;
import net.inveed.gwt.editor.client.RootContainer;
import net.inveed.gwt.editor.client.editor.auto.AutoEntityEditorForm;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor.ValueChangeListener;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;

public class EntityEditorDialog {
	interface EntityEditorDialogBinder extends UiBinder<Widget, EntityEditorDialog> {}
	
	private static final Logger LOG = Logger.getLogger(EntityEditorDialog.class.getName());
	private static final EntityEditorDialogBinder uiBinder = GWT.create(EntityEditorDialogBinder.class);
	
	private final JSEntity entity;
	private AbstractEntityEditorForm enityEditorForm;
	
	private PromiseImpl<Boolean, IError> closePromise;
	
	@UiField protected MaterialButton btnCancel;
	@UiField protected MaterialButton btnOk;
	@UiField protected MaterialDialog modal;
	@UiField protected MaterialDialogContent dc;
	@UiField protected MaterialPanel content;
	
	protected EditorTitle title;
	
	private boolean isNewEntity;
	
	private String viewEditName;
	private String viewName;
	
	public EntityEditorDialog(JSEntity entity, String viewName) {
		uiBinder.createAndBindUi(this);
		this.viewName = viewName;
		this.title = new EditorTitle();
		dc.insert(this.title, 0);
		this.entity = entity;
		this.modal.setType(DialogType.FIXED_FOOTER);
		this.content.setTextAlign(TextAlign.LEFT);
	}
	
	public void validate() {
		if (this.enityEditorForm.validate()) {
			this.btnOk.setEnabled(true);
		} else {
			this.btnOk.setEnabled(false);
		}
	}

	@UiHandler("btnOk")
	protected void onOkClick(ClickEvent evt) {
		LOG.info("OK button clicked");
		
		if (!this.enityEditorForm.validate()) {
			this.validate();
			return;
		}

		this.enityEditorForm.applyChanges();
		JsonRPCTransaction tran = new JsonRPCTransaction();
		this.enityEditorForm.persist(tran);
		
		tran.commit().thenApply((JsonRPCTransactionResponse)->{
			this.modal.close();
			this.modal.removeFromParent();
			
			if (this.isNewEntity && this.getViewEditName() != null) {
				EntityEditorDialog editDialog = new EntityEditorDialog(this.entity, this.getViewEditName());
				Promise<Boolean, IError> p = editDialog.show();
				p.thenApply((v)->{
					closePromise.complete(true);
					return null;
				});
				p.onError((e,t)->{
					closePromise.complete(true);
					return null;
				});
			} else if (closePromise != null) {
				closePromise.complete(true);
			}
			return null;
		}).onError((IError e, Throwable t) -> {
			MaterialToast.fireToast("Cannot save", 3000);
			return null;
		});
	}
	
	@UiHandler("btnCancel")
	protected void onCancelClick(ClickEvent evt) {
		LOG.info("Cancel button clicked");
		this.modal.close();
		this.modal.removeFromParent();
		
		if (closePromise == null) {
			return;
		}
		closePromise.complete(false);
	}
	
	public static AbstractEntityEditorForm getFormWidget(EntityModel model, String viewName) {
		assert(model != null);
		assert(viewName != null);
		
		viewName = viewName.trim();
		
		LOG.fine("Creating form creator...");
		IEntityEditorFactory formFactory = EntityEditorRegistry.INSTANCE.getFactory(model.getEntityName());

		if (formFactory != null) {
			LOG.fine("Instantiating form with factory...");
			AbstractEntityEditorForm ret = formFactory.create(viewName);
			if (ret != null) {
				return ret;
			}
		}
		LOG.fine("Editor not found. Trying to build auto editor");
		if (model.getEditorsDTO() == null) {
			LOG.warning("Auto editors not defined for entity " + model.getEntityName());
			return null;
		}
		AutoFormViewDTO edto = model.getEditorsDTO().get(viewName);
		if (edto == null) {
			LOG.warning("Auto editor not defined for entity " + model.getEntityName() + " view '" + viewName + "'");
			edto = model.getEditorsDTO().get(UIConstants.VIEWS_ALL);
			if (edto == null) {
				LOG.warning("Auto editor not defined for entity " + model.getEntityName() + " default view");
				return null;
			}
		}
		return new AutoEntityEditorForm(edto, model);
	}
	
	private void buildFormAndShow() {
		if (!isNewEntity) {
			LOG.info(this.entity.getModel() + "' with ID '" + this.entity.getID() + "'");
			
			this.title.setTitle(this.entity.getModel().getDisplayName(viewName) 
					+ " [#" + this.entity.getID().getDisplayValue() + "] "
					+ this.entity.getDisplayValue());
		} else {
			LOG.info("Creating new entity");
			this.title.setTitle("NEW: " + this.entity.getModel().getDisplayName(viewName)); //TODO: локализовать строки!!!
		}

		AbstractEntityEditorForm form = getFormWidget(this.entity.getModel(), viewName);
		if (form == null) {
			LOG.warning("Cannot create form");
			return;
		}
		
		LOG.fine("Form build successfully.");
		this.content.clear();
		this.content.add(form);
		
		int height = 500;
		if (form.getRequestedHeight() != null) {
			//this.modal.setType(ModalType.DEFAULT);
			height = form.getRequestedHeight() + 30;
		} 
		this.modal.setHeight((height + 160) + "px");
		//this.content.setHeight(height + "px");
		if (form.getRequestedWidth() != null) {
			//this.modal.setType(ModalType.DEFAULT);
			this.modal.setWidth((form.getRequestedWidth() + 5) + "px");
		}
		
		LOG.fine("Binding form");
		form.bind(entity);
				
		
		form.addValueChangedListener(new ValueChangeListener() {
			
			@Override
			public void onValueChanged() {
				EntityEditorDialog.this.validate();
			}
		});
		
		LOG.fine("Opening modal window");
		this.enityEditorForm = form;
		
		this.modal.open();

		this.validate();
	}
	public Promise<Boolean, IError> show() {
		RootContainer.INSTANCE.modalContainer.add(this.modal);
		this.btnOk.setEnabled(false);
		this.isNewEntity = (this.entity == null ? true : this.entity.getID() == null);
		if (this.isNewEntity) {
			this.buildFormAndShow();
		} else {
			
			Promise<Void, IError> p = this.entity.load();
			ProgressBarController.INSTANCE.add(p);
			p.thenApply((Void) -> {
				try {
					this.buildFormAndShow();
				} finally {
					ProgressBarController.INSTANCE.remove(p);
				}
				return null;
			});
			p.onError((IError e, Throwable t) -> {
				ProgressBarController.INSTANCE.remove(p);
				LOG.severe("Cannot load entity with ID " + this.entity.getID());
				return null;
			});
		}
		PromiseImpl<Boolean, IError> ret = new PromiseImpl<>();
		this.closePromise = ret;
		return ret;
	}

	public String getViewEditName() {
		return viewEditName;
	}

	public void setViewEditName(String viewEditName) {
		if (viewEditName == null) {
			return;
		}
		if (!reopenAfterCreate(this.viewName, viewEditName, this.entity.getModel())) {
			return;
		}
		this.viewEditName = viewEditName;
	}
	
	private static boolean reopenAfterCreate(String createView, String editView, EntityModel model) {
		if (createView.equals(editView)) {
			return false;
		}
		IEntityEditorFactory efactory = EntityEditorRegistry.INSTANCE.getFactory(model.getEntityName());
		if (efactory != null) {
			return efactory.reopenAfterCreate(createView, editView, model);
		}
		if (model.getEditorsDTO() == null) {
			return false;
		}
		AutoFormViewDTO cdto = model.getEditorsDTO().get(createView);
		if (cdto == null) {
			cdto =  model.getEditorsDTO().get(UIConstants.VIEWS_ALL);
		}
		AutoFormViewDTO edto = model.getEditorsDTO().get(editView);
		if (edto == null) {
			edto =  model.getEditorsDTO().get(UIConstants.VIEWS_ALL);
		}
		if (edto == null) {
			return false;
		}
		if (cdto == null) {
			return false;
		}
		/*if (cdto.viewName.equals(edto.viewName)) {
			return false;
		}*/
		return !cdto.equalsTo(edto);
	}
}
