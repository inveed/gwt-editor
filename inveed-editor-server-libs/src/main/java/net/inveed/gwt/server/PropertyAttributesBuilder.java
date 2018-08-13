package net.inveed.gwt.server;

import net.inveed.gwt.editor.shared.PropertyAttributesDTO;

public class PropertyAttributesBuilder {

	public Integer asNameIndex;
	public Double min;
	public Double max;
	public Boolean required;
	public Boolean readonly;
	public String regexp;
	public String regexpError;
	
	public String referencedEntityName;
	public String referencedEnumName;

	public String startWith;
	public String mappedBy;
	
	
	public PropertyAttributesDTO build() {
		boolean isNull = 
				this.asNameIndex == null &&
				this.min == null &&
				this.max == null &&
				this.required == null &&
				this.readonly == null &&
				this.regexp == null &&
				this.referencedEntityName == null &&
				this.startWith == null &&
				this.mappedBy == null &&
				this.referencedEnumName == null;
		
		if (isNull) {
			return null;
		}
		return new PropertyAttributesDTO(this.asNameIndex, min, max, required, readonly, regexp, this.regexpError, referencedEntityName, startWith, mappedBy, referencedEnumName);
	}
}
