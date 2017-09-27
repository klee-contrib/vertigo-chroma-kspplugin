package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.model.FileRegion;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Méthodes utilitaires pour gérer les marqueurs.
 */
public final class MarkerUtils {

	private static final String KSP_MARKER_TYPE = "io.vertigo.chroma.kspPlugin.kspProblem";

	private MarkerUtils() {
		// RAS.
	}

	public static void addKspMarker(FileRegion fileRegion, String message, int severity) {
		try {
			int lineNumber = fileRegion.getLineIdx();
			IMarker marker = fileRegion.getFile().createMarker(KSP_MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			marker.setAttribute(IMarker.CHAR_START, fileRegion.getOffset());
			marker.setAttribute(IMarker.CHAR_END, fileRegion.getOffset() + fileRegion.getLength());
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}

	public static void deleteKspMarkers(IFile file) {
		try {
			file.deleteMarkers(KSP_MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			ErrorUtils.handle(e);
		}
	}
}
