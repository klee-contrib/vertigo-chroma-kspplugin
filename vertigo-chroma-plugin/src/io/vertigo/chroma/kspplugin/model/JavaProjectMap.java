package io.vertigo.chroma.kspplugin.model;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Map associant un projet à un projet Java.
 */
public class JavaProjectMap extends HashMap<IProject, IJavaProject> {

	private static final long serialVersionUID = 1L;
}
