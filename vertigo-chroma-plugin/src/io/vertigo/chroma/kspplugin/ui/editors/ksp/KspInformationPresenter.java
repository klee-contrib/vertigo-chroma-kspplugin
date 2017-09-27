package io.vertigo.chroma.kspplugin.ui.editors.ksp;

import io.vertigo.chroma.kspplugin.model.DtoField;
import io.vertigo.chroma.kspplugin.model.DtoFile;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.utils.StringUtils;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

public class KspInformationPresenter implements DefaultInformationControl.IInformationPresenter {
	public String updatePresentation(Display display, String infoText, TextPresentation presentation, int maxWidth, int maxHeight) {

		/* Cherche le fichier Java du DTO. */
		DtoFile dtoFile = DtoManager.getInstance().findDtoFile(infoText);
		if (dtoFile == null) {
			return infoText;
		}

		/* Construit le text hover et le style en parallèle. */
		StyledStringBuiler sb = new StyledStringBuiler(presentation);
		String tableSnakeCase = StringUtils.toSnakeCase(dtoFile.getJavaName());
		sb.append(String.format("Table %s", tableSnakeCase), StyleType.BOLD);
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(String.format("Mapped to %s.%s.", dtoFile.getPackageName(), dtoFile.getJavaName()));
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append("Columns:", StyleType.BOLD);
		int fieldMaxLength = 0;
		int labelMaxLength = 0;
		List<DtoField> fields = dtoFile.getPersistentFields();
		for (DtoField field : fields) {
			int fieldLength = field.getConstantCaseName().toLowerCase().length();
			if (fieldLength > fieldMaxLength) {
				fieldMaxLength = fieldLength;
			}
			int labelLength = field.getLabel().length();
			if (labelLength > labelMaxLength) {
				labelMaxLength = labelLength;
			}
		}
		for (DtoField field : fields) {
			sb.append(System.lineSeparator() + "\t\t\t");
			sb.append(field.getConstantCaseName().toLowerCase(), StyleType.FIXED_WIDTH, fieldMaxLength);
			sb.append("\t");
			sb.append(field.getLabel(), StyleType.BOLD, labelMaxLength);
		}

		return sb.toString();
	}

	/**
	 * StringBuilder permettant de gérer en parallèle du style.
	 */
	private class StyledStringBuiler {

		private final TextPresentation presentation;
		private final StringBuilder sb = new StringBuilder();
		private int offset;

		public StyledStringBuiler(TextPresentation presentation) {
			this.presentation = presentation;
		}

		public StyledStringBuiler append(String s) {
			return append(s, StyleType.DEFAULT);
		}

		public StyledStringBuiler append(String s, StyleType styleType) {
			int length = s.length();
			/* Ajoute le string. */
			sb.append(s);
			/* Créé le style range. */
			addStyleRange(styleType, length);
			/* Met à jour l'offset courant. */
			offset += length;
			return this;
		}

		public StyledStringBuiler append(String s, StyleType styleType, int padding) {
			String paddedString = StringUtils.padRight(s, padding);
			int length = paddedString.length();
			/* Ajoute le string. */
			sb.append(paddedString);
			/* Créé le style range. */
			addStyleRange(styleType, length);
			/* Met à jour l'offset courant. */
			offset += length;
			return this;
		}

		@Override
		public String toString() {
			return sb.toString();
		}

		private void addStyleRange(StyleType styleType, int length) {
			int fontStyle = styleType == StyleType.BOLD ? SWT.BOLD : SWT.NONE;
			StyleRange range = new StyleRange(offset, length, null, null, fontStyle);
			if (styleType == StyleType.FIXED_WIDTH) {
				range.font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
			}
			presentation.addStyleRange(range);
		}
	}

	private enum StyleType {

		/**
		 * Style par défaut.
		 */
		DEFAULT,

		/**
		 * Gras.
		 */
		BOLD,

		/**
		 * Largeur fixe.
		 */
		FIXED_WIDTH;
	}
}
