package net.inveed.gwt.editor.client.lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.data.events.RowDoubleClickEvent;
import gwt.material.design.client.data.events.RowDoubleClickHandler;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialDropDown;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialToast;
import gwt.material.design.client.ui.table.MaterialDataTable;
import gwt.material.design.client.ui.table.cell.Column;
import gwt.material.design.client.ui.table.cell.WidgetColumn;
import net.inveed.gwt.editor.client.UIRegistry;
import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.editor.EntityListView;
import net.inveed.gwt.editor.client.editor.EntityListView.ListViewColumn;
import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCRequest.RequestResult;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction.TransactionResult;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.QuestionWindow;
import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.editor.shared.lists.ListViewDTO;

public abstract class EntityList extends Composite {
	interface EntityListBinder extends UiBinder<Widget, EntityList> {
	}

	private static final EntityListBinder uiBinder = GWT.create(EntityListBinder.class);

	private static final Logger LOG = Logger.getLogger(EntityList.class.getName());
	private final HashMap<String, String> excludedProperties = new HashMap<>();

	private EntityModel model;
	private EntityListView view;
	private EntityManager entityManager;
	
	//MaterialIcon btnAdd;
	//MaterialIcon btnReload;
	@UiField protected MaterialDataTable<JSEntity> grid;
	
	private MaterialPanel toolPanel;
	private MaterialPanel infoPanel;
	private MaterialLabel title;
	
	public EntityListView getView() {
		return this.view;
	}

	public EntityModel getEntityModel() {
		return this.model;
	}

	public EntityList() {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.grid.addRowDoubleClickHandler(new RowDoubleClickHandler<JSEntity>() {
			
			@Override
			public void onRowDoubleClick(RowDoubleClickEvent<JSEntity> event) {
				if (event.getModel() == null) {
					return;
				}
				openExistingItemEditor(event.getModel());
			}
		});
		this.grid.setShadow(0);
		this.grid.getScaffolding().getInfoPanel().clear();
		this.grid.getScaffolding().getTopPanel().clear();
		this.toolPanel = new MaterialPanel();
		this.toolPanel.setStyleName("tool-panel");
		
		this.infoPanel = new MaterialPanel();
		this.infoPanel.setStyleName("info-panel");
		
		this.title = new MaterialLabel();
		this.infoPanel.add(this.title);
		
		this.grid.getScaffolding().getTopPanel().add(this.infoPanel);
		this.grid.getScaffolding().getTopPanel().add(this.toolPanel);
	}
	
	public void setAbsolutHeight(int h) {
		this.grid.getScaffolding().getTopPanel().setHeight("40px");
		this.grid.getScaffolding().getTopPanel().getElement().getStyle().setPaddingTop(8, Unit.PX);
		this.grid.getScaffolding().getTopPanel().getElement().getStyle().setPaddingBottom(3, Unit.PX);
		
		this.grid.getElement().getStyle().setHeight((h + 40), Unit.PX);
		this.grid.getScaffolding().getTableBody().setHeight(h + "px");
	}
	
	public void setShadow(int v) {
		this.grid.setShadow(v);
	}
	
	protected void setTableTitle(String displayName) {
		this.setTableTitle(displayName, "1.2em");
	}
	
	protected void setTableTitle(String displayName, String size) {
		this.title.setText(displayName);
		this.title.setFontSize(size);
	}

	
	public void bind(EntityModel model, EntityManager em, String viewName) {
		this.model = model;
		this.entityManager = em;
		if (model.getListViews() == null) {
			throw new NullPointerException("listViews");
		}
		ListViewDTO dto = model.getListViews().get(viewName);
		if (dto == null) {
			dto = model.getListViews().get(UIConstants.VIEWS_ALL);
		}
		if (dto == null) {
			//TODO: error
			return;
		}
		this.view = new EntityListView(model, dto, viewName);
		addCreateIcon();
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void excludeProperty(String pname) {
		this.excludedProperties.put(pname, pname);
	}

	public void includeProperty(String pname) {
		this.excludedProperties.remove(pname, pname);
	}

	public void initialize() {
		LOG.fine("Initializing entity list");
		ArrayList<ListViewColumn<?>> displayColumns = new ArrayList<>();

		for (ListViewColumn<?> col : view.getColumns()) {
			if (this.excludedProperties.containsKey(col.getPropertyDescriptor().getName())) {
				continue;
			}
			displayColumns.add(col); // TODO: добавлять только если должны отображаться в таблице
		}

		this.addLeftSideColumns();

		for (ListViewColumn<?> c : displayColumns) {
			LOG.fine("Adding column for field " + c.getPropertyDescriptor().getName());

			Column<JSEntity, ?> col = UIRegistry.INSTANCE.createListViewColumn(c);
			if (c.getDto().width != null && c.getDto().width > 0) {
				// col.setMinimumWidth(f.width);
			}
			String colTitle = c.getDisplayName();

			LOG.fine("Adding column with name " + colTitle);
			this.grid.addColumn(col, colTitle);
		}

		this.addRightSideColumns();
		this.grid.getView().setRedraw(true);
		this.grid.getView().refresh();
	}

	protected void addRightSideColumns() {
		WidgetColumn<JSEntity, Widget> deleteColumn = new WidgetColumn<JSEntity, Widget>() {
			
			@Override
			public Widget getValue(JSEntity object) {
				MaterialButton btn = new MaterialButton(ButtonType.FLAT);
				btn.setIconType(IconType.DELETE);
				btn.setBorder("none");
				btn.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						deleteSelectedItem(object);
					}
				});
				return btn;
			}
		};
		deleteColumn.setWidth("40px");
		this.grid.addColumn(deleteColumn);
	}

	protected void addLeftSideColumns() {

	}

	private void deleteSelectedItem(JSEntity item) {
		if (item == null) {
			return;
		}
		if (!item.canDelete()) {
			String msg = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1("errors.cannotDelete"));
			MaterialToast.fireToast(msg, 2000);
			return;
		}

		Promise<Boolean, IError> p = QuestionWindow
				.open("Delete object", 
						"You're going to delete '" + item.getDisplayValue() + "'. \r\nContinue?",
						Color.RED_LIGHTEN_3);
		
		p.thenApply((response) -> {
			if (response) {
				this.deleteSelectedItemsConfirmed(item);
			}
			return null;
		});
	}

	private void addCreateIcon() {
		
		MaterialIcon btnAdd = new MaterialIcon(IconType.ADD);
		this.toolPanel.add(btnAdd);
		
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
		} else {
			MaterialDropDown dd = new MaterialDropDown();
			dd.setConstrainWidth(false);
			String uid = DOM.createUniqueId();
			btnAdd.setActivates(uid);
			dd.setActivator(uid);
			
			for (EntityModel vm : validTypes) {
				MaterialLink i = new MaterialLink(vm.getDisplayName(null));
				i.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						openNewItemEditor(vm);
					}
				});
				dd.add(i);
			}
			this.toolPanel.add(dd);

		}
		MaterialIcon btnReload = new MaterialIcon(IconType.REFRESH);
		this.toolPanel.add(btnReload);
		
		btnReload.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
	}

	private void deleteSelectedItemsConfirmed(JSEntity item) {
		JsonRPCTransaction tran = new JsonRPCTransaction();

		item.delete(tran);

		Promise<TransactionResult, IError> p = tran.commit();
		p.thenApply((TransactionResult r) -> {
			this.onDeleteComplete();
			return null;
		});

		p.onError((IError err, Throwable t) -> {
			this.onDeleteError(err, item);
			return null;
		});
	}

	private void onDeleteError(IError err, JSEntity item) {
		if (err.getType() == JsonRPCTransaction.TransactionError.TYPE) {
			JsonRPCTransaction.TransactionError e = (JsonRPCTransaction.TransactionError) err;
			List<JSEntity> problemEntities = new ArrayList<>();
			for (RequestResult ri : e.getResponses()) {
				if (ri.error == null) {
					continue;
				}
				if (ri.request.getContext() == null) {
					continue;
				}
				problemEntities.add((JSEntity) ri.request.getContext());
			}
		}
		MaterialToast.fireToast("Cannot delete object");
	}

	protected void onDeleteComplete() {
		MaterialToast.fireToast("Object deleted");
		this.refresh();
	}

	protected void openExistingItemEditor(JSEntity entity) {
		EntityEditorDialog dialog = new EntityEditorDialog(entity, UIConstants.FORM_EDIT);
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

	protected abstract void refresh();

	protected abstract void openNewItemEditor(EntityModel model);

	protected Void fill(List<JSEntity> list) {
		LOG.fine("Array found, size = " + list.size());
		
		//this.dataProvider = new ListDataSource<>();
		//this.dataProvider.add(0, list);
		this.grid.clearRowsAndCategories(true);
		this.grid.setTotalRows(0);
		//this.grid.setDataSource(this.dataProvider);
		this.grid.setRowData(0, list);
		this.grid.getView().setRedraw(true);
		this.grid.getView().refresh();
		
		//this.pagination.rebuild(this.pager);

		LOG.fine("Fill finished");
		return null;
	}

}
