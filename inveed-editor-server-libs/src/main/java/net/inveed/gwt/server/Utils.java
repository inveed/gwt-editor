package net.inveed.gwt.server;

public final class Utils {
	public static final String getNullOrValue(String v) {
		if (v == null) {
			return null;
		}
		v = v.trim();
		if (v.length() == 0) {
			return null;
		} else {
			return v;
		}
	}
	
	public static final Long getNullOrValue(long v) {
		if (v == Long.MAX_VALUE) {
			return null;
		} else if (v == Long.MIN_VALUE) {
			return null;
		} else {
			return v;
		}
	}
	
	public static final Integer getNullOrValue(int v) {
		if (v == Integer.MAX_VALUE) {
			return null;
		} else if (v == Integer.MIN_VALUE) {
			return null;
		} else {
			return v;
		}
	}
	
	public static final Double getNullOrValue(double v) {
		if (v == Double.MAX_VALUE) {
			return null;
		} else if (v == Double.MIN_VALUE) {
			return null;
		} else {
			return v;
		}
	}
}
