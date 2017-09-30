package io.vertigo.chroma.kspplugin.ui.editors.ksp.outline;

import io.vertigo.chroma.kspplugin.model.KspDeclaration;

public class TreeKspDeclaration extends TreeObject {
	private final KspDeclaration kspDeclaration;

	public TreeKspDeclaration(KspDeclaration kspDeclaration) {
		super(kspDeclaration.getConstantCaseName());
		this.kspDeclaration = kspDeclaration;
	}

	@Override
	public String toString() {
		return kspDeclaration.getJavaName();
	}

	@Override
	public String getMainText() {
		return toString();
	}

	@Override
	public String getSubText() {
		return kspDeclaration.getNature();
	}

	public KspDeclaration getKspDeclaration() {
		return kspDeclaration;
	}
}
