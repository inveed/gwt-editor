package net.inveed.gwt.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.inveed.gwt.editor.shared.EntityEditorDTO;
import net.inveed.gwt.editor.shared.forms.EditorSectionDTO;
import net.inveed.gwt.editor.shared.forms.EditorPanelDTO;
import net.inveed.gwt.server.annotations.editor.UIEditor;
import net.inveed.gwt.server.annotations.editor.UIEditorSection;
import net.inveed.gwt.server.annotations.editor.UIEditorPanel;

public class EntityEditorBuilder {
	public Integer width;
	public Integer heigh;
	
	public final List<EntityEditorSectionBuilder> scBuilders = new ArrayList<>();
	public final HashMap<String, EntityEditorPanelBuilder> panelBuilders = new HashMap<>();
	
	public void registerAnnotation(UIEditorPanel a) {
		String name = a.name().trim();
		if (name.length() < 1) {
			return; //TODO:!!!
		}
		EntityEditorPanelBuilder b = panelBuilders.get(name);
		if (b == null) {
			b = new EntityEditorPanelBuilder();
			b.name = name;
		}
		
		b.order = a.order();
		panelBuilders.put(name, b);
	}
	
	public EntityEditorPanelBuilder getPanel(String name) {
		name = name.trim();
		if (name.length() < 1) {
			return null; //TODO:!!!
		}
		EntityEditorPanelBuilder b = panelBuilders.get(name);
		if (b == null) {
			b = new EntityEditorPanelBuilder();
			b.name = name;
			panelBuilders.put(name, b);
		} 
		return b;
	}
	
	public void registerAnnotation(UIEditorSection a) {
		String name = a.name().trim();
		if (name.length() < 1) {
			return; //TODO:!!!
		}
		for (EntityEditorSectionBuilder b : scBuilders) {
			if (b.name.equals(name)) {
				//TODO: log!
				return;
			}
		}
		EntityEditorSectionBuilder b = new EntityEditorSectionBuilder();
		b.name = name;
		b.order = a.order();
		if (a.parent().length() == 0) {
			scBuilders.add(b);
		} else {
			this.getPanel(a.parent()).sections.add(b);
		}
	}
	
	public void registerAnnotation(UIEditor e) {
		this.width = e.width();
		this.heigh = e.heigh();
		for (UIEditorSection a : e.containers()) {
			this.registerAnnotation(a);
		}
		for (UIEditorPanel a : e.tabContainers()) {
			this.registerAnnotation(a);
		}
	}
	
	public EntityEditorDTO build() {
		EditorPanelDTO[] tcdtos = null;
		if (panelBuilders.size() > 0) {
			tcdtos = new EditorPanelDTO[panelBuilders.size()];
			int i = 0;
			for (String k : panelBuilders.keySet()) {
				tcdtos[i++] = panelBuilders.get(k).build();
			}
		}
		
		EditorSectionDTO[] scdtos = null;
		if (this.scBuilders.size() > 0) {
			scdtos = new EditorSectionDTO[this.scBuilders.size()];
			for (int i =0; i < scdtos.length; i ++) {
				scdtos[i] = this.scBuilders.get(i).build();
			}
		}
		return new EntityEditorDTO(this.width, this.heigh, tcdtos, scdtos);
	}

}
