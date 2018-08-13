package net.inveed.gwt.editor.client.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.nmorel.gwtjackson.client.ObjectMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.client.i18n.Localizer;
import net.inveed.gwt.editor.client.model.properties.BooleanPropertyModel;
import net.inveed.gwt.editor.client.model.properties.DatePropertyModel;
import net.inveed.gwt.editor.client.model.properties.DurationPropertyModel;
import net.inveed.gwt.editor.client.model.properties.EntityReferencePropertyModel;
import net.inveed.gwt.editor.client.model.properties.EnumPropertyModel;
import net.inveed.gwt.editor.client.model.properties.FloatPropertyModel;
import net.inveed.gwt.editor.client.model.properties.IPropertyDesc;
import net.inveed.gwt.editor.client.model.properties.IntegerFieldModel;
import net.inveed.gwt.editor.client.model.properties.IntegerIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.LinkedEntitiesListPropertyModel;
import net.inveed.gwt.editor.client.model.properties.NetworkAddressPropertyModel;
import net.inveed.gwt.editor.client.model.properties.SecretFieldPropertyModel;
import net.inveed.gwt.editor.client.model.properties.StringIDPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TextPropertyModel;
import net.inveed.gwt.editor.client.model.properties.TimestampPropertyModel;
import net.inveed.gwt.editor.client.types.IJSObject;
import net.inveed.gwt.editor.client.types.JSMap;
import net.inveed.gwt.editor.client.utils.JsonHelper;
import net.inveed.gwt.editor.shared.EntityEditorsDTO;
import net.inveed.gwt.editor.shared.EntityModelDTO;
import net.inveed.gwt.editor.shared.FieldType;
import net.inveed.gwt.editor.shared.PropertyModelDTO;

public final class EntityModel {	
	public static interface EntityModelMapper extends ObjectMapper<EntityModelDTO> {}

	private static final String I18N_PREFIX = "entities";
	
	private final EntityModelDTO nativeModel;
	
	private List<IPropertyDesc<?>> idProperties = new ArrayList<>();
	private List<IPropertyDesc<?>> nameProperties = new ArrayList<>();
	private List<IPropertyDesc<?>> allProperties;
	private List<IPropertyDesc<?>> declaredProperties;
	
	private HashMap<String, IPropertyDesc<?>> allPropertiesByName;
	
	private HashMap<String, EntityListView> listViews = new HashMap<>();
	private HashMap<String, EntityFormView> formViews = new HashMap<>();
	
	private HashMap<String, EntityModel> childModels = new HashMap<>();
	private HashMap<String, EntityModel> childModelsByDescriminator = new HashMap<>();
	
	private String discriminatorField;
	
	public EntityModel(EntityModelDTO model, ConfigurationRegistry registry) {
		this.nativeModel = model;
	}
	
	void initialize() {
		if (this.allProperties != null) {
			return;
		}

		HashMap<String, IPropertyDesc<?>> nameProperties = new HashMap<>();
		ArrayList<IPropertyDesc<?>> declaredProperties = new ArrayList<>();
		for (String fname : this.nativeModel.properties.keySet()) {
			PropertyModelDTO fm = this.nativeModel.properties.get(fname);
			IPropertyDesc<?> fld = this.createPropertyDescriptor(fname, fm);
			declaredProperties.add(fld);
			
			if (fm.type == FieldType.ID_INTEGER || fm.type == FieldType.ID_STRING) {
				this.idProperties.add(fld);
			}
			
			if (fm.attributes.asNameIndex != null) {
				nameProperties.put(fld.getName(), fld);
			}
		}
		
		if (this.getParentModel() != null && this.getParentModel().getNameProperties() != null) {
			List<IPropertyDesc<?>> parentNameProps = this.getParentModel().getNameProperties();
			for (IPropertyDesc<?> pd : parentNameProps) {
				if (nameProperties.containsKey(pd.getName()))
					continue;
				nameProperties.put(pd.getName(), pd);
			}
		}
		
		this.nameProperties = new ArrayList<>(nameProperties.values());
		this.nameProperties.sort(new Comparator<IPropertyDesc<?>>() {

			@Override
			public int compare(IPropertyDesc<?> o1, IPropertyDesc<?> o2) {
				int ret = o1.getAsNameIndex().compareTo(o2.getAsNameIndex());
				if (ret == 0) {
					return o1.getName().compareTo(o2.getName());
				}
				return ret;
			}
		});
		
		ArrayList<IPropertyDesc<?>> allFields = new ArrayList<>(declaredProperties);
		
		if (this.getParentModel() != null) {
			allFields.addAll(this.getParentModel().getFields());
		}
		
		this.declaredProperties = declaredProperties;
		this.allProperties = allFields;
		
		if (this.getParentModel() != null) {
			this.getParentModel().registerChild(this);
		} else {
			if (this.nativeModel.typeDescriminatorField != null) {
				this.discriminatorField = this.nativeModel.typeDescriminatorField.trim();
				if (this.discriminatorField.length() == 0) {
					this.discriminatorField = null;
				}
			}
			if (this.discriminatorField == null) {
				this.discriminatorField = "#type";
			}
		}
	}
	
	private void registerChild(EntityModel child) {
		if (this.getParentModel() != null) {
			this.getParentModel().registerChild(child);
		}
		
		this.childModels.put(child.getEntityName(), child);
		String desc = child.nativeModel.typeDescriminator;
		if (desc == null) {
			desc = child.getEntityName();
		}
		this.childModelsByDescriminator.put(desc, child);
	}
	
	public String getTypeDescriminator() {
		return this.nativeModel.typeDescriminator;
	}
	
	public EntityListView getListView(String name) {
		EntityListView ret = this.listViews.get(name);
		if (ret != null) {
			return ret;
		}
		ret = new EntityListView(this, name);
		this.listViews.put(name, ret);
		return ret;
	}
	
	public EntityFormView getFormView(String name) {
		EntityFormView ret = this.formViews.get(name);
		if (ret != null) {
			return ret;
		}
		ret = new EntityFormView(this, name);
		this.formViews.put(name, ret);
		return ret;
	}
	
	private IPropertyDesc<?> createPropertyDescriptor(String name, PropertyModelDTO field) {
		switch (field.type) {
		case ID_INTEGER:
			return new IntegerIDPropertyModel(field, name, this);
		case ID_STRING:
			return new StringIDPropertyModel(field, name, this);
		case ADDR_HOST:
		case ADDR_IP:
			return new NetworkAddressPropertyModel(field, name, this);
		case BOOLEAN:
			return new BooleanPropertyModel(field, name, this);
		case DATE:
			return new DatePropertyModel(field, name, this);
		case DURATION_ISO:
		case DURATION_MIN:
		case DURATION_SECONDS:
		case DURATION_MS:
			return new DurationPropertyModel(field, name, this);
		case ENUM:
			return new EnumPropertyModel(field, name, this);
		case FLOAT:
			return new FloatPropertyModel(field, name, this);
		case INTEGER:
			return new IntegerFieldModel(field, name, this);
		case OBJECT_REF:
			return new EntityReferencePropertyModel(field, name, this);
		case PASSWORD:
		case TEXT:
		case TEXT_LONG:
		case URL:
			return new TextPropertyModel(field, name, this);
		case TIMESTAMP:
		case TIMESTAMP_MS:
			return new TimestampPropertyModel(field, name, this);
		case LINKED_ENTITIES_LIST:
			return new LinkedEntitiesListPropertyModel(field, name, this);
		case SECRET_KEY:
		case BINARY_KEY:
			return new SecretFieldPropertyModel(field, name, this);
		default:
			return null;

		}
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
		if (!this.nativeModel.isAbstract){
			ret.add(this);
		}
		for (EntityModel c : this.childModelsByDescriminator.values()) {
			if (!c.nativeModel.isAbstract) {
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
			return root.nativeModel.typeOnUpdate;
		} else {
			return this.nativeModel.typeOnUpdate;
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
		List<IPropertyDesc<?>> idFields = this.getIdFields();
		if (idFields == null) {
			return null;
		}
		if (idFields.size() == 0) {
			return null;
		}
		if (idFields.size() == 1) {
			IPropertyDesc<?> idFld = idFields.get(0);
			if (!json.containsKey(idFld.getName())) {
				return null;
			} else {
				return idFld.convertToJSObject(json.get(idFld.getName()), em);
			}
		} else {
			JSMap<IJSObject> ret = new JSMap<>();
			for (int i = 0; i < idFields.size(); i ++) {
				IPropertyDesc<?> idFld = idFields.get(0);
				if (!json.containsKey(idFld.getName())) {
					return null;
				} else {
					ret.put(idFld.getName(), idFld.convertToJSObject(json.get(idFld.getName()), em));
				}
			}
			return ret;
		}
	}
	
	public IJSObject getEntityIDFromValue(JSONValue json, EntityManager em) {
		List<IPropertyDesc<?>> idFields = this.getIdFields();
		if (idFields == null) {
			return null;
		}
		if (idFields.size() == 0) {
			return null;
		}
		if (idFields.size() == 1) {
			IPropertyDesc<?> idFld = idFields.get(0);
			return idFld.convertToJSObject(json, em);
		} else {
			if (json.isObject() != null) {
				return this.getEntityID(json.isObject(), em);
			} else {
				return null;
			}
		}
	}
	
	public List<IPropertyDesc<?>> getDeclaredFields() {
		this.initialize();
		return this.declaredProperties;
	}
	
	public List<IPropertyDesc<?>> getFields() {
		this.initialize();
		return this.allProperties;
	}
	
	public Map<String, IPropertyDesc<?>> getFieldsMap() {
		if (this.allPropertiesByName == null) {
			this.allPropertiesByName = new HashMap<>();
			for (IPropertyDesc<?> d : this.getFields()) {
				this.allPropertiesByName.put(d.getName(), d);
			}
		}
		return this.allPropertiesByName;
	}
	
	public IPropertyDesc<?> getField(String name) {
		return this.getFieldsMap().get(name);
	}
 	
	public IPropertyDesc<?> findProperty(String name) {
		for (IPropertyDesc<?> p : this.getFields()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	public List<IPropertyDesc<?>> getIdFields() {
		this.initialize();
		if (this.idProperties != null && this.idProperties.size() > 0) {
			return idProperties;
		} else {
			EntityModel parent = this.getParentModel();
			if (parent != null) {
				return parent.getIdFields();
			}
		}
		return null;
	}
	
	public List<IPropertyDesc<?>> getNameProperties() {
		this.initialize();
		if (this.nameProperties != null && this.nameProperties.size() > 0) {
			return this.nameProperties;
		} else {
			EntityModel parent = this.getParentModel();
			if (parent != null) {
				return parent.getNameProperties();
			}
		}
		return null;
	}
	
	public EntityModel getParentModel() {
		if (this.nativeModel.superType != null) {
			return ConfigurationRegistry.INSTANCE.getModel(this.nativeModel.superType);
		}
		return null;
	}
	
	public EntityModel getRootModel() {
		EntityModel parent = this.getParentModel();
		if (parent == null) {
			return this;
		} else {
			return parent.getRootModel();
		}
	}
	
	public boolean isParentType(EntityModel m) {
		EntityModel p = this.getParentModel();
		if (p == null) {
			return false;
		}
		if (p == m) {
			return true;
		}
		return p.isParentType(m);
	}
	public static EntityModel parseModel(String json, ConfigurationRegistry registry) {
		GWT.log("Parsing model for json: " + json);
		EntityModelMapper mapper = GWT.create(EntityModelMapper.class);
		EntityModelDTO model = mapper.read(json);
		
		GWT.log("Model parsed for entity type " + model.name);
		return new EntityModel(model, registry);
	}
	
	public String getDisplayName(String view) {
		String ret = null;
		if (view != null) {
			view = view.trim();
			if (view.length() > 0) {
				ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getKey() + "." + "views" + "." + view + "." + "name"));
				if (ret != null) {
					return ret;
				}
			}
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getKey() + "." + "name"));
		if (ret != null) {
			return ret;
		}
		return this.getEntityName();
	}
	
	public String getPluralDisplayName(String view) {
		String ret = null;
		if (view != null) {
			view = view.trim();
			if (view.length() > 0) {
				ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getKey() + "." + "views" + "." + view + "." + "pname"));
			}
		}
		ret = JsonHelper.safeGetString(Localizer.INSTANCE.getMessage(this.getKey() + "." + "pname"));
		if (ret != null) {
			return ret;
		}
		return this.getEntityName() + "(s)";
	}

	public String getKey() {
		return I18N_PREFIX + "." + this.getEntityName();
	}
	
	public String getEntityName() {
		return this.nativeModel.name;
	}
	
	public EntityEditorsDTO getEditorsDTO() {
		if (this.nativeModel.editors != null) {
			return this.nativeModel.editors;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getEditorsDTO();
		} else {
			return null;
		}
	}
	
	public String getServiceArgId() {
		if (this.nativeModel.service != null && this.nativeModel.service.argID != null) {
			return this.nativeModel.service.argID;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgId();
		} else {
			return null;
		}
	}
	
	public String getServiceArgData() {
		if (this.nativeModel.service != null && this.nativeModel.service.argData != null) {
			return this.nativeModel.service.argData;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgData();
		} else {
			return null;
		}
	}
	
	public String getServiceArgPage() {
		if (this.nativeModel.service != null && this.nativeModel.service.argPage != null) {
			return this.nativeModel.service.argPage;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgPage();
		} else {
			return null;
		}
	}
	public String getServiceName() {
		if (this.nativeModel.service != null && this.nativeModel.service.service != null) {
			return this.nativeModel.service.service;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceName();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodList() {
		if (this.nativeModel.service != null && this.nativeModel.service.methodList != null) {
			return this.nativeModel.service.methodList;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodList();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodUpdate() {
		if (this.nativeModel.service != null && this.nativeModel.service.methodUpdate != null) {
			return this.nativeModel.service.methodUpdate;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodUpdate();
		} else {
			return null;
		}
	}
	public String getServiceMethodCreate() {
		if (this.nativeModel.service != null && this.nativeModel.service.methodCreate != null) {
			return this.nativeModel.service.methodCreate;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodCreate();
		} else {
			return null;
		}
	}
	public String getServiceMethodDelete() {
		if (this.nativeModel.service != null && this.nativeModel.service.methodDelete != null) {
			return this.nativeModel.service.methodDelete;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodDelete();
		} else {
			return null;
		}
	}
	
	public String getServiceMethodGet() {
		if (this.nativeModel.service != null && this.nativeModel.service.methodGet != null) {
			return this.nativeModel.service.methodGet;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceMethodGet();
		} else {
			return null;
		}
	}
	
	public String getServiceArgPageSize() {
		if (this.nativeModel.service != null && this.nativeModel.service.argPageSize != null) {
			return this.nativeModel.service.argPageSize;
		} else if (this.getParentModel() != null) {
			return this.getParentModel().getServiceArgPageSize();
		} else {
			return null;
		}
	}
	
	public Map<String, String> getServiceCreateArgs() {
		if (this.nativeModel.service != null && this.nativeModel.service.createArgs != null) {
			return this.nativeModel.service.createArgs;
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
