package net.inveed.gwt.editor.commons;

public enum TriBool {
	UNDEF,
	TRUE,
	FALSE;
	
	public Boolean toBoolean() {
		if (this == UNDEF) {
			return null;
		} else if (this == TRUE) {
			return true;
		} else {
			return false;
		}
	}
}
