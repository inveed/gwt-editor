package net.inveed.gwt.editor.shared;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import net.inveed.gwt.editor.shared.forms.EditorPanelDTO;
import net.inveed.gwt.editor.shared.forms.EditorSectionDTO;

@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE) 
public class EntityEditorDTO {
	static final String P_WIDTH = "width";
	static final String P_HEIGH = "heigh";
	static final String P_TCONT = "panels";
	static final String P_RSECT = "rootSections";
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_WIDTH)
	public final Integer width;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_HEIGH)
	public final Integer heigh;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_TCONT)
	public final EditorPanelDTO[] panels;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty(P_RSECT)
	public final EditorSectionDTO[] rootSections;
	
	
	public EntityEditorDTO(
			@JsonProperty(P_WIDTH) Integer width, 
			@JsonProperty(P_HEIGH) Integer heigh,
			@JsonProperty(P_TCONT) EditorPanelDTO[] tabContainers,
			@JsonProperty(P_RSECT) EditorSectionDTO[] rootSections
			) {
		this.width = width;
		this.heigh = heigh;
		this.panels = tabContainers;
		this.rootSections = rootSections;
	}
	
	public EntityEditorDTO merge(EntityEditorDTO dfl) {
		Integer width = null;
		Integer heigh = null;
		HashMap<String, EditorPanelDTO> tabContainers = new HashMap<>();
		HashMap<String, EditorSectionDTO> rootSections = new HashMap<>();
		
		if (this.width != null && this.width > 0) {
			width = this.width;
		} else {
			width = dfl.width;
		}
		
		if (this.heigh != null && this.heigh > 0) {
			heigh = this.heigh;
		} else {
			heigh = dfl.heigh;
		}
		
		
		if (dfl.panels != null) {
			for (EditorPanelDTO dto : dfl.panels) {
				tabContainers.put(dto.name, dto);
			}
		}
		if (dfl.rootSections != null) {
			for (EditorSectionDTO s : dfl.rootSections) {
				rootSections.put(s.name, s);
			}
		}
		
		if (this.panels != null) {
			for (EditorPanelDTO dto : this.panels) {
				if (tabContainers.containsKey(dto.name)) {
					tabContainers.put(dto.name, dto.merge(tabContainers.get(dto.name)));
				} else {
					tabContainers.put(dto.name, dto);
				}
			}
		}
		
		if (this.rootSections != null) {
			for (EditorSectionDTO s : this.rootSections) {
				rootSections.put(s.name, s);
			}
		}
		
		return new EntityEditorDTO(width, heigh, 
				tabContainers.values().toArray(new EditorPanelDTO[0]),
				rootSections.values().toArray(new EditorSectionDTO[0]));
	}

}
