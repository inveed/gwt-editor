package net.inveed.gwt.server;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.commons.reflection.BeanTypeDesc;
import net.inveed.gwt.editor.shared.EntityEditorDTO;
import net.inveed.gwt.editor.shared.EntityEditorsDTO;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.PropertyModelDTO;
import net.inveed.gwt.server.annotations.JsonRPCServiceRef;
import net.inveed.gwt.server.annotations.UILinkedEntity;
import net.inveed.gwt.server.annotations.UILinkedEntityCreateArg;
import net.inveed.gwt.server.annotations.editor.UIEditor;
import net.inveed.gwt.server.annotations.editor.UIEditorSection;
import net.inveed.gwt.server.annotations.editor.UIEditorPanel;
import net.inveed.gwt.server.annotations.editor.UIEditors;
import net.inveed.rest.jpa.annotations.TypeDescriminator;
import net.inveed.rest.jpa.annotations.TypeDescriminatorField;
import net.inveed.rest.jpa.typeutils.EntityTypeExt;

public class EntityModelBuilder {
	private final BeanTypeDesc<?> bean;
	
	public String entityName;
	public String typeDescriminator;
	public String superType;
	public boolean isAbstract;
	public String typeDescriminatorField;
	public boolean typeOnUpdate;
	
	public EntityAccessServiceBuilder service = new EntityAccessServiceBuilder();
	public Map<String, PropertyModelBuilder> properties = new HashMap<>();
	public Map<String, EntityEditorBuilder> editors = new HashMap<>();
	
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
		
		Map<String, PropertyModelDTO> flds = new HashMap<>();
		for (String k : this.properties.keySet()) {
			PropertyModelBuilder fb = this.properties.get(k);
			PropertyModelDTO fm = fb.build();
			if (fm == null) {
				//TODO: Warn!
				continue;
			}
			flds.put(k, fm);
		}
		EntityEditorsDTO ees = null;
		if (this.editors.size() > 0) {
			Map<String, EntityEditorDTO> eedtoMap = new HashMap<>();
			for (String k : this.editors.keySet()) {
				EntityEditorDTO eedto = this.editors.get(k).build();
				eedtoMap.put(k, eedto);
			}
			ees = new EntityEditorsDTO(eedtoMap);
		}
		return new EntityModelDTO(this.entityName, this.typeDescriminator, this.superType, this.isAbstract, typeDescriminatorField, this.typeOnUpdate, service.build(), ees, flds);
	}
	
	private void parseProperties() {
		Collection<BeanPropertyDesc> props = null;
		if (this.superType != null) {
			props = bean.getDeclaredProperties().values();
		} else {
			props = bean.getProperties();
		}
		for (BeanPropertyDesc prop : props) {
			if (prop.getAnnotation(JsonIgnore.class) != null) {
				continue;
			}
			PropertyModelBuilder f = PropertyModelBuilder.build(prop);
			if (f != null) {
				this.properties.put(f.getName(), f);
			}
			
			UIEditorSection fca = prop.getAnnotation(UIEditorSection.class);
			if (fca != null) {
				EntityEditorBuilder eeb = this.getEntityEditorBuilder(fca.viewName());
				if (eeb != null) {
					eeb.registerAnnotation(fca);
				}
			}
			
			UIEditorPanel tca = prop.getAnnotation(UIEditorPanel.class);
			if (tca != null) {
				EntityEditorBuilder eeb = this.getEntityEditorBuilder(tca.viewName());
				if (eeb != null) {
					eeb.registerAnnotation(tca);
				}
			}
		}
	}
	
	private EntityEditorBuilder getEntityEditorBuilder(String view) {
		if (view == null) {
			return null;
		}
		view = view.trim();
		if (view.length() < 1) {
			return null;
		}
		EntityEditorBuilder ret = this.editors.get(view);
		if (ret == null) {
			ret = new EntityEditorBuilder();
			this.editors.put(view, ret);
		}
		return ret;

	}
	
	private void parseEditors() {
		HashMap<String, UIEditor> m =new HashMap<>();
		
		UIEditors eas = bean.getAnnotation(UIEditors.class);
		if (eas != null) {
			for (UIEditor e : eas.value()) {
				m.put(e.viewName(), e);
			}
		}
		UIEditor ea = bean.getAnnotation(UIEditor.class);
		if (ea != null) {
			m.put(ea.viewName(), ea);
		}
		
		for (String k : m.keySet()) {
			UIEditor e = m.get(k);
			EntityEditorBuilder eb = this.getEntityEditorBuilder(k);
			eb.registerAnnotation(e);
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
		
		this.entityName = PropertyModelBuilder.getEntityName(this.bean.getType());
		this.isAbstract = Modifier.isAbstract(this.bean.getType().getModifiers());
		this.parseEditors();
	}
}
