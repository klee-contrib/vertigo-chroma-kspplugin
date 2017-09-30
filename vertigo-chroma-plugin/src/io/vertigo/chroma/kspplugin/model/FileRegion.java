package io.vertigo.chroma.kspplugin.model;

import java.util.Objects;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

/**
 * Région de fichier.
 * 
 * <p>
 * Représente un fichier et une localisation dans le document du fichier.
 * </p>
 */
public class FileRegion {

	private final IFile file;
	private final int offset;
	private final int length;
	private final int lineIdx;

	public FileRegion(IFile file, int offset, int length) {
		this(file, offset, length, -1);
	}

	public FileRegion(IFile file, int offset, int length, int lineIdx) {
		this.file = file;
		this.offset = offset;
		this.length = length;
		this.lineIdx = lineIdx;
	}

	public IFile getFile() {
		return file;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	public int getLineIdx() {
		return lineIdx;
	}

	public String getProjectName() {
		return getProject().getName();
	}

	public IProject getProject() {
		return getFile().getProject();
	}

	@Override
	public int hashCode() {
		return Objects.hash(file.getFullPath(), offset, length);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		FileRegion other = (FileRegion) obj;
		return other.file.equals(file) && other.offset == offset && other.length == length;
	}
}
