package net.inveed.gwt.editor.client.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.model.properties.IPropertyDescriptor;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.forms.panels.AutoFormViewDTO;
import net.inveed.gwt.editor.shared.lists.ListViewDTO;
import net.inveed.gwt.editor.shared.properties.AbstractPropertyDTO;

public final class EntityModel {
	private static final String[] STRING_ARRAY = new String[0];

	public static interface EntityModelMapper extends ObjectMapper<EntityModelDTO> {}
	
	private static final Logger LOG = Logger.getLogger(EntityModel.class.getName());
	private static final String I18N_PREFIX = "entities";
	
	private final EntityModelDTO dto;

	private boolean valid;
	private IPropertyDescriptor<?> idProperty;
	
	private List<IPropertyDescriptor<?>> nameProperties;
	private List<IPropertyDescriptor<?>> allProperties;
	private List<IPropertyDescriptor<?>> declaredProperties;
	
	private HashMap<String, IPropertyDescriptor<?>> allPropertiesByName;
	
	private HashMap<String, EntityModel> childModels = new HashMap<>();
	private HashMap<String, EntityModel> childModelsByDescriminator = new HashMap<>();
	
	private String discriminatorField;
	
	public EntityModel(EntityModelDTO dto, ConfigurationRegistry registry) {
		if (dto == null) {
			LOG.warning("Cannot create entity model: DTO is NULL");
			throw new NullPointerException("dto");
		}
		if (registry == null) {
			LOG.warning("Cannot create entity model: REGISTRY is NULL");
			throw new NullPointerException("registry");
		}
		if (dto.name == null) {
			LOG.warning("Cannot create entity model: DTO.name is NULL");
			throw new NullPointerException("dto.name");
		}
		if (dto.properties == null) {
			LOG.warning("Cannot create entity model: DTO.properties is NULL");
			throw new NullPointerException("dto.name");
		}
		
		this.dto = dto;
	}
	
	boolean initialize() {
		if (this.allProperties != null) {
			//Initialized or initializing now.
			return this.valid;
		}
		
		this.allProperties 		= new ArrayList<>();
		this.declaredProperties = new ArrayList<>();
		
		HashMap<String, IPropertyDescriptor<?>> declaredPropertiesMap = new HashMap<>();
		
		for (String fname : this.dto.properties.keySet()) {
			AbstractPropertyDTO fm = this.dto.properties.get(fname);
			IPropertyDescriptor<?> fld = ConfigurationRegistry.INSTANCE.createPropertyDesc(fm, fname, this);
			if (fld == null) {
				LOG.warning("Cannot create property '" + fname + "' for model '" + this.dto.name + "'");
				this.valid = false;
				return false;
			}
			if (!fld.isValid()) {
				LOG.warning("Property '" + fname + "' for model '" + this.dto.name + "' is invalid");
				this.valid = false;
				return false;
			}
			declaredPropertiesMap.put(fld.getName(), fld);
			if (fld.isId()) {
				if (this.idProperty != null) {
					LOG.warning("Multiple ID property for model '" + this.dto.name + "': '"
							+ this.idProperty.getName() +"' already declared, '" 
							+ fld.getName() + "' was the second.");
					
					this.valid = false;
					return false;
				}
				this.idProperty = fld;
			}
		}
		
		this.allProperties.addAll(declaredPropertiesMap.values());
		this.declaredProperties.addAll(declaredPropertiesMap.values());
		
		if (this.getParentModel() != null) {
			if (!this.getParentModel().initialize()) {
				LOG.warning("Model '" + this.dto.name + "' has invalid supertype '" + this.getParentModel().getEntityName() + "'");
				this.valid = false;
				return false;
			}
			List<IPropertyDescriptor<?>> parentProperties = this.getParentModel().getAllPropertyDescriptors();
			if (parentProperties == null) {
				LOG.warning("Model '" + this.dto.name + "' has invalid supertype '" + this.getParentModel().getEntityName() + "' with NULL propertis");
				this.valid = false;
				return false;
			}
			
			for (IPropertyDescriptor<?> pd : parentProperties) {
				if (declaredPropertiesMap.containsKey(pd.getName())) {
					//Re-defined
					continue;
				}
				this.allProperties.add(pd);
			}
		}
		
		this.nameProperties = new ArrayList<>();
		for (IPropertyDescriptor<?> pd : this.allProperties) {
			if (pd.getAsNameIndex() != null) {
				this.nameProperties.add(pd);
			}
		}

		this.nameProperties.sort(new Comparator<IPropertyDescriptor<?>>() {
			@Override
			public int compare(IPropertyDescriptor<?> o1, IPropertyDescriptor<?> o2) {
				int ret = o1.getAsNameIndex().compareTo(o2.getAsNameIndex());
				if (ret == 0) {
					return o1.getName().compareTo(o2.getName());
				}
				return ret;
			}
		});
		
		if (this.getParentModel() != null) {
			this.getParentModel().registerChild(this);
		} else {
			if (this.dto.typeDescriminatorField != null) {
				this.discriminatorField = this.dto.typeDescriminatorField.trim();
				if (this.discriminatorField.length() == 0) {
					this.discriminatorField = null;
				}
			}
			if (this.discriminatorField == null) {
				this.discriminatorField = "#type";
			}
		}
		
		this.allPropertiesByName = new HashMap<>();
		for (IPropertyDescriptor<?> d : this.getAllPropertyDescriptors()) {
			this.allPropertiesByName.put(d.getName(), d);
		}
		this.valid = true;
		return true;
	}
	
	private void registerChild(EntityModel child) {
		if (this.getParentModel() != null) {
			this.getParentModel().registerChild(child);
		}
		
		this.childModels.put(child.getEntityName(), child);
		String desc = child.dto.typeDescriminator;
		if (desc == null) {
			desc = child.getEntityName();
		}
		this.childModelsByDescriminator.put(desc, child);
	}
	
	public String getTypeDescriminator() {
		return this.dto.typeDescriminator;
	}
	
	public EntityModel getSubtype(String name) {
		EntityModel root = this.getRootModel();
		
		EntityModel ret = root.childModelsByDescriminator.get(name);
		if (ret == null) {
			ret = root.childModels.get(name);
		}
		return ret;
	}
	
	public List<EntityModel> getInstantiableTypes() {
		ArrayList<EntityModel> ret = new ArrayList<>();
		if (!this.dto.isAbstract){
			ret.add(this);
		}
		for (EntityModel c : this.childModelsByDescriminator.values()) {
			if (!c.dto.isAbstract) {
				ret.add(c);
			}
		}
		return ret;
	}
	
	public String getTypeDiscriminatorField() {
		EntityModel root = this.getRootModel();
		if (root != null) {
			return root.discriminatorField;
		} else {
			return this.discriminatorField;
		}
	}
	
	public boolean isRequiredTypeOnUpdate() {
		EntityModel root = this.getRootModel();
		if (root != null) {
			return root.dto.typeOnUpdate;
		} else {
			return this.dto.typeOnUpdate;
		}
	}
	public EntityModel getEntityType(JSONObject json) {
		EntityModel root = this.getRootModel();
		if (root.childModels.size() == 0) {
			return null;
		}
		if (!json.containsKey(root.discriminatorField)) {
			//TODO: LOG
			return null;
		}
		JSONValue jType = json.get(root.discriminatorField);
		if (jType.isString() == null) {
			//TODO: LOG
			return null;
		}
		String type = jType.isString().stringValue().trim();
		if (type.length() == 0) {
			//TODO: log
			return null;
		}
		return root.getSubtype(jType.isString().stringValue().trim());
	}
	
	public IJSObject getEntityID(JSONObject json, EntityManager em) {
		IPropertyDescriptor<?> idFld = this.getIdPropertyDescriptor();
		if (idFld == null) {
			LOG.warning("Trying to get Entity ID for model " + this.getEntityName() 
				+ "' with undefined ID field");
			return null;
		}
		if (!json.containsKey(idFld.getName())) {
			LOG.warning("Trying to get Entity ID for model " + this.getEntityName() 
				+ "' from invalid JSON: field '" + idFld.getName() +"' is absent");
			return null;
		} else {
			return idFld.getValue(json, em);
			//return idFld.convertToJSObject(json.get(idFld.getName()), em);
		}
	}
	
	public List<IPropertyDescriptor<?>> getDeclaredPropertyDescriptors() {
		this.initialize();
		return this.declaredProperties;
	}
	
	public List<IPropertyDescriptor<?>> getAllPropertyDescriptors() {
		this.initialize();
		return this.allProperties;
	}
	
	public Map<String, IPropertyDescriptor<?>> getPropertiesMap() {
		this.initialize();
		return Collections.unmodifiableMap(this.allPropertiesByName);
	}
	
	public IPropertyDescriptor<?> getPropertyDescriptor(String name) {
		return this.getPropertiesMap().get(name);
	}
 	
	public IPropertyDescriptor<?> getIdPropertyDescriptor() {
		this.initialize();
		if (this.idProperty != null) {
			return this.idProperty;
		} else {
			EntityModel parent = this.getParentModel();
			if (parent != null) {
				return parent.getIdPropertyDescriptor();
			}
		}
		return null;
	}
	
	public List<IPropertyDescriptor<?>> getNamePropertyDescriptors() {
		this.initialize();
		if (this.nameProperties != null && this.nameProperties.size() > 0) {
			return this.nameProperties;
		} else {
			EntityModel parent = this.getParentModel();
			if (parent != null) {
				return parent.getNamePropertyDescriptors();
			}
		}
		return null;
	}
	
	public EntityModel getParentModel() {
		if (this.dto.superType != null) {
			return ConfigurationRegistry.INSTANCE.getModel(this.dto.superType);
		}
		return null;
	}
	
	public EntityModel getRootModel() {
		EntityModel parent = this.getParentModel();
		return parent == null ? this : parent.getRootModel();
	}
	
	public boolean isParentType(EntityModel m) {
		if (m == null) {
			
		}
		EntityModel p = this.getParentModel();
		if (p == m) {
			return true;
		}
		return p == null ? false : p.isParentType(m);
	}
	
	private String[] getLocalizationPrefixes(String view) {
		ArrayList<String> prefixes = new ArrayList<>();
		if (view != null) {
			view = view.trim();
			if (view.length() > 0) {
				prefixes.add(this.getKey() + "." + "_view_" + view);
			}
		}
		prefixes.add(this.getKey());
		return prefixes.toArray(STRING_ARRAY);
	}
	
	public String getDisplayName(String view) {
		String ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1("name", getLocalizationPrefixes(view)));
		if (ret != null) {
			return ret;
		}
		return this.getEntityName();
	}
	
	public String getPluralDisplayName(String view) {
		String ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1("pname", getLocalizationPrefixes(view)));
		if (ret != null) {
			return ret;
		}
		return this.getEntityName() + "(s)";
	}
	
	public String getError(String code, String view) {
		
		ArrayList<String> prefixes = new ArrayList<>();
		if (view != null) {
			view = view.trim();
			if (view.length() > 0) {
				prefixes.add(this.getKey() + "." + "_view_" + view + ".errors");
			}
		}
		prefixes.add(this.getKey() + ".errors");
		prefixes.add("errors");
		
		String ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage1(code, prefixes.toArray(STRING_ARRAY)));
		if (ret != null) {
			return ret;
		}		
		return code;
	}

	public String getKey() {
		return I18N_PREFIX + "." + this.getEntityName();
	}
	
	public String getEntityName() {
		return this.dto.name;
	}
	
	public Map<String, AutoFormViewDTO> getEditorsDTO() {
		//TODO: накладывать иерархию
		if (this.dto.formViews != null) {
			return this.dto.formViews;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getEditorsDTO();
		} else {
			return null;
		}
	}
	
	public Map<String, ListViewDTO> getListViews() {
		//TODO: накладывать иерархию
		if (this.dto.listViews != null) {
			return this.dto.listViews;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getListViews();
		} else {
			return null;
		}
	}
	
	public String getServiceArgId() {
		if (this.dto.service != null && this.dto.service.argID != null) {
			return this.dto.service.argID;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgId();
		} else {
			return null;
		}
	}
	
	public String getServiceArgData() {
		if (this.dto.service != null && this.dto.service.argData != null) {
			return this.dto.service.argData;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgData();
		} else {
			return null;
		}
	}
	
	public String getServiceArgPage() {
		if (this.dto.service != null && this.dto.service.argPage != null) {
			return this.dto.service.argPage;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgPage();
		} else {
			return null;
		}
	}
	public String getServiceName() {
		if (this.dto.service != null && this.dto.service.service != null) {
			return this.dto.service.service;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceName();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodList() {
		if (this.dto.service != null && this.dto.service.methodList != null) {
			return this.dto.service.methodList;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodList();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodUpdate() {
		if (this.dto.service != null && this.dto.service.methodUpdate != null) {
			return this.dto.service.methodUpdate;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodUpdate();
		} else {
			return null;
		}
	}
	public String getServiceMethodCreate() {
		if (this.dto.service != null && this.dto.service.methodCreate != null) {
			return this.dto.service.methodCreate;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodCreate();
		} else {
			return null;
		}
	}
	public String getServiceMethodDelete() {
		if (this.dto.service != null && this.dto.service.methodDelete != null) {
			return this.dto.service.methodDelete;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodDelete();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodGet() {
		if (this.dto.service != null && this.dto.service.methodGet != null) {
			return this.dto.service.methodGet;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodGet();
		} else {
			return null;
		}
	}
	
	public String getServiceArgPageSize() {
		if (this.dto.service != null && this.dto.service.argPageSize != null) {
			return this.dto.service.argPageSize;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgPageSize();
		} else {
			return null;
		}
	}
	
	public Map<String, String> getServiceCreateArgs() {
		if (this.dto.service != null && this.dto.service.createArgs != null) {
			return this.dto.service.createArgs;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceCreateArgs();
		} else {
			return null;
		}
	}

	public boolean canDelete() {
		return true; //TODO :!!!
	}

	public boolean canCreate() {
		return true; //TODO :!!!
	}
}
