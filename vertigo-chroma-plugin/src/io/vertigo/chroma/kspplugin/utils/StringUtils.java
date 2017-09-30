package io.vertigo.chroma.kspplugin.utils;

import java.util.regex.Pattern;

public final class StringUtils {

	private static final Pattern CONSTANT_CASE = Pattern.compile("^[A-Z0-9_]+$");
	private static final Pattern SNAKE_CASE = Pattern.compile("^[A-Za-z0-9_]+$");
	private static final Pattern SQL_PARAMETER_NAME = Pattern.compile("^[A-Za-z0-9_#\\.]+$");
	private static final Pattern CANONICAL_JAVA_NAME = Pattern.compile("^[A-Za-z0-9\\.]+$");
	private static final Pattern CAMEL_CASE = Pattern.compile("^[A-Za-z0-9]+$");
	private static final Pattern NOT_SPACE = Pattern.compile("^\\S+$");

	private StringUtils() {
		// RAS.
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s); // NOSONAR
	}

	public static String toPascalCase(String constantCase) {
		/* Délègue à l'implémentation de vertigo-core */
		try {
			return VertigoStringUtils.constToUpperCamelCase(constantCase);
		} catch (Exception e) { // NOSONAR
			/* En cas d'échec, implémentation dégradée. */
			String[] parts = constantCase.split("_");
			StringBuilder sb = new StringBuilder();
			for (String part : parts) {
				sb.append(toProperCase(part));
			}
			return sb.toString();
		}
	}

	public static String toCamelCase(String constantCase) {
		/* Délègue à l'implémentation de vertigo-core */
		try {
			return VertigoStringUtils.constToLowerCamelCase(constantCase);
		} catch (Exception e) { // NOSONAR
			/* En cas d'échec, implémentation dégradée. */
			String[] parts = constantCase.split("_");
			StringBuilder sb = new StringBuilder();
			boolean isFirst = true;
			for (String part : parts) {
				if (isFirst) {
					sb.append(part.toLowerCase());
				} else {
					sb.append(toProperCase(part));
				}
				isFirst = false;
			}
			return sb.toString();

		}
	}

	public static String toConstantCase(String camelCase) {
		/* Délègue à l'implémentation de vertigo-core */
		try {
			return VertigoStringUtils.camelToConstCase(camelCase);
		} catch (Exception e) { // NOSONAR
			/* En cas d'échec, implémentation dégradée. */
			StringBuilder sb = new StringBuilder();
			for (char c : camelCase.toCharArray()) {
				if (Character.isUpperCase(c) && sb.length() > 0) {
					sb.append('_');
				}
				sb.append(Character.toUpperCase(c));
			}

			return sb.toString();
		}
	}

	public static String toSnakeCase(String camelCase) {
		return toConstantCase(camelCase).toLowerCase();
	}

	public static String toProperCase(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String getLastNameFragment(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		String[] parts = s.split("\\.");
		return parts[parts.length - 1];
	}

	public static String removeExtension(String s) {
		if (s == null || s.isEmpty()) {
			return s;
		}
		int index = s.lastIndexOf('.');
		if (index == -1) {
			return s;
		}
		return s.substring(0, index);
	}

	public static boolean isConstantCase(String s) {
		return CONSTANT_CASE.matcher(s).matches();
	}

	public static boolean isSnakeCase(String s) {
		return SNAKE_CASE.matcher(s).matches();
	}

	public static boolean isSqlParameterName(String s) {
		return SQL_PARAMETER_NAME.matcher(s).matches();
	}

	public static boolean isCamelCase(String s) {
		return CAMEL_CASE.matcher(s).matches();
	}

	public static boolean isNotSpace(String s) {
		return NOT_SPACE.matcher(s).matches();
	}

	public static boolean isCanonicalJavaName(String s) {
		return CANONICAL_JAVA_NAME.matcher(s).matches();
	}
}
