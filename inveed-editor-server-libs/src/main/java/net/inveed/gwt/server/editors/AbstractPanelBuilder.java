package net.inveed.gwt.server.editors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.inveed.gwt.editor.commons.FormFieldLocation;
import net.inveed.gwt.editor.shared.forms.EditorFieldDTO;
import net.inveed.gwt.editor.shared.forms.panels.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorFieldsRowDTO;
import net.inveed.gwt.editor.shared.forms.rows.EditorListRowDTO;
import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;
import net.inveed.gwt.server.propbuilders.EntityListPropertyBuilder;

public abstract class AbstractPanelBuilder<T extends Annotation> {
	public final String name;
	public final String parentName;
	public final T annotation;
	public final int order;

	public final List<AbstractPanelBuilder<?>> children;
	public AbstractPanelBuilder<?> parent;
	
	public abstract AbstractPanelBuilder<?> getSection(String name);

	public AbstractPanelBuilder(String name, String parent, T annotation, int order) {
		this.name = name;
		this.parentName = parent;
		this.annotation = annotation;
		this.parent = null;
		this.order = order;
		this.children = new ArrayList<>();
	}
	
	protected IEditorRowDTO[] buildRows(String viewName, Map<String, FieldInView> fields) {
		ArrayList<FieldInView> myFields = new ArrayList<>();
		for (FieldInView f : fields.values()) {
			if (f.container == this) {
				myFields.add(f);
			}
		}
		if (myFields.size() == 0 && children.size() == 0) {
			return null;
		}
		ArrayList<IEditorRowDTO> globalRows = new ArrayList<>();
		if (myFields.size() > 0) {
			IEditorRowDTO[] fieldRows = getFieldRows(myFields);
			if (children.size() == 0) {
				return fieldRows;
			}
			EditorSectionDTO fakeSection = new EditorSectionDTO(fieldRows, null);
			globalRows.add(fakeSection);
		}

		if (this.children.size() == 0 && globalRows.size() == 0) {
			AbstractPanelBuilder<?> child = this.children.get(0);
			if (child instanceof AutoFormSectionPanelBuilder) {
				// Single child section, no own fields – no reason to make one more container.
				return child.buildRows(viewName, fields);
			}
		}
		for (AbstractPanelBuilder<?> child : this.children) {
			IEditorRowDTO row = child.buildRow(viewName, fields);
			globalRows.add(row);
		}
		if (globalRows.size() == 0) {
			return null;
		}
		return globalRows.toArray(new IEditorRowDTO[0]);
	}
	
	private IEditorRowDTO[] getFieldRows(ArrayList<FieldInView> fields) {
		fields.sort(new Comparator<FieldInView>() {
			@Override
			public int compare(FieldInView o1, FieldInView o2) {
				int ret = Integer.compare(o1.annotation.order(), o2.annotation.order());
				if (ret != 0) {
					return ret;
				}
				if (o1.annotation.location() == FormFieldLocation.LEFT) {
					return -1;
				}
				return 0;
			}
		});
		
		ArrayList<IEditorRowDTO> rows = new ArrayList<>();
		EditorFieldDTO pendingField = null;
		for (FieldInView f : fields) {
			if (f.builder instanceof EntityListPropertyBuilder) {
				EditorListRowDTO rdto = new EditorListRowDTO(f.builder.getPropertyName());
				rows.add(rdto);
			} else if (f.annotation.location() == FormFieldLocation.BOTH) {
				if (pendingField != null) {
					EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{pendingField});
					rows.add(rdto);
					pendingField = null;
				}
				EditorFieldDTO fld = getFieldDTO(f);
				EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{fld});
				rows.add(rdto);
			} else if (f.annotation.location() == FormFieldLocation.LEFT) {
				if (pendingField != null) {
					EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{pendingField});
					rows.add(rdto);
				}
				pendingField = getFieldDTO(f);
			} else if (f.annotation.location() == FormFieldLocation.RIGHT) {
				EditorFieldDTO fld = getFieldDTO(f);
				if (pendingField != null) {
					EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{pendingField, fld});
					rows.add(rdto);
					pendingField = null;
				} else {
					EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{fld});
					rows.add(rdto);
				}
			}
		}
		if (pendingField != null) {
			EditorFieldsRowDTO rdto = new EditorFieldsRowDTO(new EditorFieldDTO[]{pendingField});
			rows.add(rdto);
		}
		return rows.toArray(new IEditorRowDTO[0]);
	}
	
	private static final EditorFieldDTO getFieldDTO(FieldInView af) {
		return new EditorFieldDTO(af.name, null, af.annotation.readonly());
	}
	protected abstract IEditorRowDTO buildRow(String viewName, Map<String, FieldInView> fields);
}
