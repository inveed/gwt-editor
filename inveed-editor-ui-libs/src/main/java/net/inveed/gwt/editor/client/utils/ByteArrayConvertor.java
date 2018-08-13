package net.inveed.gwt.editor.client.utils;

import java.nio.charset.Charset;

public class ByteArrayConvertor {
	public static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	
	
	public static final void intToByteArray(int value, byte[] buf, int offset) {
		buf[offset] = (byte)((value >>> 24) & 0xFF);
		buf[offset+1] =  (byte)((value >>> 16) & 0xFF);
		buf[offset+2] = (byte)((value >>> 8) & 0xFF);
		buf[offset+3] = (byte)(value & 0xFF);
	}
	
	public static final byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)((value >>> 24) & 0xFF),
	            (byte)((value >>> 16) & 0xFF),
	            (byte)((value >>> 8) & 0xFF),
	            (byte)(value & 0xFF)};
	}
	
	public static final int byteArrayToInt(byte[] bytes, int offset) {
		return ((int) (bytes[0 + offset] & 0xFF)) << 24 | ((int)(bytes[1+ offset ] & 0xFF)) << 16 | ((int)(bytes[2 + offset] & 0xFF)) << 8 | ((int)(bytes[3 + offset] & 0xFF));
	}
	
	public static final byte[] intTo2Octet(int value) {
	    return new byte[] {
	    		(byte)((value >>> 8) & 0xFF),
	    		(byte)(value & 0xFF)};
	}
	
	public static int intFrom2Octets(byte[] bytes, int offset) {
	     return (bytes[0 + offset] & 0xFF) << 8 | (bytes[1 + offset] & 0xFF);
	}
	
	public static final byte[] longTo4Octets(long value) {
	    return new byte[] {
	            (byte)((value >>> 24) & 0xFFL),
	            (byte)((value >>> 16) & 0xFFL),
	            (byte)((value >>> 8) & 0xFFL),
	            (byte)(value & 0xFFL)};
	}
	
	public static final byte[] longToByteArray(long value) {
	    return new byte[] {
	    		(byte)((value >>> 56) & 0xFFL),
	    		(byte)((value >>> 48) & 0xFFL),
	    		(byte)((value >>> 40) & 0xFFL),
	    		(byte)((value >>> 32) & 0xFFL),
	            (byte)((value >>> 24) & 0xFFL),
	            (byte)((value >>> 16) & 0xFFL),
	            (byte)((value >>> 8) & 0xFFL),
	            (byte)(value & 0xFFL)};
	}
	
	public static final long byteArrayToLong(byte[] bytes, int offset) {
		return ((long) (bytes[0 + offset] & 0xFF)) << 56 
				| ((long) (bytes[1 + offset] & 0xFF)) << 48
				| ((long) (bytes[2 + offset] & 0xFF)) << 40
				| ((long) (bytes[3 + offset] & 0xFF)) << 32 
				| ((long) (bytes[4 + offset] & 0xFF)) << 24 
				| ((long)(bytes[5 + offset] & 0xFF)) << 16 
				| ((long)(bytes[6 + offset] & 0xFF)) << 8 
				| ((long)(bytes[7 + offset] & 0xFF));
	}

	public static long longFrom4Octets(byte[] bytes, int offset) {
		return ((long) (bytes[0 + offset] & 0xFF)) << 24 | ((long)(bytes[1 + offset] & 0xFF)) << 16 | ((long)(bytes[2 + offset] & 0xFF)) << 8 | ((long)(bytes[3 + offset] & 0xFF));
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(toHex(bytes[i] >> 4));
			sb.append(toHex(bytes[i]));
		}

		return sb.toString();
	}

	private static char toHex(int nibble) {
		final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		return hexDigit[nibble & 0xF];
	}

	public static byte[] stringToByteArrayEncoded(String v) {
		if (v == null) {
			return intToByteArray(-1);
		}
		byte[] sv = v.getBytes(UTF8_CHARSET);
		byte[] ret = new byte[sv.length + 4];
		byte[] len = intToByteArray(sv.length);
		System.arraycopy(len, 0, ret, 0, 4);
		System.arraycopy(sv, 0, ret, 4, sv.length);
		return ret;
		
	}
	
	public static String stringFromByteArrayEncoded(byte[] b, int offset) {
		if (b == null)
			return null;
		if (b.length - offset < 4)
			return null;
		int len = byteArrayToInt(b, offset);
		if (len < 0) {
			return null;
		}
		if (b.length - offset - 4 < len) {
			return null;
		}
	
		return new String(b, offset + 4, len);
	}
}
