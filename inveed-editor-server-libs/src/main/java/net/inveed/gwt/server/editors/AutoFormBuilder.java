package net.inveed.gwt.server.editors;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.commons.reflection.BeanTypeDesc;
import net.inveed.gwt.editor.commons.UIConstants;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.server.EntityModelBuilder;
import net.inveed.gwt.server.annotations.editor.UIAutoEditorView;
import net.inveed.gwt.server.annotations.editor.UIAutoEditorViews;
import net.inveed.gwt.server.annotations.editor.UIEditorField;
import net.inveed.gwt.server.annotations.editor.UIEditorSection;
import net.inveed.gwt.server.annotations.editor.UIEditorSections;
import net.inveed.gwt.server.annotations.editor.UIEditorTabPanel;
import net.inveed.gwt.server.annotations.editor.UIEditorTabPanels;
import net.inveed.gwt.server.annotations.editor.UIEditorsField;
import net.inveed.gwt.server.propbuilders.IPropertyBuiler;

public class AutoFormBuilder {
	public static final class Field {
		public final String name;
		public final IPropertyBuiler<?> builder;
		public final BeanPropertyDesc property;
		public final HashMap<String,UIEditorField> annotations;
		
		public Field(String name,
				IPropertyBuiler<?> builder, 
				BeanPropertyDesc prop, 
				HashMap<String,UIEditorField> annotations) {
			this.name = name;
			this.builder = builder;
			this.property = prop;
			this.annotations = annotations;
		}

	}
	private BeanTypeDesc<?> bean;
	private HashMap<String, UIAutoEditorView> declaredViews;
	private AutoFormRootPanelBuilder rootBuilder;

	public AutoFormBuilder(BeanTypeDesc<?> bean) {
		this.bean = bean;
	}
	
	public HashMap<String, AutoFormViewDTO> build(EntityModelBuilder modelBuilder) {
		this.parse();
		
		ArrayList<String> foundViews = new ArrayList<>();
		HashMap<String, Field> fields = findAllFields(modelBuilder, foundViews);
		HashMap<String, AutoFormViewDTO> ret = new HashMap<>();
		HashMap<String, AutoFormViewBuilder> builders = new HashMap<>();
		
		for (String v : declaredViews.keySet()) {
			UIAutoEditorView viewAnnotation = declaredViews.get(v);
			builders.put(viewAnnotation.viewName(), new AutoFormViewBuilder(viewAnnotation));
		}
		
		for (String v : foundViews) {
			v = v.trim();
			if (builders.containsKey(v)) {
				continue;
			}
			builders.put(v, new AutoFormViewBuilder(v));
		}
		
		AutoFormViewBuilder rootBuilder = builders.get(UIConstants.VIEWS_ALL);
		for (AutoFormViewBuilder bldr : builders.values()) {
			if (bldr.name.equals(UIConstants.VIEWS_ALL)) {
				continue;
			}
			if (bldr.viewAnnotation == null) {
				bldr.setParent(rootBuilder);
			} else {
				AutoFormViewBuilder pb = builders.get(bldr.viewAnnotation.inheritFrom());
				if (pb == null) {
					pb = rootBuilder;
				}
				bldr.setParent(pb);
			}
		}
		
		for (AutoFormViewBuilder vb : builders.values()) {
			AutoFormViewDTO dto = vb.build(fields.values(), this.rootBuilder);
			if (dto == null) {
				continue;
			}
			ret.put(vb.name, dto);
		}
		return ret;
	}
	
	HashMap<String, Field> findAllFields(EntityModelBuilder modelBuilder, ArrayList<String> foundViews) {
		HashMap<String, Field> fields = new HashMap<>();
		HashMap<String, Boolean> fvmap = new HashMap<>();
		for (BeanPropertyDesc prop : this.bean.getProperties()) {
			HashMap<String,UIEditorField> annotations = getFieldAnnotations(prop);
			if (annotations == null) {
				continue;
			}
			if (annotations.size() == 0) {
				continue;
			}
			
			for (String fv : annotations.keySet()) {
				fvmap.put(fv, true);
			}
			
			IPropertyBuiler<?> builder = EntityModelBuilder.getBuilder(prop);
			if (builder == null) {
				continue;
			}
			builder.prepare(prop);
			String name = builder.getPropertyName();
		
			
			Field fld = new Field(name, builder, prop, annotations);
			fields.put(name, fld);
		}
		foundViews.addAll(fvmap.keySet());
		return fields;
	}

	private static HashMap<String,UIEditorField> getFieldAnnotations(BeanPropertyDesc prop) {
		HashMap<String,UIEditorField> ret = new HashMap<>();
		UIEditorField fld = prop.getAnnotation(UIEditorField.class);
		if (fld != null) {
			if (fld.forViews().length > 0) {
				for (String fv : fld.forViews()) {
					if (!ret.containsKey(fv)) {
						ret.put(fv, fld);
					}
				}
			} else if (!ret.containsKey(fld.forView())) {
				ret.put(fld.forView(), fld);
			}
		}
		UIEditorsField flds = prop.getAnnotation(UIEditorsField.class);
		if (flds != null) {
			for (UIEditorField fld1 : flds.value()) {
				if (fld1.forViews().length > 0) {
					for (String fv : fld1.forViews()) {
						if (!ret.containsKey(fv)) {
							ret.put(fv, fld1);
						}
					}
				} else if (!ret.containsKey(fld1.forView())) {
					ret.put(fld1.forView(), fld1);
				}
			}
		}
		return ret;
	}
	public void parse() {
		// Finding all UIAutoEditorView declarations
		this.declaredViews = new HashMap<>();
		parseViews(this.bean, this.declaredViews);
		
		// if default view was not declared, will use "silent" annotation.
		if (!declaredViews.containsKey(UIConstants.VIEWS_ALL)) {
			UIAutoEditorView allViewsAnnotation = new UIAutoEditorView() {
				@Override
				public Class<? extends Annotation> annotationType() {
					return UIAutoEditorView.class;
				}
				
				@Override
				public int width() {
					return 0;
				}
				
				@Override
				public String viewName() {
					return "";
				}
				
				@Override
				public int heigh() {
					return 0;
				}

				@Override
				public String inheritFrom() {
					return "";
				}
			};
			declaredViews.put(UIConstants.VIEWS_ALL, allViewsAnnotation);
		}

		HashMap<String, AbstractPanelBuilder<?>> declaredSections = new HashMap<>();
		parseSections(this.bean, declaredSections);
		
		// Trying to find annotations in props
		for (BeanPropertyDesc prop : this.bean.getProperties()) {
			parseSections(prop, declaredSections);
		}
		
		for (AbstractPanelBuilder<?> l : declaredSections.values()) {
			if (l instanceof AutoFormTabPanelBuilder) {
				findNativeChildren((AutoFormTabPanelBuilder)l);
			}
		}
		
		// Building sections tree
		for (AbstractPanelBuilder<?> l : declaredSections.values()) {
			if (l.parentName != null) {
				String pname = l.parentName.trim();
				if (pname.length() > 0) {
					AbstractPanelBuilder<?> parent = declaredSections.get(pname);
					if (parent == null) {
						//TODO: LOG
						continue;
					}
					parent.children.add(l);
					l.parent = parent;
				}
			}
		}
		
		//Ordering all sections and finding root
		List<AbstractPanelBuilder<?>> rootLeafs = new ArrayList<>();
		for (AbstractPanelBuilder<?> leaf : declaredSections.values()) {
			leaf.children.sort(new Comparator<AbstractPanelBuilder<?>>() {
				@Override
				public int compare(AbstractPanelBuilder<?> o1, AbstractPanelBuilder<?> o2) {
					int ret = Integer.compare(o1.order, o2.order);
					if (ret != 0) {
						return ret;
					}
					return o1.name.compareTo(o2.name);
				}
			});
			if (leaf.parent == null) {
				rootLeafs.add(leaf);
			}
		}
		
		AutoFormRootPanelBuilder rb = new AutoFormRootPanelBuilder("", null, null, 0);	
		if (rootLeafs.size() > 0) {
			for (AbstractPanelBuilder<?> rl : rootLeafs) {
				rb.children.add(rl);
				rl.parent = rb;
			}
		}
		this.rootBuilder = rb;
	}

	private static void parseViews(BeanTypeDesc<?> bean, HashMap<String, UIAutoEditorView> declaredViews) {
		UIAutoEditorView v1 = bean.getAnnotation(UIAutoEditorView.class);
		if (v1 != null) {
			if (!declaredViews.containsKey(v1.viewName())) {
				declaredViews.put(v1.viewName(), v1);
			}
		}
		UIAutoEditorViews vs = bean.getAnnotation(UIAutoEditorViews.class);
		if (vs != null) {
			for (UIAutoEditorView v : vs.value()) {
				if (!declaredViews.containsKey(v.viewName())) {
					declaredViews.put(v.viewName(), v);
				}
			}
		}
		if (bean.getSupertype() != null && bean.getSupertype().getType() != Object.class) {
			parseViews(bean.getSupertype(), declaredViews);
		}
	}

	private static void parseSections(BeanPropertyDesc property, HashMap<String, AbstractPanelBuilder<?>> result) {
		// Sections
		UIEditorSections sas = property.getAnnotation(UIEditorSections.class);
		if (sas != null) {
			for (UIEditorSection e : sas.value()) {
				if (result.containsKey(e.name())) {
					AbstractPanelBuilder<?> a = result.get(e.name());
					if (a.annotation.annotationType() != UIEditorSection.class) {
						// TODO: LOG
						// Переопределение для того же идентификатора!
					}
				} else {
					result.put(e.name(), new AutoFormSectionPanelBuilder(e.name(), e.parent(), e, e.order()));
				}
			}
		}

		UIEditorSection sa = property.getAnnotation(UIEditorSection.class);
		if (sa != null) {
			if (result.containsKey(sa.name())) {
				AbstractPanelBuilder<?> a = result.get(sa.name());
				if (a.annotation.annotationType() != UIEditorSection.class) {
					// TODO: LOG
					// Переопределение для того же идентификатора!
				}
			} else {
				result.put(sa.name(), new AutoFormSectionPanelBuilder(sa.name(), sa.parent(), sa, sa.order()));
			}
		}

		// Tabs
		UIEditorTabPanels tas = property.getAnnotation(UIEditorTabPanels.class);
		if (sas != null) {
			for (UIEditorTabPanel e : tas.value()) {
				if (result.containsKey(e.id())) {
					AbstractPanelBuilder<?> a = result.get(e.id());
					if (a.annotation.annotationType() != UIEditorTabPanel.class) {
						// TODO: LOG
						// Переопределение для того же идентификатора!
					}
				} else {
					result.put(e.id(), new AutoFormTabPanelBuilder(e.id(), e.parent(), e, e.order()));
				}
			}
		}

		UIEditorTabPanel ta = property.getAnnotation(UIEditorTabPanel.class);
		if (ta != null) {
			if (result.containsKey(ta.id())) {
				AbstractPanelBuilder<?> a = result.get(ta.id());
				if (a.annotation.annotationType() != UIEditorTabPanel.class) {
					// TODO: LOG
					// Переопределение для того же идентификатора!
				}
			} else {
				result.put(ta.id(), new AutoFormTabPanelBuilder(ta.id(), ta.parent(), ta, ta.order()));
			}
		}
	}
	private static void parseSections(BeanTypeDesc<?> bean, HashMap<String, AbstractPanelBuilder<?>> result) {
		// Sections
		UIEditorSections sas = bean.getAnnotation(UIEditorSections.class);
		if (sas != null) {
			for (UIEditorSection e : sas.value()) {
				if (result.containsKey(e.name())) {
					AbstractPanelBuilder<?> a = result.get(e.name());
					if (a.annotation.annotationType() != UIEditorSection.class) {
						// TODO: LOG
						// Переопределение для того же идентификатора!
					}
				} else {
					result.put(e.name(), new AutoFormSectionPanelBuilder(e.name(), e.parent(), e, e.order()));
				}
			}
		}

		UIEditorSection sa = bean.getAnnotation(UIEditorSection.class);
		if (sa != null) {
			if (result.containsKey(sa.name())) {
				AbstractPanelBuilder<?> a = result.get(sa.name());
				if (a.annotation.annotationType() != UIEditorSection.class) {
					// TODO: LOG
					// Переопределение для того же идентификатора!
				}
			} else {
				result.put(sa.name(), new AutoFormSectionPanelBuilder(sa.name(), sa.parent(), sa, sa.order()));
			}
		}

		// Tabs
		UIEditorTabPanels tas = bean.getAnnotation(UIEditorTabPanels.class);
		if (sas != null) {
			for (UIEditorTabPanel e : tas.value()) {
				if (result.containsKey(e.id())) {
					AbstractPanelBuilder<?> a = result.get(e.id());
					if (a.annotation.annotationType() != UIEditorTabPanel.class) {
						// TODO: LOG
						// Переопределение для того же идентификатора!
					}
				} else {
					result.put(e.id(), new AutoFormTabPanelBuilder(e.id(), e.parent(), e, e.order()));
				}
			}
		}

		UIEditorTabPanel ta = bean.getAnnotation(UIEditorTabPanel.class);
		if (ta != null) {
			if (result.containsKey(ta.id())) {
				AbstractPanelBuilder<?> a = result.get(ta.id());
				if (a.annotation.annotationType() != UIEditorTabPanel.class) {
					// TODO: LOG
					// Переопределение для того же идентификатора!
				}
			} else {
				result.put(ta.id(), new AutoFormTabPanelBuilder(ta.id(), ta.parent(), ta, ta.order()));
			}
		}

		// Supertype
		if (bean.getSupertype() != null && bean.getSupertype().getType() != Object.class) {
			parseSections(bean.getSupertype(), result);
		}
	}
	
	private static void findNativeChildren(AutoFormTabPanelBuilder leaf) {
		int o = 1;
		for (UIEditorSection section : leaf.annotation.tabs()) {
			AutoFormSectionPanelBuilder sl = new AutoFormSectionPanelBuilder(section.name(), leaf.name, section, section.order() == 0 ? o : section.order());
			leaf.children.add(sl);
			sl.parent = leaf;
			o++;
		}
	}
}
