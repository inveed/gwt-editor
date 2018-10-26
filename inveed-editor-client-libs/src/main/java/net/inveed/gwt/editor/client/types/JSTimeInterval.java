package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

import net.inveed.gwt.editor.commons.DurationFormat;
import net.inveed.gwt.editor.commons.DurationPrecision;

public class JSTimeInterval implements IJSObject {
	public static final String TYPE = "TIME_INTERVAL";
	private static final long SEC_IN_DAY = 24L * 60L * 60L;
	private static final long SEC_IN_HOUR = 3600L;
	
	private final int months;
	private final double seconds;
	private final DurationFormat jsonFormat;
	
	public static final JSTimeInterval parse(String v, DurationFormat format, DurationPrecision maxItem) {
		if (v == null) {
			return null;
		}
		try {
			Double d = Double.parseDouble(v);
			if (d != null) {
				return parse(new JSONNumber(d), format, maxItem);
			}
		} catch (Exception e) {}
		return parse(new JSONString(v), format, maxItem);
	}
	
	public static final JSTimeInterval parse(JSONValue json, DurationFormat format, DurationPrecision maxItem) {
		if (json.isNull() != null) {
			return null;
		} else if (json.isNumber() != null) {
			double v = ((long) json.isNumber().doubleValue()); // округлим
			switch (format) {
			case FLOAT_MSEC:
				return new JSTimeInterval(0, v / 1000D, format);
			case FLOAT_SECONDS:
				return new JSTimeInterval(0, v, format);
			case INTEGER_MSEC:
				return new JSTimeInterval(0, v / 1000D, format);
			case INTEGER_SECONDS:
				return new JSTimeInterval(0, v, format);
			default:
				return null;
			}
		} else if (json.isString() != null && format == DurationFormat.ISO) {
			return null; //TODO: Parse ISO Duration
		} else {
			//TODO: error!
			return null;
		}
	}
	
	public JSTimeInterval(Integer years, Integer months, Integer days, Integer hours, Integer minutes, Double seconds, DurationFormat format) {
		int y = years == null? 0 : years;
		int M = months == null ? 0 : months;
		long d = days == null ? 0 : days;
		long h = hours == null ? 0 : hours;
		long m = minutes == null ? 0 : minutes;
		double s = seconds == null ? 0 : seconds;
		
		this.months = y * 12 + M;
		this.seconds = (double) (d * SEC_IN_DAY + h * SEC_IN_HOUR + m * 60) + s;
		this.jsonFormat = format;
	}
	
	public JSTimeInterval(int months, double seconds, DurationFormat format) {
		this.months = months;
		this.seconds = seconds;
		this.jsonFormat = format;
	}
	
	@Override
	public boolean isEquals(IJSObject other) {
		if (other == null) {
			return false;
		}
		if (other.getType() != this.getType()) {
			return false;
		}
		
		JSTimeInterval oi = (JSTimeInterval) other;
		return oi.months == this.months && oi.seconds == this.seconds;
	}
	
	
	@Override
	public int compareTo(IJSObject other) {
		if (other == null) {
			return -1;
		}
		if (other.getType() != TYPE) {
			return TYPE.compareTo(other.getType());
		}
		JSTimeInterval o = (JSTimeInterval) other;
		int ret = Integer.compare(this.months, o.months);
		if (ret != 0){
			return ret;
		}
		ret = Double.compare(this.seconds, o.seconds);
		return ret;
	}

	@Override
	public String getType() {
		return TYPE;
	}
	
	public int getMonths(){ 
		return this.months;
	}
	
	public double getSeconds() {
		return this.seconds;
	}
	
	public int getHYears() { 
		return this.getMonths() / 12;
	}
	
	public int getHMonths() {
		int years = this.getHYears();
		return this.getMonths() - years * 12;
	}
	public long getHDays() {
		return (long) this.getSeconds() / SEC_IN_DAY;
	}
	
	public long getHHours() {
		long days = this.getHDays();
		long lsec = (long) this.getSeconds() - days * SEC_IN_DAY;
		return lsec / SEC_IN_HOUR;
	}
	public long getHMinutes() {
		long days = this.getHDays();
		long hours = this.getHHours();
		long lsec = (long) this.getSeconds() - days * SEC_IN_DAY - hours * SEC_IN_HOUR;
		return lsec / 60;
	}
	
	public double getHSeconds() {
		long days = this.getHDays();
		long hours = this.getHHours();
		long minutes = this.getHMinutes();
		return this.getSeconds() - (double) (days * SEC_IN_DAY + hours * SEC_IN_HOUR + minutes * 60);
	}

	@Override
	public JSONValue getJSONValue() {
		if (this.jsonFormat == DurationFormat.ISO) {
			return new JSONString(this.toISOString());
		}
		if (this.months != 0) {
			//TODO: ERROR
			return JSONNull.getInstance();
		}
		
		switch (this.jsonFormat) {			
		case FLOAT_MSEC:
			return new JSONNumber(this.seconds * 1000D);
		case FLOAT_SECONDS:
			return new JSONNumber(this.seconds);
		case INTEGER_MSEC:
			return new JSONNumber((long)(this.seconds * 1000D));
		case INTEGER_SECONDS:
			return new JSONNumber((long)this.seconds);
		default:
			//TODO: ERROR
			return JSONNull.getInstance();
		}
	}
	
	public String toISOString() {
		StringBuffer sb = new StringBuffer();
		sb.append("P");
		int y = this.getHYears();
		int M = this.getHMonths();
		long d = this.getHDays();
		long h = this.getHHours();
		long m = this.getHMinutes();
		double s = this.getHSeconds();
		
		if (y != 0) {
			sb.append(y);
			sb.append("Y");
		}
		if (M != 0) {
			sb.append(M);
			sb.append("M");
		}
		if (d != 0) {
			sb.append(d);
			sb.append("D");
		}
		sb.append("T");
		if (h != 0) {
			sb.append(h);
			sb.append("H");
		}
		if (m != 0) {
			sb.append(m);
			sb.append("M");
		}
		if (s != 0) {
			sb.append(s);
			sb.append("S");
		}
		
		return sb.toString();
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		int y = this.getHYears();
		int M = this.getHMonths();
		long d = this.getHDays();
		long h = this.getHHours();
		long m = this.getHMinutes();
		double s = this.getHSeconds();
		
		if (y != 0) {
			sb.append(y);
			sb.append(" Years ");
		}
		if (M != 0) {
			sb.append(M);
			sb.append(" Months ");
		}
		if (d != 0) {
			sb.append(d);
			sb.append(" Days ");
		}
		if (h != 0) {
			sb.append(h);
			sb.append(" Hours ");
		}
		if (m != 0) {
			sb.append(m);
			sb.append(" Minutes ");
		}
		if (s != 0) {
			sb.append(s);
			sb.append(" Seconds ");
		}
		
		return sb.toString().trim();
	}
	
	@Override
	public String getDisplayValue() {
		return this.toString();
	}

}
