package net.inveed.gwt.editor.shared.properties;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StringIdPropertyDTO extends AbstractPropertyDTO {
	private static final long serialVersionUID = 3953864569161542642L;

	public static final String TYPE = "stringId";
	
	public StringIdPropertyDTO(
			@JsonProperty(AbstractPropertyDTO.P_ASNAMEIDX) Integer asNameIndex) {
		super(asNameIndex);
	}

	@JsonIgnore
	@Override
	public String getType() {
		return TYPE;
	}
}
