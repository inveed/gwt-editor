package net.inveed.gwt.server.propbuilders;

import java.lang.annotation.Annotation;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;

import com.fasterxml.jackson.annotation.JsonProperty;

import net.inveed.commons.reflection.BeanPropertyDesc;
import net.inveed.gwt.server.annotations.UIAsName;

public abstract class AbstractPropertyBuilder<T extends Annotation> implements IPropertyBuiler<T> {
	private T annotation;
	private String propertyName;
	private BeanPropertyDesc property;
	private Integer asNameIndex;
	
	@Override
	public boolean prepare(BeanPropertyDesc prop) {
		this.property = prop;
		this.annotation = prop.getAnnotation(getAnnotationType());
		if (this.annotation == null) {
			//TODO: LOG
		}
		String forcedName = this.getForcedName();
		if (forcedName != null) {
			forcedName = forcedName.trim();
			if (forcedName.length() > 0) {
				this.propertyName = forcedName;
			}
		}
		if (this.propertyName == null) {
			JsonProperty jsonAnnotation = prop.getAnnotation(JsonProperty.class);
			if (jsonAnnotation != null) {
				this.propertyName = jsonAnnotation.value();
			} else {
				this.propertyName = prop.getName();
			}
		}
		
		UIAsName asa = prop.getAnnotation(UIAsName.class);
		if (asa != null) {
			this.asNameIndex = asa.value();
		}
		return true;
	}

	@Override
	public String getPropertyName() {
		return this.propertyName;
	}
	
	public BeanPropertyDesc getProperty() {
		return this.property;
	}
	protected T getAnnotation() {
		return this.annotation;
	}
	
	protected boolean isRequired() {
		if (this.isRequiredAnnotation()) {
			return true;
		}
		if (this.getProperty().getType().getType().isPrimitive()) {
			return true;
		}
		if (canBeEmpty()) { // пустое значение будет считаться за значение, а не за NULL
			return false;
		}
		Column ca = this.getProperty().getAnnotation(Column.class);
		if (ca != null && !ca.nullable()) {
			return true;
		}
		JoinColumn jca = this.getProperty().getAnnotation(JoinColumn.class);
		if (jca != null && !jca.nullable()) {
			return true;
		}
		JoinColumns jcsa = this.getProperty().getAnnotation(JoinColumns.class);
		if (jcsa != null) {
			for (JoinColumn jc : jcsa.value()) {
				if (!jc.nullable()) {
					return true;
				}
			}
		}
		return false;
	}
	
	protected boolean canBeEmpty() {
		return false;
	}
	
	protected boolean isReadonly() {
		Boolean roa = this.isReadonlyAnnotation();
		if (roa != null && roa) {
			return true;
		}
		if (!this.getProperty().canSet()) {
			return true;
		}
		return false;
	}
	
	protected Integer getAsNameIndex() {
		return this.asNameIndex;
	}
	
	protected abstract Class<T> getAnnotationType();
	
	protected abstract String getForcedName();
	
	protected abstract boolean isRequiredAnnotation();
	
	protected abstract Boolean isReadonlyAnnotation();
}
