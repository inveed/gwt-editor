package net.inveed.gwt.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.commons.reflection.BeanTypeDesc;
import net.inveed.commons.reflection.JavaTypeDesc;
import net.inveed.commons.reflection.JavaTypeRegistry;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.editor.shared.lists.ListViewDTO;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;
import net.inveed.gwt.server.annotations.JsonRPCServiceRef;
import net.inveed.gwt.server.annotations.UIAsName;
import net.inveed.gwt.server.annotations.UILinkedEntity;
import net.inveed.gwt.server.annotations.UILinkedEntityCreateArg;
import net.inveed.gwt.server.annotations.UIListView;
import net.inveed.gwt.server.annotations.UIListViewFieldCollection;
import net.inveed.gwt.server.annotations.UIPropertyAnnotation;

import net.inveed.gwt.server.annotations.editor.UIEditorField;
import net.inveed.gwt.server.annotations.properties.UIBooleanProperty;
import net.inveed.gwt.server.annotations.properties.UIDateTimeProperty;
import net.inveed.gwt.server.annotations.properties.UIEnumProperty;
import net.inveed.gwt.server.annotations.properties.UIIdProperty;
import net.inveed.gwt.server.annotations.properties.UILinkedListProperty;
import net.inveed.gwt.server.annotations.properties.UINumberProperty;
import net.inveed.gwt.server.annotations.properties.UIObjectRefProperty;
import net.inveed.gwt.server.annotations.properties.UITextProperty;
import net.inveed.gwt.server.editors.AutoFormBuilder;
import net.inveed.gwt.server.propbuilders.IPropertyBuiler;
import net.inveed.rest.jpa.annotations.TypeDescriminator;
import net.inveed.rest.jpa.annotations.TypeDescriminatorField;
import net.inveed.rest.jpa.typeutils.EntityTypeExt;

public class EntityModelBuilder {
	public final BeanTypeDesc<?> bean;
	
	public boolean isAbstract;
	public String  entityName;
	public String  superType;
	public String  typeDescriminator;
	public String  typeDescriminatorField;
	public boolean typeOnUpdate;
	
	public EntityAccessServiceBuilder 			service = new EntityAccessServiceBuilder();
	
	public Map<String, IPropertyBuiler<?>>  	propertyBuilders = new HashMap<>();
	public Map<String, EntityListViewBuilder> 	listViewBuilders = new HashMap<>();
	
	private AutoFormBuilder				editorsBuilder;
	
	public static EntityModelDTO build(BeanTypeDesc<?> bean) {
		EntityModelBuilder bld = new EntityModelBuilder(bean);
		bld.parse();
		return bld.build();
	}
	
	public EntityModelBuilder(BeanTypeDesc<?> bean) {
		this.bean = bean;
	}
	
	public EntityModelDTO build() {
		if (this.entityName == null) {
			return null;
		}
		
		Map<String, AbstractPropertyDTO> properties = new HashMap<>();
		for (String k : this.propertyBuilders.keySet()) {
			IPropertyBuiler<?> pb = this.propertyBuilders.get(k);
			AbstractPropertyDTO dto = pb.build();
			if (dto == null) {
				//TODO: Warn!
				continue;
			}
			properties.put(k, dto);
		}
		
		Map<String, AutoFormViewDTO> editors = editorsBuilder.build(this);
		
		Map<String, ListViewDTO> listViews = null;
		if (this.listViewBuilders.size() > 0) {
			listViews = new HashMap<>();
			for (String k : this.listViewBuilders.keySet()) {
				ListViewDTO eedto = this.listViewBuilders.get(k).build();
				listViews.put(k, eedto);
			}
		}
	
		return new EntityModelDTO(
				this.entityName, 
				this.superType,
				this.isAbstract,
				this.typeDescriminator,
				typeDescriminatorField,
				this.typeOnUpdate,
				service.build(),
				editors,
				listViews,
				properties);
	}
	
	private static Class<? extends Annotation> determinePropertyType(BeanPropertyDesc prop) {
		if (prop.getAnnotation(JsonIgnore.class) != null) {
			return null;
		}
		if (prop.getAnnotation(UIEditorField.class) == null 
				&& prop.getAnnotation(UIListView.class) == null
				&& prop.getAnnotation(UIAsName.class) == null) {
			// Not marked for UI.
			return null;
		}
		
		if (prop.getAnnotation(Id.class) != null) {
			return UIIdProperty.class;
		}
		Class<?> c = prop.getType().getType();
		if (List.class.isAssignableFrom(c)) {
			if (prop.getAnnotation(OneToMany.class) != null) {
				return UILinkedListProperty.class;
			}
		}
		if (c == String.class) {
			return UITextProperty.class;
		} else if (c == boolean.class || c == Boolean.class) {
			return UIBooleanProperty.class;
		} else if (c.isEnum()) {
			return UIEnumProperty.class;
		} else if (c.isPrimitive() 
				|| c == Integer.class 
				|| c == Long.class 
				|| c == Short.class 
				|| c == Byte.class 
				|| c == Character.class 
				|| c == Float.class 
				|| c == Double.class) {
			return UINumberProperty.class;
		} else if (c == Date.class || c == java.sql.Date.class) {
			return UIDateTimeProperty.class;
		}
		
		if (prop.getAnnotation(JoinColumn.class) != null
				|| prop.getAnnotation(JoinColumns.class) != null
				|| prop.getAnnotation(OneToMany.class) != null) {
			return UIObjectRefProperty.class;
		}
		if (prop.getAnnotation(ManyToOne.class) != null) {
			return UIObjectRefProperty.class;
		}
		
		return null;
	}
	
	public static IPropertyBuiler<?> getBuilder(BeanPropertyDesc prop) {
		if (prop.getAnnotation(JsonIgnore.class) != null) {
			return null;
		}

		List<Annotation> annotatedAnnotations = prop.getAnnotatedAnnotations(UIPropertyAnnotation.class);
		if (annotatedAnnotations == null) {
			return null;
		}
		Annotation propAnnotation = null;
		Class<? extends Annotation> annotationClass = null;
		
		if (annotatedAnnotations.size() == 0) {
			annotationClass = determinePropertyType(prop);
			if (annotationClass == null) {
				return null;
			}
		} else {
			if (annotatedAnnotations.size() != 1) {
				//TODO: LOG
			}
			propAnnotation = annotatedAnnotations.get(0);
			annotationClass = propAnnotation.annotationType();
		}
		UIPropertyAnnotation aa = annotationClass.getAnnotation(UIPropertyAnnotation.class);
		if (aa == null) {
			//TODO: log вообще хрень какая-то
			return null;
		}
		String impl = aa.implementor();
		try {
			Class<?> implClass = Class.forName(impl);
			if (!IPropertyBuiler.class.isAssignableFrom(implClass)) {
				//TODO: log
				return null;
			}
			return (IPropertyBuiler<?>) implClass.newInstance();
		} catch (ClassNotFoundException e) {
			//TODO: LOG
			return null;
		} catch (InstantiationException e) {
			//TODO: LOG
			return null;
		} catch (IllegalAccessException e) {
			//TODO: LOG
			return null;
		}
	}
	
	private void parseLists() {
		for (BeanPropertyDesc prop : bean.getProperties()) {
			HashMap<String, UIListView> lvmap = new HashMap<>();
			UIListViewFieldCollection lvas = prop.getAnnotation(UIListViewFieldCollection.class);
			if (lvas != null) {
				for (UIListView lv : lvas.value()) {
					lvmap.put(lv.name(), lv);
				}
			}
			UIListView lva = prop.getAnnotation(UIListView.class);
			if (lva != null) {
				lvmap.put(lva.name(), lva);
			}
			
			IPropertyBuiler<?> builder = getBuilder(prop);
			if (builder == null) {
				//TODO: LOG
				continue;
			}
			
			builder.prepare(prop);
			
			for (String k : lvmap.keySet()) {
				if (!this.listViewBuilders.containsKey(k)) {
					EntityListViewBuilder b = new EntityListViewBuilder();
					this.listViewBuilders.put(k, b);
				}
				this.listViewBuilders.get(k).registerProperty(builder, lvmap.get(k));
			}
		}
	}
	private void parseProperties() {
		Collection<BeanPropertyDesc> props = null;
		if (this.superType != null) {
			props = bean.getDeclaredProperties().values();
		} else {
			props = bean.getProperties();
		}
		
		for (BeanPropertyDesc prop : props) {
			IPropertyBuiler<?> builder = getBuilder(prop);
			if (builder == null) {
				//TODO: LOG
				continue;
			}
			
			builder.prepare(prop);
			this.propertyBuilders.put(builder.getPropertyName(), builder);
		}
	}
	
	private void parseSupertype() {
		if (this.bean.getSupertype() == null)
			return;
		@SuppressWarnings("unchecked")
		EntityTypeExt<?> ee = this.bean.getSupertype().getExtension(EntityTypeExt.class);
		if (ee == null) {
			return;
		}
		if (ee.isMappedSuperclass()) {
			return;
		}
		
		this.superType = ee.getEntityName();
		
		TypeDescriminator tda = this.bean.getAnnotation(TypeDescriminator.class);
		if (tda != null) {
			this.typeDescriminator = tda.value();
		} else {
			@SuppressWarnings("unchecked")
			EntityTypeExt<?> ete = this.bean.getExtension(EntityTypeExt.class);
			if (ete != null) {
				this.typeDescriminator = ete.getEntityName();
			} else {
				this.typeDescriminator = this.bean.getShortName().trim();
			}
		}
	}
	private void parse() {
		
		this.parseSupertype();
		
		this.parseProperties();
		
		JsonRPCServiceRef srefa = bean.getAnnotation(JsonRPCServiceRef.class);
		if (srefa != null) {
			EntityAccessServiceBuilder easb = new EntityAccessServiceBuilder();
			easb.service = srefa.value();
			easb.methodCreate = srefa.methodCreate();
			easb.methodDelete = srefa.methodDelete();
			easb.methodUpdate = srefa.methodUpdate();
			easb.methodList = srefa.methodList();
			easb.methodGet = srefa.methodGet();
			easb.argData = srefa.argData();
			easb.argId = srefa.argID();
			easb.argPage = srefa.argPage();
			easb.argPageSize = srefa.argPageSize();
			
			UILinkedEntity mla = bean.getAnnotation(UILinkedEntity.class);
			if (mla != null) {
				easb.createArgs = new HashMap<>();
				for (UILinkedEntityCreateArg i : mla.items()) {
					String prop = i.property().trim();
					if (prop.length() == 0)
						prop = i.createArg();
					easb.createArgs.put(i.createArg(), prop);
				}
			}
			
			this.service = easb;
		}
		
		if (this.superType == null) {
			if (!Modifier.isFinal(this.bean.getType().getModifiers())) {
				TypeDescriminatorField tdfa = this.bean.getAnnotation(TypeDescriminatorField.class);
				if (tdfa == null && this.bean.getAnnotation(JsonSubTypes.class) != null && this.bean.getAnnotation(JsonTypeInfo.class) != null) {
					JsonTypeInfo ti = this.bean.getAnnotation(JsonTypeInfo.class);
					if (ti.include() == JsonTypeInfo.As.PROPERTY) {
						this.typeDescriminatorField = ti.property();
						this.typeOnUpdate = true;
					} else {
						this.typeDescriminatorField = "#type";
					}
				} else if (tdfa != null) {
					this.typeDescriminatorField = tdfa.value();
				} else {
					this.typeDescriminatorField = "#type";
				}
			}
		}
		
		this.entityName = getEntityName(this.bean.getType());
		this.isAbstract = Modifier.isAbstract(this.bean.getType().getModifiers());
		
		this.editorsBuilder = new AutoFormBuilder(this.bean);
		
		this.parseLists();
	}
	
	public static final String getEntityName(Class<?> type) {
        JavaTypeDesc<?> t = JavaTypeRegistry.getType(type);
        return getEntityName(t);
	}
	@SuppressWarnings("unchecked")
	public static final String getEntityName(JavaTypeDesc<?> t) {
        EntityTypeExt<?> e = t.getExtension(EntityTypeExt.class);
        if (e != null) {
                return e.getEntityName();
        }
        return t.getType().getSimpleName();
	}
}
