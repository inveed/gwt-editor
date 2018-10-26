package net.inveed.gwt.editor.commons;

public enum DurationFormat {
	/**
	 * Value will be parsed and serialized as ISO
	 */
	ISO,
	
	/** 
	 * Value will be parsed and serialized as a number of seconds
	 */
	INTEGER_SECONDS,
	
	/** 
	 * Value will be parsed and serialized as a number of milliseconds
	 */
	INTEGER_MSEC,
	
	FLOAT_SECONDS,
	FLOAT_MSEC
}
