package net.inveed.gwt.editor.shared.lists;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ListViewDTO {
	private static final String P_COLUMNS = "columns";
	
	@JsonProperty(P_COLUMNS)
	public final PropertyInListViewDTO[] columns;
	
	public ListViewDTO(
			@JsonProperty(P_COLUMNS) PropertyInListViewDTO[] columns) {
		this.columns = columns;
	}
}
