package net.inveed.gwt.editor.commons;

public enum DurationPrecision {
	YEAR (7),
	MONTH (6),
	DAY (5),
	HOUR(4),
	MINUTE(3),
	SECOND(2),
	MSEC(1);
	
	private int level;
	private DurationPrecision(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
}
