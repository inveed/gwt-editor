package net.inveed.gwt.editor.shared.properties;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
		  use = JsonTypeInfo.Id.MINIMAL_CLASS,
		  include = JsonTypeInfo.As.PROPERTY, 
		  property = "#type")
public abstract class AbstractPropertyDTO implements Serializable {
	private static final long serialVersionUID = -8967264227948932045L;

	public static final String P_ASNAMEIDX = "asNameIndex";
	
	@JsonProperty(index=20, value=P_ASNAMEIDX)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public final Integer asNameIndex;

	public abstract String getType();

	public AbstractPropertyDTO(Integer asNameIndex) {
		this.asNameIndex = asNameIndex;
	}
}
