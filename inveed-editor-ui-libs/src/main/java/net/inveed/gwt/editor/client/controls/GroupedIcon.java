package net.inveed.gwt.editor.client.controls;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;

import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.base.mixin.ColorsMixin;
import gwt.material.design.client.base.mixin.CssNameMixin;
import gwt.material.design.client.base.mixin.ToggleStyleMixin;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.CssName;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;

public class GroupedIcon extends MaterialWidget {
	private CssNameMixin<GroupedIcon, IconPosition> positionMixin;
    private CssNameMixin<GroupedIcon, IconSize> sizeMixin;
    private ToggleStyleMixin<GroupedIcon> prefixMixin;
    private ColorsMixin<GroupedIcon> iconColorMixin;

    
	public GroupedIcon() {
		this.setElement(this.createElement());
		this.setInitialClasses(CssName.MATERIAL_ICONS);
		this.setHeight("34px");
		this.setMargin(4);
		getElement().getStyle().setCursor(Style.Cursor.DEFAULT);
	}

    public GroupedIcon(IconType iconType) {
        this();
        setIconType(iconType);
    }

    public GroupedIcon(IconType iconType, Color bgColor) {
        this();
        setIconType(iconType);
        setBackgroundColor(bgColor);
    }

    public GroupedIcon(IconType iconType, Color textColor, Color bgColor) {
        this();
        setIconType(iconType);
        setTextColor(textColor);
        setBackgroundColor(bgColor);
    }
	protected Element createElement() {
		return Document.get().createElement("i");
	}

	public IconType getIconType() {
        return IconType.fromStyleName(getElement().getInnerText());
    }
	
    public void setIconType(IconType icon) {
        getElement().setInnerText(icon.getCssName());
    }

    public void setIconPosition(IconPosition position) {
        getPositionMixin().setCssName(position);
    }

    public void setIconSize(IconSize size) {
        getSizeMixin().setCssName(size);
    }

    public IconSize getIconSize() {
        return getSizeMixin().getCssName();
    }

    public void setIconColor(Color iconColor) {
        getIconColorMixin().setTextColor(iconColor);
    }

    public Color getIconColor() {
        return getIconColorMixin().getTextColor();
    }

    public void setIconFontSize(double size, Style.Unit unit) {
        getElement().getStyle().setFontSize(size, unit);
    }

    public void setIconPrefix(boolean prefix) {
        getPrefixMixin().setOn(prefix);
    }

    public boolean isIconPrefix() {
        return getPrefixMixin().isOn();
    }

    protected CssNameMixin<GroupedIcon, IconPosition> getPositionMixin() {
        if (positionMixin == null) {
            positionMixin = new CssNameMixin<>(this);
        }
        return positionMixin;
    }

    protected CssNameMixin<GroupedIcon, IconSize> getSizeMixin() {
        if (sizeMixin == null) {
            sizeMixin = new CssNameMixin<>(this);
        }
        return sizeMixin;
    }

    protected ToggleStyleMixin<GroupedIcon> getPrefixMixin() {
        if (prefixMixin == null) {
            prefixMixin = new ToggleStyleMixin<>(this, CssName.PREFIX);
        }
        return prefixMixin;
    }

    protected ColorsMixin<GroupedIcon> getIconColorMixin() {
        if (iconColorMixin == null) {
            iconColorMixin = new ColorsMixin<>(this);
        }
        return iconColorMixin;
    }
}
