package net.inveed.gwt.editor.client.lists;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.ButtonGroup;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Pagination;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;
import org.gwtbootstrap3.client.ui.gwt.CellTable;
import org.gwtbootstrap3.extras.notify.client.constants.NotifyType;
import org.gwtbootstrap3.extras.notify.client.ui.Notify;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.RangeChangeEvent;

import net.inveed.gwt.editor.client.editor.EntityEditorDialog;
import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCRequest.RequestResult;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction;
import net.inveed.gwt.editor.client.jsonrpc.JsonRPCTransaction.TransactionResult;
import net.inveed.gwt.editor.client.model.EntityListView;
import net.inveed.gwt.editor.client.model.EntityModel;
import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.model.EntityListView.PropertyInView;
import net.inveed.gwt.editor.client.model.EntityManager;
import net.inveed.gwt.editor.client.utils.ErrorWindow;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.client.utils.Promise;
import net.inveed.gwt.editor.client.utils.QuestionWindow;
import net.inveed.gwt.editor.shared.UIConstants;

public abstract class EntityList extends Composite {
	interface EntityListBinder extends UiBinder<Widget, EntityList> {
	}

	private static final EntityListBinder uiBinder = GWT.create(EntityListBinder.class);

	private static final Logger LOG = Logger.getLogger(EntityList.class.getName());
	private final HashMap<String, String> excludedProperties = new HashMap<>();

	private EntityModel model;
	private EntityListView view;
	private EntityManager entityManager;

	ListDataProvider<JSEntity> dataProvider;

	@UiField Heading title;
	@UiField Button btnAdd;
	@UiField Button btnReload;
	@UiField Pagination pagination;
	@UiField ButtonGroup bgRight;
	@UiField protected CellTable<JSEntity> grid;
	
	private SimplePager pager = new SimplePager();

	public EntityListView getView() {
		return this.view;
	}

	public EntityModel getEntityModel() {
		return this.model;
	}

	public EntityList() {
		initWidget(uiBinder.createAndBindUi(this));
		this.dataProvider = new ListDataProvider<>();
		this.grid.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(final RangeChangeEvent event) {
				pagination.rebuild(pager);
			}
		});

		this.pager.setDisplay(this.grid);
		this.pagination.clear();
		this.grid.setPageSize(20);
	}
	
	public void setPageSize(int size) {
		this.grid.setPageSize(size);
	}
	
	public int getPageSize() {
		return this.grid.getPageSize();
	}

	public void bind(EntityModel model, EntityManager em, String viewName) {
		this.model = model;
		this.entityManager = em;
		this.view = model.getListView(viewName);
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
		ArrayList<PropertyInView> displayFields = new ArrayList<>();

		for (PropertyInView f : view.getProperties()) {
			if (this.excludedProperties.containsKey(f.property.getName())) {
				continue;
			}
			displayFields.add(f); // TODO: добавлять только если должны отображаться в таблице
		}

		displayFields.sort(new Comparator<PropertyInView>() {

			@Override
			public int compare(PropertyInView o1, PropertyInView o2) {
				int o1Order = o1.order;
				int o2Order = o2.order;
				if (o1Order < o2Order) {
					return -1;
				} else if (o1Order > o2Order) {
					return 1;
				}
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});

		this.addLeftSideColumns();

		for (PropertyInView f : displayFields) {
			LOG.fine("Adding column for field " + f.property.getName());

			Column<JSEntity, ?> col = f.property.createTableColumn();
			if (f.width > 0) {
				// col.setMinimumWidth(f.width);
			}
			String colName = f.getDisplayName();

			LOG.fine("Adding column with name " + colName);
			this.grid.addColumn(col, colName);
		}

		this.addRightSideColumns();

		this.dataProvider.addDataDisplay(this.grid);
	}

	protected void addRightSideColumns() {

		Column<JSEntity, String> colEdit = new Column<JSEntity, String>(
				new ButtonCell(ButtonType.DEFAULT, IconType.EDIT)) {

			@Override
			public String getValue(JSEntity object) {
				return null;
			}

			@Override
			public void render(Context context, JSEntity entity, SafeHtmlBuilder sb) {
				if (!entity.canEdit()) {
					sb.appendHtmlConstant("<div/>");
				}
				super.render(context, entity, sb);
			}
		};
		colEdit.setCellStyleNames("grid-cell-btn");

		colEdit.setFieldUpdater(new FieldUpdater<JSEntity, String>() {

			@Override
			public void update(int index, JSEntity object, String value) {
				openExistingItemEditor(object);
			}
		});

		Column<JSEntity, String> colDelete = new Column<JSEntity, String>(
				new ButtonCell(ButtonType.DEFAULT, IconType.RECYCLE)) {

			@Override
			public String getValue(JSEntity object) {
				return null;
			}

			@Override
			public void render(Context context, JSEntity entity, SafeHtmlBuilder sb) {
				if (!entity.canDelete()) {
					sb.appendHtmlConstant("<div/>");
				}
				super.render(context, entity, sb);
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<JSEntity, String>() {

			@Override
			public void update(int index, JSEntity object, String value) {
				deleteSelectedItem(object);
			}
		});

		colDelete.setCellStyleNames("grid-cell-btn");

		this.grid.addColumn(colEdit);
		this.grid.addColumn(colDelete);
	}

	protected void addLeftSideColumns() {

	}

	private void deleteSelectedItem(JSEntity item) {
		if (item == null) {
			return;
		}
		if (!item.canDelete()) {
			String msg = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage("errors.cannotDelete"));
			Notify.notify(msg, NotifyType.DANGER);
			return;
		}

		Promise<Boolean, IError> p = QuestionWindow
				.open("You're going to delete '" + item.getDisplayValue() + "'. \r\nContinue?");
		p.thenApply((response) -> {
			if (response) {
				this.deleteSelectedItemsConfirmed(item);
			}
			return null;
		});
	}

	private void addCreateIcon() {

		List<EntityModel> validTypes = this.model.getInstantiableTypes();
		if (validTypes.size() == 0) {
			return;
		}

		if (validTypes.size() == 1) {
			this.btnAdd.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					openNewItemEditor(validTypes.get(0));
				}
			});
		} else {
			this.btnAdd.removeFromParent();
			ButtonGroup ddg = new ButtonGroup();
			bgRight.insert(ddg, 0);
			ddg.add(this.btnAdd);
			this.btnAdd.setDataToggle(Toggle.DROPDOWN);
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

		ErrorWindow.open("Cannot delete items");
	}

	protected void onDeleteComplete() {
		Notify.notify("Item deleted");
		this.refresh();
	}

	protected void openExistingItemEditor(JSEntity entity) {
		EntityEditorDialog dialog = new EntityEditorDialog(entity);
		Promise<Boolean, IError> p = dialog.show(UIConstants.FORM_EDIT);
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

		this.dataProvider.getList().clear();
		this.dataProvider.getList().addAll(list);
		this.dataProvider.flush();
		
		this.pagination.rebuild(this.pager);

		LOG.fine("Fill finished");
		return null;
	}

}
