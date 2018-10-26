package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EntityListPropertyDTO extends AbstractPropertyDTO {
	private static final long serialVersionUID = -4083893238785951957L;

	private static final String P_MAPPED_BY = "mappedBy";
	private static final String P_RENTITY = "referencedEntity";
		
	public static final String TYPE = "entityList";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_RENTITY)
	public final String referencedEntityName;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_MAPPED_BY)
	public final String mappedBy;
	
	public EntityListPropertyDTO(
			String referencedEntityName,
			String  mappedBy) {
		this(null, referencedEntityName, mappedBy);
	}
	
	public EntityListPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex,
			@JsonProperty(P_RENTITY) String referencedEntityName,
			@JsonProperty(P_MAPPED_BY) String  mappedBy) {
		super(asNameIndex);
		this.mappedBy = mappedBy;
		this.referencedEntityName = referencedEntityName;
	}
	
	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
