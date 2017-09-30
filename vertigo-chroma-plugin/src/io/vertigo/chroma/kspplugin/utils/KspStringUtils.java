package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.DomainType;
import io.vertigo.chroma.kspplugin.model.DtoDefinitionPath;
import io.vertigo.chroma.kspplugin.model.KspAttribute;
import io.vertigo.chroma.kspplugin.model.KspDeclarationMainParts;
import io.vertigo.chroma.kspplugin.model.KspDeclarationNameParts;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class KspStringUtils {

	private static final Pattern PACKAGE = Pattern.compile("package\\s*([A-Za-z0-9\\.]+)\\s*;?");
	private static final Pattern DECLARATION_NAME = Pattern.compile("([A-Z]+)_(\\S+)");
	private static final Pattern DT_DEFINITION_DOMAIN = Pattern.compile("DO_DT_([A-Z0-9_]+)(?:_DTO|_DTC)");
	private static final Pattern KASPER_3_DT_DEFINITION_DOMAIN = Pattern.compile("Dt((?:Object)|(?:Collection))\\(\\s*([^\\\\\\s)]+)\\s*\\)");
	private static final Pattern DT_DEFINITION_DECLARATION = Pattern.compile("DT_([A-Z0-9_]+)");
	private static final Pattern TASK_DECLARATION = Pattern.compile("(?:(?:TK)|(?:SV))_([A-Z0-9_]+)");
	private static final Pattern VERTIGO_DECLARATION_HEADER = Pattern.compile("\\s*((?:create)|(?:alter))\\s+([A-Za-z]+)\\s+([A-Z0-9_]+)\\s*[\\{]?\\s*");
	private static final Pattern KASPER_6_DECLARATION_HEADER = Pattern.compile("\\s*((?:create)|(?:alter))\\s+([A-Za-z]+)\\s+([A-Z0-9_]+)\\s*[\\(]?\\s*");
	private static final Pattern KASPER_5_DECLARATION_HEADER = Pattern.compile("\\s*([A-Z0-9_]+)\\s*=\\s*new\\s+([A-Za-z]+)\\s*(?:\\(.*)?");
	private static final Pattern KASPER_4_DECLARATION_HEADER = Pattern.compile("\\s*[A-Za-z]+\\[([A-Z0-9_]+)\\]\\s*=\\s*new\\s+([A-Za-z]+)\\s*(?:\\(.*)?");
	private static final Pattern KASPER_3_DECLARATION_HEADER = Pattern.compile("\\s*([A-Za-z]+)\\s+([A-Z0-9_]+)\\s*[\\{]?\\s*");
	private static final Pattern VERTIGO_KSP_ATTRIBUTE = Pattern
			.compile("\\s*attribute\\s+(\\S+)\\s*\\{\\s*((?:\\s*[^:\\s]+\\s*:\\s*\\S+\\s*)+)\\s*\\}\\s*[;,]?\\s*");
	private static final Pattern KASPER_6_KSP_ATTRIBUTE = Pattern
			.compile("\\s*attribute\\s+(\\S+)\\s*\\(\\s*((?:\\s*[^:\\s]+\\s*:\\s*\\S+\\s*)+)\\s*\\)\\s*[;,]?\\s*");
	private static final Pattern KASPER_5_KSP_ATTRIBUTE = Pattern
			.compile("\\s*attribute\\[(\\S+)\\]\\s*=\\s*new\\s*Attribute\\s*\\(\\s*((?:\\s*[^=\\s]+\\s*=\\s*\\S+\\s*[;,]?\\s*)+)\\s*\\)\\s*[;,]?\\s*");
	private static final Pattern KASPER_3_KSP_ATTRIBUTE = Pattern.compile("\\s*(\\S+)\\s+([A-Z0-9_]+)\\s+(?:(not)\\s+)?null\\s+((?:in)|(?:out))\\s*;?\\s*");
	private static final Pattern VERTIGO_KSP_ATTRIBUTE_PROPERTIES = Pattern.compile("([^:\\s]+)\\s*:\\s*([^;,\\s]+)");
	private static final Pattern KASPER_5_KSP_ATTRIBUTE_PROPERTIES = Pattern.compile("([^=\\s]+)\\s*=\\s*([^;,\\s]+)");
	private static final Pattern KASPER_3_DEFINITION_PATH = Pattern.compile("\"?([A-Za-z0-9]+)\\.([A-Za-z0-9]+)\"?");
	private static final Pattern VERTIGO_DAO_FILE_NAME = Pattern.compile("^(.+[DP]AO)\\.java$");
	private static final Pattern KASPER_4_DAO_FILE_NAME = Pattern.compile("^(Services.+)\\.java$");
	private static final Pattern KASPER_3_DAO_FILE_NAME = Pattern.compile("^(.+Services)\\.java$");
	private static final Pattern VERTIGO_SERVICE_FILE_NAME = Pattern.compile("^(.+ServicesImpl)\\.java$");
	private static final Pattern KASPER_4_SERVICE_FILE_NAME = Pattern.compile("^(Facade.+Bean)\\.java$");
	private static final Pattern KASPER_3_SERVICE_FILE_NAME = Pattern.compile("^(.+FacadeMetier)\\.java$");
	private static final Pattern WS_FILE_NAME = Pattern.compile("^(.*Web.*)\\.java$");

	private KspStringUtils() {
		// RAS.
	}

	public static String getFieldNameFromGetter(String getter) {
		String pascalCase = getter.substring(3);
		return pascalCase.substring(0, 1).toLowerCase() + pascalCase.substring(1);
	}

	public static String getPackageName(String lineContent) {
		Matcher matcher = PACKAGE.matcher(lineContent);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static DtoDefinitionPath getKasper3DefinitionPath(String definitionPath) {
		Matcher matcher = KASPER_3_DEFINITION_PATH.matcher(definitionPath);
		if (matcher.matches()) {
			String packageName = matcher.group(1);
			String dtoName = matcher.group(2);
			return new DtoDefinitionPath(packageName, dtoName);
		}
		return null;
	}

	public static KspDeclarationMainParts getVertigoKspDeclarationParts(String lineContent) {
		Matcher matcher = VERTIGO_DECLARATION_HEADER.matcher(lineContent);
		if (matcher.matches()) {
			String verb = matcher.group(1);
			String nature = matcher.group(2);
			String name = matcher.group(3);
			return new KspDeclarationMainParts(verb, nature, name);
		}
		return null;
	}

	public static KspDeclarationMainParts getKasper6KspDeclarationParts(String lineContent) {
		Matcher matcher = KASPER_6_DECLARATION_HEADER.matcher(lineContent);
		if (matcher.matches()) {
			String verb = matcher.group(1);
			String nature = matcher.group(2);
			String name = matcher.group(3);
			return new KspDeclarationMainParts(verb, nature, name);
		}
		return null;
	}

	public static KspDeclarationMainParts getKasper5KspDeclarationParts(String lineContent) {
		Matcher matcher = KASPER_5_DECLARATION_HEADER.matcher(lineContent);
		if (matcher.matches()) {
			String name = matcher.group(1);
			String nature = matcher.group(2);
			return new KspDeclarationMainParts(nature, name);
		}
		return null;
	}

	public static KspDeclarationMainParts getKasper4KspDeclarationParts(String lineContent) {
		Matcher matcher = KASPER_4_DECLARATION_HEADER.matcher(lineContent);
		if (matcher.matches()) {
			String name = matcher.group(1);
			String nature = matcher.group(2);
			return new KspDeclarationMainParts(nature, name);
		}
		return null;
	}

	public static KspDeclarationMainParts getKasper3KspDeclarationParts(String lineContent) {
		Matcher matcher = KASPER_3_DECLARATION_HEADER.matcher(lineContent);
		if (matcher.matches()) {
			String nature = matcher.group(1);
			String name = matcher.group(2);
			return new KspDeclarationMainParts(nature, name);
		}
		return null;
	}

	public static KspAttribute getVertigoKspAttribute(String lineContent) {

		/* Premier pattern pour détecter l'attribut. */
		Matcher matcherAttribute = VERTIGO_KSP_ATTRIBUTE.matcher(lineContent);
		if (matcherAttribute.matches()) {
			String attributeName = matcherAttribute.group(1);
			KspAttribute attribute = new KspAttribute(attributeName);
			String allProperties = matcherAttribute.group(2);

			/* Second pattern pour détecter les paires clé-valeur de propriétés. */
			Matcher matcherProperties = VERTIGO_KSP_ATTRIBUTE_PROPERTIES.matcher(allProperties);
			while (matcherProperties.find()) {
				String propertyName = matcherProperties.group(1);
				String propertyValue = matcherProperties.group(2);
				attribute.addProperty(propertyName, propertyValue);
			}

			return attribute;
		}

		return null;
	}

	public static KspAttribute getKasper6KspAttribute(String lineContent) {

		/* Premier pattern pour détecter l'attribut. */
		Matcher matcherAttribute = KASPER_6_KSP_ATTRIBUTE.matcher(lineContent);
		if (matcherAttribute.matches()) {
			String attributeName = matcherAttribute.group(1);
			KspAttribute attribute = new KspAttribute(attributeName);
			String allProperties = matcherAttribute.group(2);

			/* Second pattern pour détecter les paires clé-valeur de propriétés. */
			Matcher matcherProperties = VERTIGO_KSP_ATTRIBUTE_PROPERTIES.matcher(allProperties);
			while (matcherProperties.find()) {
				String propertyName = matcherProperties.group(1);
				String propertyValue = matcherProperties.group(2);
				attribute.addProperty(propertyName, propertyValue);
			}

			return attribute;
		}

		return null;
	}

	public static KspAttribute getKasper5KspAttribute(String lineContent) {

		/* Premier pattern pour détecter l'attribut. */
		Matcher matcherAttribute = KASPER_5_KSP_ATTRIBUTE.matcher(lineContent);
		if (matcherAttribute.matches()) {
			String attributeName = matcherAttribute.group(1);
			KspAttribute attribute = new KspAttribute(attributeName);
			String allProperties = matcherAttribute.group(2);

			/* Second pattern pour détecter les paires clé-valeur de propriétés. */
			Matcher matcherProperties = KASPER_5_KSP_ATTRIBUTE_PROPERTIES.matcher(allProperties);
			while (matcherProperties.find()) {
				String propertyName = matcherProperties.group(1);
				String propertyValue = matcherProperties.group(2);
				attribute.addProperty(propertyName, propertyValue);
			}

			return attribute;
		}

		return null;
	}

	public static KspAttribute getKasper3KspAttribute(String lineContent) {

		/* Premier pattern pour détecter l'attribut. */
		Matcher matcherAttribute = KASPER_3_KSP_ATTRIBUTE.matcher(lineContent);
		if (matcherAttribute.matches()) {
			String rawDomain = matcherAttribute.group(1);
			String attributeName = matcherAttribute.group(2);
			boolean notNull = matcherAttribute.group(3) != null;
			boolean in = "in".equals(matcherAttribute.group(4));

			/* Cas d'un DTO ou DTC. */
			Matcher matcherDomain = KASPER_3_DT_DEFINITION_DOMAIN.matcher(rawDomain);
			if (matcherDomain.matches()) {
				DomainType domainType = "Collection".equals(matcherDomain.group(1)) ? DomainType.DTC : DomainType.DTO;
				String innerDomainName = matcherDomain.group(2);

				/* Cas d'un bean non persistant : syntaxe DT_NOM */
				String beanDtName = getDtDefinitionName(innerDomainName);
				if (beanDtName != null) {
					return new KspAttribute(attributeName, rawDomain, notNull, in, domainType, beanDtName);
				}

				/* Cas d'un DTO persistant : syntaxe "package.Nom" */
				DtoDefinitionPath definitionPath = getKasper3DefinitionPath(innerDomainName);
				if (definitionPath != null) {
					String persistentDtName = StringUtils.toConstantCase(definitionPath.getDtoName());
					return new KspAttribute(attributeName, rawDomain, notNull, in, domainType, persistentDtName);
				}
			}

			/* Cas d'un primitif. */
			return new KspAttribute(attributeName, rawDomain, notNull, in, DomainType.PRIMITIVE, null);
		}

		return null;
	}

	public static KspDeclarationNameParts getKspDeclarationNameParts(String s) {
		Matcher matcher = DECLARATION_NAME.matcher(s);
		if (matcher.matches()) {
			String prefix = matcher.group(1);
			String constantCaseNameOnly = matcher.group(2);
			return new KspDeclarationNameParts(prefix, constantCaseNameOnly);
		}
		return null;
	}

	public static String getDtDefinitionName(String s) {
		Matcher matcher = DT_DEFINITION_DECLARATION.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		matcher = DT_DEFINITION_DOMAIN.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getTaskName(String s) {
		Matcher matcher = TASK_DECLARATION.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getDaoFileName(String s) {
		Matcher matcher = VERTIGO_DAO_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getKasper4DaoFileName(String s) {
		Matcher matcher = KASPER_4_DAO_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getKasper3DaoFileName(String s) {
		Matcher matcher = KASPER_3_DAO_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getVertigoServiceFileName(String s) {
		Matcher matcher = VERTIGO_SERVICE_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getKasper4ServiceFileName(String s) {
		Matcher matcher = KASPER_4_SERVICE_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getKasper3ServiceFileName(String s) {
		Matcher matcher = KASPER_3_SERVICE_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}

	public static String getWsFileName(String s) {
		Matcher matcher = WS_FILE_NAME.matcher(s);
		if (matcher.matches()) {
			return matcher.group(1);
		}
		return null;
	}
}
