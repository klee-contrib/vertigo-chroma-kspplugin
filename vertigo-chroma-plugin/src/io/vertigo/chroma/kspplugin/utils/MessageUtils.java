package io.vertigo.chroma.kspplugin.utils;

import java.text.MessageFormat;

public final class MessageUtils {

	private MessageUtils() {
		// RAS.
	}

	public static void showNoTaskImplementationFoundMessage(String javaName) {
		UiUtils.showMessage(MessageFormat.format("No task implementation found for {0}.", javaName));
	}

	public static void showNoKspDeclarationFoundMessage(String javaName) {
		UiUtils.showMessage(MessageFormat.format("No KSP declaration found for {0}.", javaName));
	}

	public static void showNoJavaFileFoundMessage(String javaName) {
		UiUtils.showMessage(MessageFormat.format("No Java file found for {0}.", javaName));
	}

	public static void showNoKspWordSelectedMessage() {
		UiUtils.showMessage("No KSP name selected.");
	}

	public static void showNoJavaWordSelectedMessage() {
		UiUtils.showMessage("No Java name selected.");
	}

	public static void showNoKspFoundMessage(String word) {
		UiUtils.showMessage(MessageFormat.format("No KSP file found for {0}.", word));
	}

	public static void showNoElementMessage(String nature) {
		UiUtils.showMessage(MessageFormat.format("No {0} found in the workspace.", nature));
	}
}
