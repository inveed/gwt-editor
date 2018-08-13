package net.inveed.gwt.editor.client.types;

import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JSTimeInterval implements IJSObject {
	public static enum Format {
		ISO,
		MINUTES,
		SECONDS,
		MSECONDS
	}
	public static final String TYPE = "TIME_INTERVAL";
	private static final long SEC_IN_DAY = 24L * 60L * 60L;
	private static final long SEC_IN_HOUR = 3600L;
	
	private final int months;
	private final double seconds;
	private final Format jsonFormat;
	
	public static final JSTimeInterval parse(String v, Format format) {
		if (v == null) {
			return null;
		}
		try {
			Double d = Double.parseDouble(v);
			if (d != null) {
				return parse(new JSONNumber(d), format);
			}
		} catch (Exception e) {}
		return parse(new JSONString(v), format);
	}
	
	public static final JSTimeInterval parse(JSONValue json, Format format) {
		if (json.isNull() != null) {
			return null;
		} else if (json.isNumber() != null) {
			if (format == Format.MSECONDS) {
				return new JSTimeInterval(0, json.isNumber().doubleValue() / 1000D, format);
			} else if (format == Format.SECONDS) {
				return new JSTimeInterval(0, json.isNumber().doubleValue(), format);
			} else if (format == Format.MINUTES) {
				return new JSTimeInterval(0, json.isNumber().doubleValue() * 60D, format);
			} else {
				//TODO: LOG
				return null;
			}
		} else if (json.isString() != null) {
			String sv = json.isString().stringValue();
			if (format == Format.ISO) {
				//TODO: parse ISO duration
				return null;
			}
			try {
				double dv = Double.parseDouble(sv);
				if (format == Format.MSECONDS) {
					return new JSTimeInterval(0, dv / 1000D, format);
				} else if (format == Format.SECONDS) {
					return new JSTimeInterval(0, dv, format);
				} else if (format == Format.MINUTES) {
					return new JSTimeInterval(0, dv * 60D, format);
				} else {
					//TODO: LOG! мы не должны тут оказаться!
					return null; //Placeholder! 
				}
			} catch (NumberFormatException e) { 
				//TODO: LOG! не число. Должно быть ISO строка
				return null;
			}
		} else {
			//TODO: error!
			return null;
		}
	}
	
	public JSTimeInterval(Integer years, Integer months, Integer days, Integer hours, Integer minutes, Double seconds, Format format) {
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
	
	public JSTimeInterval(int months, double seconds, Format format) {
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
		if (this.months != 0 || this.jsonFormat == Format.ISO) {
			return new JSONString(this.toISOString());
		} else if (this.jsonFormat == Format.SECONDS) {
			return new JSONNumber(this.seconds);
		} else if (this.jsonFormat == Format.MSECONDS) {
			return new JSONNumber((long) (this.seconds * 1000D));
		} else if (this.jsonFormat == Format.MINUTES) {
			return new JSONNumber((long) (this.seconds / 60D));
		} else {
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

}
