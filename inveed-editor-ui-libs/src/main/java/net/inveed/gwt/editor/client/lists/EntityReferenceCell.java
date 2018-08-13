package net.inveed.gwt.editor.client.lists;

import java.util.UUID;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;

import net.inveed.gwt.editor.client.model.JSEntity;
import net.inveed.gwt.editor.client.utils.IError;
import net.inveed.gwt.editor.client.utils.Promise;

public class EntityReferenceCell extends AbstractCell<JSEntity> {
	interface Template extends SafeHtmlTemplates {
		@Template("<div id=\"{0}\">...Loading...</div>")
		SafeHtml uninitialized(String id);
		@Template("<div>-- NOT SET --</div>")
		SafeHtml undef();
		@Template("<div>{0}</div>")
		SafeHtml initialized(String text);
	}

	private static Template template;
	private final String idPrefix;
	private int order = 0;

	/**
	 * Construct a new EntityReferenceCell.
	 */
	public EntityReferenceCell() {
		if (template == null) {
			template = GWT.create(Template.class);
		}
		this.idPrefix = "c_" + UUID.randomUUID().toString().replaceAll("-", "");
	}

	@Override
	public void render(Context context, JSEntity value, SafeHtmlBuilder sb) {
		if (value == null) {
			sb.append(template.undef());
		} else {
			if (value.isInitialized()) {
				sb.append(template.initialized(value.getDisplayValue()));
			} else {
				final String id = this.idPrefix + "_" + order;
				order++;
				sb.append(template.uninitialized(id));
				Promise<Void, IError> p = value.load();
				p.thenApply((v)->{
					Element divElement = DOM.getElementById(id);
					if (divElement == null) {
						return null;
					}
					divElement.setInnerText(value.getDisplayValue());;
					return null;
				});
				p.onError((v,e)->{
					return null;
				});
			}
		}
	}

}
