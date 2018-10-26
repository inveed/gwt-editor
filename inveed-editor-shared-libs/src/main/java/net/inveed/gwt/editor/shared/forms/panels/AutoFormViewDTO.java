package net.inveed.gwt.editor.shared.forms.panels;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import net.inveed.gwt.editor.shared.forms.rows.IEditorRowDTO;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public final class AutoFormViewDTO extends AbstractEditorPanelDTO implements IEditorRowDTO {
	static final String P_WIDTH = "width";
	static final String P_HEIGH = "heigh";
	static final String P_VIEW_NAME = "viewName";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_WIDTH)
	public final Integer width;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_HEIGH)
	public final Integer heigh;
	
	@JsonProperty(P_VIEW_NAME)
	public final String viewName;
	
	public AutoFormViewDTO(
			@JsonProperty(AbstractEditorPanelDTO.P_ROWS) IEditorRowDTO[] rows,
			@JsonProperty(P_VIEW_NAME) String viewName,
			@JsonProperty(P_WIDTH) Integer width, 
			@JsonProperty(P_HEIGH) Integer heigh
			) {
		super(rows);
		this.viewName = viewName;
		this.width = width;
		this.heigh = heigh;
	}
	
	@Override
	public boolean equalsTo(IEditorRowDTO obj) {
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		AutoFormViewDTO dto = (AutoFormViewDTO) obj;
		return equalsTo(dto);
	}
	
	public boolean equalsTo(AutoFormViewDTO dto) {
		if (dto == null) {
			return false;
		}
		if (dto.rows == null && this.rows == null) {
			return true;
		} else if (dto.rows == null || this.rows == null) {
			return false;
		} else if (dto.rows.length != this.rows.length) {
			return false;
		}
		for (int i = 0; i < this.rows.length; i++) {
			IEditorRowDTO rcdto = this.rows[i];
			IEditorRowDTO rodto = dto.rows[i];
			if (!rcdto.equalsTo(rodto)) {
				return false;
			}
		}
		return true;
	}
}
