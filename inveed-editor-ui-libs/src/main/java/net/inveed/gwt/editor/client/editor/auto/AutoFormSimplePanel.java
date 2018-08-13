package net.inveed.gwt.editor.client.editor.auto;

public class AutoFormSimplePanel extends AutoFormPanel {
	private int order;
	
	public AutoFormSimplePanel() {
		super();
	}

	@Override
	protected void initWidget() {
		this.initWidget(this.getForm());
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
