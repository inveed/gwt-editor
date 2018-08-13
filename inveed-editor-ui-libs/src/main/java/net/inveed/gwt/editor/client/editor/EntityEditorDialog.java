package net.inveed.gwt.editor.client.editor;

import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import net.inveed.gwt.editor.client.ProgressBarController;
import net.inveed.gwt.editor.client.RootContainer;
import net.inveed.gwt.editor.client.editor.auto.AutoEntityEditorForm;
import net.inveed.gwt.editor.client.editor.fields.AbstractPropertyEditor.ValueChangeListener;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction.TransactionResult;
import net.inveed.gwt.editor.client.model.EntityFormView;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;

public class EntityEditorDialog {
	interface EntityEditorDialogBinder extends UiBinder<Widget, EntityEditorDialog> {}
	
	private static final Logger LOG = Logger.getLogger(EntityEditorDialog.class.getName());
	private static final EntityEditorDialogBinder uiBinder = GWT.create(EntityEditorDialogBinder.class);
	
	private final JSEntity entity;
	private AbstractEntityEditorForm enityEditorForm;
	
	private PromiseImpl<Boolean, IError> closePromise;
	
	@UiField protected Button btnCancel;
	@UiField protected Button btnOk;
	@UiField protected Modal modal;
	@UiField protected ModalBody content;
	
	public EntityEditorDialog(JSEntity entity) {
		uiBinder.createAndBindUi(this);
		this.entity = entity;
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
			this.modal.hide();
			this.modal.removeFromParent();
			
			if (closePromise != null) {
				closePromise.complete(true);
			}
			return null;
		}).onError((IError e, Throwable t) -> {
			this.modal.hide();
			this.modal.removeFromParent();
			
			if (closePromise != null) {
				closePromise.complete(false);
			}
			return null;
		});
	}
	
	@UiHandler("btnCancel")
	protected void onCancelClick(ClickEvent evt) {
		LOG.info("Cancel button clicked");
		this.modal.hide();
		this.modal.removeFromParent();
		
		if (closePromise == null) {
			return;
		}
		closePromise.complete(false);
	}
	
	public static AbstractEntityEditorForm getFormWidget(EntityModel model, EntityFormView view) {
		assert(model != null);
		assert(view != null);

		LOG.fine("Creating form creator...");
		IEntityEditorFactory formFactory = EntityEditorRegistry.INSTANCE.getFactory(model.getEntityName());

		if (formFactory != null) {
			LOG.fine("Instantiating form with factory...");
			AbstractEntityEditorForm ret = formFactory.create(view);
			if (ret != null) {
				return ret;
			}
		} 

		LOG.fine("Using default form");			
		return new AutoEntityEditorForm(view);
	}
	
	private void buildFormAndShow(String viewName, boolean isNewEntity) {
		EntityFormView view = this.entity.getModel().getFormView(viewName);
		if (view == null) {
			LOG.warning("Cannot find view with name " + viewName);
			return;
		}
		
		if (!isNewEntity) {
			LOG.info("Editing entity '" + this.entity.getModel() + "' with ID '" + this.entity.getID() + "'");
			
			this.modal.setTitle("Editing " //TODO: локализовать строки!!!
					+ this.entity.getModel().getDisplayName(viewName) 
					+ ": " 
					+ this.entity.getDisplayValue());
			
		} else {
			LOG.info("Creating new entity");
			this.modal.setTitle("Creating new:" + this.entity.getModel().getDisplayName(viewName)); //TODO: локализовать строки!!!
		
		}

		AbstractEntityEditorForm form = getFormWidget(this.entity.getModel(), view);
		if (form == null) {
			LOG.warning("Cannot create form");
			return;
		}
		
		LOG.fine("Form build successfully.");
		this.content.clear();
		this.content.add(form);
		
		if (form.getRequestedHeight() != null) {
			//this.modal.setType(ModalType.DEFAULT);
			this.modal.setHeight((form.getRequestedHeight() + 30) + "");
		}
		if (form.getRequestedWidth() != null) {
			//this.modal.setType(ModalType.DEFAULT);
			this.modal.setWidth((form.getRequestedWidth() + 5) + "");
		}
		
		LOG.fine("Binding form");
		form.bind(entity);
				
		LOG.fine("Opening modal window");
		
		form.addValueChangedListener(new ValueChangeListener() {
			
			@Override
			public void onValueChanged() {
				EntityEditorDialog.this.validate();
			}
		});
		
		this.enityEditorForm = form;
		
		int maxHeigh = Math.max((int) ((double) Window.getClientHeight() * 0.95D) - 150, 500);
		this.content.getElement().getStyle().setProperty("maxHeight", maxHeigh, Unit.PX);
		this.modal.show();
		this.validate();
	}
	public Promise<Boolean, IError> show(String viewName) {
		RootContainer.INSTANCE.modalContainer.add(this.modal);
		
		this.btnOk.setEnabled(false);
		boolean isNew = (this.entity == null ? true : this.entity.getID() == null);
		if (isNew) {
			this.buildFormAndShow(viewName, isNew);
		} else {
			
			Promise<Void, IError> p = this.entity.load();
			ProgressBarController.INSTANCE.add(p);
			p.thenApply((Void) -> {
				try {
					this.buildFormAndShow(viewName, isNew);
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
}
