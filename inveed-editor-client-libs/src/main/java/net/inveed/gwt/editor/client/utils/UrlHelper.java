package net.inveed.gwt.editor.client.utils;

public class UrlHelper {
	private static String concatInternal(String prefix, String suffix) {
		if (prefix.endsWith("/") && suffix.startsWith("/")) {
			return prefix + suffix.substring(1);
		} else if (prefix.endsWith("/")) {
			return prefix + suffix;
		} else if (suffix.startsWith("/")) {
			return prefix + suffix;
		} else {
			return prefix + "/" + suffix;
		}
	}
	
	public static String concat(String ... parts) {
		if (parts.length == 0) {
			return null;
		} else if (parts.length == 1) {
			return normalize(parts[0]);
		} else if (parts.length == 2) {
			return normalize(concatInternal(parts[0], parts[1]));
		} else {
			String p = parts[0];
			for (int i = 1; i < parts.length; i ++) {
				p = concatInternal(p, parts[i]);
			}
			return normalize(p);
		}
	}
	
	public static String normalize(String url) {
		int schemaPos = url.indexOf("://");
		String schema = null;
		String path = null;
		if (schemaPos > 0) {
			schema = url.substring(0, schemaPos);
			path = url.substring(schemaPos+3);
		} else {
			path = url;
		}
		while (true) {
			String p = path.replaceAll("//", "/");
			if (p.equals(path)) {
				path = p;
				break;
			}
			path = p;
		}
		if (schema != null) {
			return schema + "://" + path;
		} else {
			return path;
		}
	}
}
