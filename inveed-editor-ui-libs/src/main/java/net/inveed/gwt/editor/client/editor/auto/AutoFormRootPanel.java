package net.inveed.gwt.editor.client.editor.auto;

import java.util.HashMap;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.constants.ColumnSize;
import org.gwtbootstrap3.client.ui.html.Div;

public class AutoFormRootPanel extends AutoFormPanel {
	private HashMap<String, AutoFormSimplePanel> tabs = new HashMap<>();
	private Column colMain;
	private Column colTabs;
	public AutoFormRootPanel() {
		super();
	}
	
	@Override
	protected void initWidget() {
		Div w = new Div();
		Row row1 = new Row();
		this.colMain = new Column(ColumnSize.XS_12);
		row1.add(this.colMain);
		
		Row row2 = new Row();
		this.colTabs = new Column(ColumnSize.XS_12);
		row2.add(this.colTabs);
		
		w.add(row1);
		w.add(row2);
		
		this.initWidget(w);
	}
	
	public void addTab(String code, AutoFormSimplePanel panel) {
		this.tabs.put(code, panel);
	}
	
	@Override
	public void build() {
		super.build();
		this.colMain.add(this.getForm());
		
		if (this.tabs.size() == 0) {
			this.colTabs.setVisible(false);
			return;
		} else if (this.tabs.size() > 1) {
			NavTabs nt = new NavTabs();
			TabContent tc = new TabContent();
			
			this.colTabs.add(nt);
			this.colTabs.add(tc);
			
			boolean first = true;
			for (String tcode : this.tabs.keySet()) {
				AutoFormSimplePanel tab = this.tabs.get(tcode);
				tab.build();
	
				TabListItem tli = new TabListItem(tcode);
				TabPane pane = new TabPane();
				pane.add(tab);
				//pane.setId(uid);
				tli.setDataTargetWidget(pane);
				
				if (first) {
					tli.setActive(true);
					pane.setActive(true);
					first = false;
				}
				nt.add(tli);
				tc.add(pane);
			}
		} else {
			AutoFormSimplePanel tab = this.tabs.entrySet().iterator().next().getValue();
			tab.build();
			this.colTabs.add(tab);
		}
	}
}
