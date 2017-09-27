package io.vertigo.chroma.kspplugin.model;

import java.util.List;

/**
 * Stocke l'ensemble des DTO de tout un workspace.
 */
public class DtoWorkspace {

	private final List<DtoFile> dtoFiles;

	public DtoWorkspace(List<DtoFile> dtoFiles) {
		this.dtoFiles = dtoFiles;
	}

	public List<DtoFile> getDtoFiles() {
		return dtoFiles;
	}
}
