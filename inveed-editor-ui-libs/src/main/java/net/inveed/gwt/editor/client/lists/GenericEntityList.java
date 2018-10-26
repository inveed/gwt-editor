package net.inveed.gwt.editor.client.lists;

import java.util.logging.Logger;

import net.inveed.gwt.editor.client.ProgressBarController;
import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.types.JSEntityList;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.PromiseImpl;
import net.inveed.gwt.editor.commons.UIConstants;

public class GenericEntityList extends EntityList {
	private static final Logger LOG = Logger.getLogger(GenericEntityList.class.getName());
	
	private EntityManager entityManager;
	
	public void bind(EntityModel model, String viewName, EntityManager em) {
		super.bind(model, em, viewName);
		this.entityManager = em;
		this.setTableTitle(model.getPluralDisplayName(viewName), "1.5em");
	}
	
	@Override
	public void initialize() {
		super.initialize();
	}
	
	@Override
	protected void refresh() {
		this.load();
	}
	public Promise<Void, IError> load() {
		LOG.fine("Loading data...");
		PromiseImpl<Void, IError> ret = new PromiseImpl<>();
		Promise<JSEntityList, IError> promise = this.entityManager.listEntities(this.getEntityModel(), 0, Integer.MAX_VALUE, null);
		ProgressBarController.INSTANCE.add(promise);
		promise.thenApply((JSEntityList l)->{
			try {
				LOG.fine("Got list response with " + l.getValue().size() + " items");
				this.fill(l.getValue());
				ret.complete(null);
			} finally {
				ProgressBarController.INSTANCE.remove(promise);
			}
			return null;
		});
		promise.onError((IError e, Throwable t) -> {
			LOG.fine("Got error response with " + e + " error");
			ProgressBarController.INSTANCE.remove(promise);
			ret.error(e, t);
			return null;
		});
		LOG.fine("Waiting for result");
		return ret;
	}
	
	protected void openNewItemEditor(EntityModel model) {
		JSEntity entity = new JSEntity(model, this.entityManager);
		EntityEditorDialog dialog = new EntityEditorDialog(entity, UIConstants.FORM_CREATE);
		dialog.setViewEditName(UIConstants.FORM_EDIT);
		Promise<Boolean, IError> p = dialog.show();
		p.thenApply((Boolean v) -> {
			if (v != null) {
				if (v) {
					this.refresh();
				}
			}
			return null;
		});
	}	
}
