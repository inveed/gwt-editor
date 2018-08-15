package net.inveed.gwt.server;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.commons.reflection.BeanTypeDesc;
import net.inveed.commons.reflection.EnumTypeDesc;
import net.inveed.commons.reflection.JavaTypeDesc;
import net.inveed.commons.reflection.JavaTypeRegistry;
import net.inveed.commons.reflection.ListTypeDesc;
import net.inveed.commons.reflection.NativeTypeDesc;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.FormViewAttributesDTO;
import net.inveed.gwt.editor.shared.ListViewAttributesDTO;
import net.inveed.gwt.editor.shared.PropertyModelDTO;
import net.inveed.gwt.server.annotations.UIAsName;
import net.inveed.gwt.server.annotations.UIProperty;
import net.inveed.gwt.server.annotations.UIFormView;
import net.inveed.gwt.server.annotations.UIFormViews;
import net.inveed.gwt.server.annotations.UIListView;
import net.inveed.gwt.server.annotations.UIListViews;
import net.inveed.gwt.server.annotations.UIRangeValidator;
import net.inveed.gwt.server.annotations.UISelectionFilter;
import net.inveed.gwt.server.annotations.UISelectionFilters;
import net.inveed.gwt.server.annotations.UITextFieldValidator;
import net.inveed.rest.jpa.IComplexIdEntity;
import net.inveed.rest.jpa.IEntityInstantiator;
import net.inveed.rest.jpa.annotations.EntityInstantiator;
import net.inveed.rest.jpa.typeutils.EntityTypeExt;
import net.inveed.rest.jpa.typeutils.JsonPropertyExt;

public class PropertyModelBuilder {
	private transient BeanPropertyDesc desc;
	
	public FieldType type;
	public String defaultValue;
	public String enabledWhen;
	public PropertyAttributesBuilder attributes = new PropertyAttributesBuilder();
	
	public Map<String, FormViewAttributesDTO> formViews = new HashMap<>();
	public Map<String, ListViewAttributesDTO> listViews = new HashMap<>();
	public Map<String, String> filters = new HashMap<>();
	public String name;
	
	public PropertyModelDTO build() {
		if (this.type == null) {
			return null;
		}
		return new PropertyModelDTO(
				this.type,
				this.defaultValue,
				this.enabledWhen,
				attributes.build(),
				this.formViews.size() != 0 ? this.formViews : null,
				this.listViews.size() != 0 ? this.listViews : null,
				this.filters.size() != 0 ? this.filters : null);
	}
	
	private void parse() {
	
		this.type = this.findType();
		if (type == null) {
			return;
		}
		this._setProperties();
		
		JsonPropertyExt jpe = this.desc.getExtension(JsonPropertyExt.class);
		if (jpe != null) {
			this.name = jpe.getJSONName();
		} else {
			this.name = this.desc.getName();
		}
		
		UIAsName ana = this.desc.getAnnotation(UIAsName.class);
		if (ana != null) {
			this.attributes.asNameIndex = ana.value();
		}
		
		ArrayList<UIListView> listViews = new ArrayList<>();
		ArrayList<UIFormView> formViews = new ArrayList<>();
		UIListView lva = this.desc.getAnnotation(UIListView.class);
		if (lva != null) {
			listViews.add(lva);
		}
		UIFormView fva = this.desc.getAnnotation(UIFormView.class);
		if (fva != null) {
			formViews.add(fva);
		}
		UIListViews lvsa = this.desc.getAnnotation(UIListViews.class);
		if (lvsa != null) {
			Collections.addAll(listViews, lvsa.value());
		}
		UIFormViews fvsa = this.desc.getAnnotation(UIFormViews.class);
		if (fvsa != null) {
			Collections.addAll(formViews, fvsa.value());
		}
		
		for (UIFormView a : formViews) {
			this.formViews.put(a.name(), new FormViewAttributesBuilder(a).build());
		}
		for (UIListView a : listViews) {
			this.listViews.put(a.name(), new ListViewAttributesBuilder(a).build());
		}
		
		UIProperty fa = desc.getAnnotation(UIProperty.class);
		if (fa != null) {
			this.defaultValue = fa.defaultValue().length() != 0 ? fa.defaultValue() : null;
			this.enabledWhen = fa.enabledWhen().length() != 0 ? fa.enabledWhen() : null;
		}
		
	}
	
	@JsonIgnore(true)
	private void _setProperties() {
		UIRangeValidator rangeValidator = this.desc.getAnnotation(UIRangeValidator.class);
		UITextFieldValidator tfv = this.desc.getAnnotation(UITextFieldValidator.class);
		
		
		if (this._isReadonly()) {
			attributes.readonly = true;
		}
		this.attributes.required = _isRequired();
		
		switch (this.type) {
		case OBJECT_REF:
			JavaTypeDesc<?> tdef = this.desc.getType();
			if (tdef instanceof BeanTypeDesc) {
				this.setRefTypeProperty((BeanTypeDesc<?>) tdef);
			} else {
				//TODO: LOG!
			}
			UISelectionFilter[] filters = null;
			UISelectionFilters aFilters = this.desc.getAnnotation(UISelectionFilters.class);
			if (aFilters != null) {
				filters = aFilters.value();
			} else {
				UISelectionFilter aFilter = this.desc.getAnnotation(UISelectionFilter.class);
				if (aFilter != null) {
					filters = new UISelectionFilter[] {aFilter};
				}
			}
			if (filters != null) {
				for (UISelectionFilter f : filters) {
					this.filters.put(f.param(), f.value());
				}
			}
		case INTEGER:
		case FLOAT:
		case DURATION_ISO:
		case DURATION_MIN:
		case DURATION_SECONDS:
		case DURATION_MS:
			if (rangeValidator != null) {
				if (rangeValidator.max() != Double.MIN_VALUE) {
					attributes.max = rangeValidator.max();
				}
				if (rangeValidator.min() != Double.MAX_VALUE) {
					attributes.min = rangeValidator.min();
				}
			}
			break;
		case SECRET_KEY:
		case BINARY_KEY:
			if (rangeValidator != null) {
				if (rangeValidator.min() != Double.MAX_VALUE) {
					attributes.min = rangeValidator.min();
				}
			}
		case TEXT:
		//case TEXT_LOCALIZED:
		case TEXT_LONG:
		case URL:
			if (rangeValidator != null) {
				if (rangeValidator.max() != Double.MIN_VALUE) {
					attributes.max = rangeValidator.max();
				} else {
					attributes.max = this._getLengthFromAnnotation();
				}
				if (rangeValidator.min() != Double.MAX_VALUE) {
					attributes.min = rangeValidator.min();
				}
			} else {
				attributes.max = this._getLengthFromAnnotation();
			}
			if (tfv != null) {
				if (tfv.regexp().trim().length() > 0 && this.type != FieldType.URL)
					attributes.regexp = tfv.regexp().trim();
				if (tfv.regexpError().trim().length() > 0) {
					attributes.regexpError = tfv.regexpError().trim();
				}
				else if (tfv.startWith().length() > 0) {
					attributes.startWith = tfv.startWith();
				}
			}
			break;
			
		case ADDR_HOST:
		case PASSWORD:
			if (rangeValidator != null) {
				if (rangeValidator.max() != Double.MIN_VALUE) {
					attributes.max = rangeValidator.max();
				} else {
					attributes.max = this._getLengthFromAnnotation();
				}
				if (rangeValidator.min() != Double.MAX_VALUE) {
					attributes.min = rangeValidator.min();
				}
			} else {
				attributes.max = this._getLengthFromAnnotation();
			}
			break;
		case LINKED_ENTITIES_LIST:
			if (rangeValidator != null) {
				if (rangeValidator.max() != Double.MIN_VALUE) {
					this.attributes.max = rangeValidator.max();
				}
				if (rangeValidator.min() != Double.MAX_VALUE) {
					this.attributes.min = rangeValidator.min();
				}
			} 
			this.attributes.readonly = null;
			break;
		default:
			break;
		}
		
	}
	
	@JsonIgnore(true)
	private boolean _isReadonly() {
		if (desc.getType() instanceof ListTypeDesc) {
			return true;
		}
		UIProperty fa = desc.getAnnotation(UIProperty.class);
		if (fa != null && fa.readonly() != UIProperty.TriBool.UNDEF) {
			return fa.readonly() == UIProperty.TriBool.TRUE;
		}
		if (!desc.canSet()) {
			return true;
		}
		return false;
	}
		
	@JsonIgnore(true)
	private Boolean _isRequired() {
		Id ida = desc.getAnnotation(Id.class);
		if (ida != null) {
			return true;
		}
		
		Column ca = desc.getAnnotation(Column.class);
		if (ca != null) {
			if (!ca.nullable()) { 
				return true;
			}
		}
		JoinColumn ja = desc.getAnnotation(JoinColumn.class);
		if (ja != null) {
			if (!ja.nullable()) {
				return true;
			}
		}
		ManyToOne ma = desc.getAnnotation(ManyToOne.class);
		if (ma != null && !ma.optional()) {
			return true;
		}
		
		UIProperty fa = desc.getAnnotation(UIProperty.class);
		if (fa != null && fa.required()) {
			return true;
		}
		return false;
	}
	
	private final Double _getLengthFromAnnotation() {
		Column c = this.desc.getAnnotation(Column.class);
		if (c != null) {
			return (double) c.length();
		}
		return null;
	}
	
	public static PropertyModelBuilder build(BeanPropertyDesc desc) {
		PropertyModelBuilder ret = new PropertyModelBuilder();
		ret.desc = desc;
		ret.parse();
		if (ret.getName() != null) {
			return ret;
		}
		return null;
	}
	
	String getName() {
		return this.name;
	}

	private FieldType findSimpleType(NativeTypeDesc<?> ntype) {
		if (ntype.isInt() || ntype.isLong() || ntype.isShort() || ntype.isByte()) {
			if (desc.getAnnotation(Id.class) != null) {
				this.attributes.readonly = true;
				return FieldType.ID_INTEGER;
			}
			return FieldType.INTEGER;
		} else if (ntype.isBoolean()) {
			return FieldType.BOOLEAN;
		} else if(ntype.isFloat() || ntype.isDouble()) {
			return FieldType.FLOAT;
		} else if (ntype.isString()) {
			if (desc.getAnnotation(Id.class) != null) {
				return FieldType.ID_STRING;
			}
			if (IComplexIdEntity.class.isAssignableFrom(desc.getBeanType().getType()) && desc.getName().equals("complexId")) {
				return FieldType.ID_STRING;
			}
			Column ca = desc.getAnnotation(Column.class);
			if (ca != null) {
				if (ca.length() <= 128) {
					return FieldType.TEXT;
				}
				this.attributes.max = (double) ca.length();
			} 
			return FieldType.TEXT_LONG;
		} else if (ntype.isDate()) {
			return FieldType.DATE;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private FieldType findBeanType(BeanTypeDesc<?> btype) {
		EntityTypeExt<?> eext = btype.getExtension(EntityTypeExt.class);
		if (eext != null && desc.getType().getExtension(EntityTypeExt.class) != null) {
			// Поле - сущность, контейнер - сущность, особые правила сериализации
			
			
			if (eext.getIDFields().size() == 1) {
				this.setRefTypeProperty(btype);
				return FieldType.OBJECT_REF;
				
			} else {
				if (eext.getBeanType().getAnnotation(EntityInstantiator.class) != null) {
					this.setRefTypeProperty(btype);
					return FieldType.OBJECT_REF;
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}
	
	private void setRefTypeProperty(BeanTypeDesc<?> btype) {
		EntityTypeExt<?> eext = btype.getExtension(EntityTypeExt.class);
		if (eext != null && desc.getType().getExtension(EntityTypeExt.class) != null) {
			// Поле - сущность, контейнер - сущность, особые правила сериализации
			
			
			if (eext.getIDFields().size() == 1) {
				this.attributes.referencedEntityName = eext.getEntityName();
			} else {
				IEntityInstantiator<?, ?> ei = eext.getInstantiator();
				if (ei != null) {
					this.attributes.referencedEntityName = eext.getEntityName();
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	public static final String getEntityName(Class<?> type) {
		JavaTypeDesc<?> t = JavaTypeRegistry.getType(type);
		EntityTypeExt<?> e = t.getExtension(EntityTypeExt.class);
		if (e != null) {
			return e.getEntityName();
		}
		return t.getType().getSimpleName();
	}
	
	@SuppressWarnings("unchecked")
	private FieldType findListType(ListTypeDesc type) {
		OneToMany otma = this.desc.getAnnotation(OneToMany.class);
		if (otma != null) {
			JavaTypeDesc<?> targetType = null;
			if (otma.targetEntity() == void.class) {
				//TODO: вычислить по RAW-свойсту целевой тип
				Type rawType = this.desc.getRawGetterType();
				if (rawType == null) {
					//TODO: LOG
					return null;
				}
				if (rawType instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) rawType;
					if (pt.getActualTypeArguments() == null || pt.getActualTypeArguments().length != 1) {
						//TODO: LOG
						return null;
					}
					if (pt.getActualTypeArguments()[0] instanceof Class<?>) {
						targetType = JavaTypeRegistry.getType((Class<?>)pt.getActualTypeArguments()[0]);
					} else {
						//TODO: LOG
						return null;
					}
				}
			} else {
				targetType = JavaTypeRegistry.getType(otma.targetEntity());
			}
			
			EntityTypeExt<?> olEntity = targetType.getExtension(EntityTypeExt.class);
			if (olEntity == null) {
				//TODO: LOG
				return null; // Списки должны быть списками сущностей JPA.
			}
						
			this.attributes.referencedEntityName = olEntity.getEntityName();
			UIProperty fld = this.desc.getAnnotation(UIProperty.class);
			if (fld != null) {
				if (fld.mappedBy().length() > 0) {
					this.attributes.mappedBy = fld.mappedBy();
				}
			}
			if (otma.mappedBy().length() > 0 && this.attributes.mappedBy == null) {
				this.attributes.mappedBy = otma.mappedBy();
			}
			return FieldType.LINKED_ENTITIES_LIST;
		}
		return null;
	}
	
	private FieldType findEnumType(EnumTypeDesc<?> type) {
		this.attributes.referencedEnumName = type.getName();
		return FieldType.ENUM;
	}
	
	private FieldType findType() {
		UIProperty typeAnnotation = this.desc.getAnnotation(UIProperty.class);
		if (typeAnnotation != null) {
			FieldType ret = typeAnnotation.type();
			if (ret != FieldType.AUTO) {
				return ret;
			}
		}
		
		JavaTypeDesc<?> fieldType = desc.getType();
		
		if (fieldType instanceof EnumTypeDesc<?>) {
			return this.findEnumType((EnumTypeDesc<?>) fieldType);
		}
		if (fieldType instanceof NativeTypeDesc<?>) {
			return findSimpleType((NativeTypeDesc<?>) fieldType);
		}
		if (fieldType instanceof ListTypeDesc) {
			return this.findListType((ListTypeDesc) fieldType);
		}
		if (fieldType instanceof BeanTypeDesc) {
			return this.findBeanType((BeanTypeDesc<?>) fieldType);
		} 
		
		
		return null;
	}

}
