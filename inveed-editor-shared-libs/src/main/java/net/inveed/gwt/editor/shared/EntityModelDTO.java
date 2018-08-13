package net.inveed.gwt.editor.shared;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
@JsonPropertyOrder(value={EntityModelDTO.P_NAME, 
		EntityModelDTO.P_SUPERTYPE, 
		EntityModelDTO.P_ISABSTRACT, 
		EntityModelDTO.P_TYPE_DESC, 
		EntityModelDTO.P_TYPE_DESC_FLD,
		EntityModelDTO.P_SERVICE, 
		EntityModelDTO.P_PROPS})
public class EntityModelDTO implements Serializable {
	private static final long serialVersionUID = 1119934767404990227L;

	static final String P_NAME = "name";
	static final String P_SUPERTYPE = "superType";
	static final String P_ISABSTRACT = "isAbstract";
	static final String P_TYPE_DESC = "typeDescriminator";
	static final String P_TYPE_DESC_FLD = "typeDescriminatorField";
	static final String P_TYPE_ON_UPDATE = "typeOnUpdate";
	static final String P_SERVICE = "service";
	static final String P_EDITORS = "editors";
	static final String P_PROPS = "properties";
	
	@JsonProperty(P_NAME)
	public final String name;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_SUPERTYPE)
	public final String superType;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_ISABSTRACT)
	public final boolean isAbstract;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_TYPE_DESC)
	public final String typeDescriminator;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_TYPE_DESC_FLD)
	public final String typeDescriminatorField;
	
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@JsonProperty(P_TYPE_ON_UPDATE)
	public final boolean typeOnUpdate;
		
	@JsonProperty(P_SERVICE)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final EntityAccessServiceDTO service;
	
	@JsonProperty(P_EDITORS)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final EntityEditorsDTO editors;
	
	@JsonProperty(P_PROPS)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Map<String, PropertyModelDTO> properties;
	
	public EntityModelDTO(
			@JsonProperty(P_NAME) String entityName,
			@JsonProperty(P_TYPE_DESC) String typeDescriminator,
			@JsonProperty(P_SUPERTYPE) String superType,
			@JsonProperty(P_ISABSTRACT) boolean isAbstract,
			@JsonProperty(P_TYPE_DESC_FLD) String typeDescriminatorField,
			@JsonProperty(P_TYPE_ON_UPDATE) boolean typeOnUpdate,
			@JsonProperty(P_SERVICE) EntityAccessServiceDTO service,
			@JsonProperty(P_EDITORS) EntityEditorsDTO editors,
			@JsonProperty(P_PROPS) Map<String, PropertyModelDTO> properties) {
		this.name = entityName;
		this.typeDescriminator = typeDescriminator;
		this.superType = superType;
		this.isAbstract = isAbstract;
		this.typeDescriminatorField = typeDescriminatorField;
		this.typeOnUpdate = typeOnUpdate;
		this.service = service;
		this.properties = properties;
		this.editors = editors;
	}
}
