package io.vertigo.chroma.kspplugin.utils;

import io.vertigo.chroma.kspplugin.Activator;
import io.vertigo.chroma.kspplugin.model.KspDeclaration;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

/**
 * Utilitaire pour fournir des images pour l'UI.
 * <p>
 * On privilégie le recyclage d'images des plugins standard existants.
 * </p>
 */
public final class ImageUtils {

	private ImageUtils() {
		// RAS.
	}

	/**
	 * Obtient une image pour une déclaration KSP.
	 * 
	 * @param kspDeclaration Déclaration KSP.
	 * @return Image.
	 */
	public static Image getDeclarationImage(KspDeclaration kspDeclaration) { // NOSONAR
		org.eclipse.ui.ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		org.eclipse.jdt.ui.ISharedImages sharedImagesJdt = JavaUI.getSharedImages();
		switch (kspDeclaration.getNature()) {
		case "DtDefinition": // Kasper >= 4
		case "DT": // Kasper <= 3
			return sharedImagesJdt.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
		case "Task": // Kasper >= 5
		case "Service": // Kasper <= 4
			return sharedImagesJdt.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC);
		case "Domain":
			return sharedImagesJdt.getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_ANNOTATION);
		case "FileInfo":
			return sharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_FILE);
		default:
			/*
			 * Pas d'images spécifiques pour : Constraint, Formatter, Association, PublisherNode, Controller...
			 */
			return sharedImages.getImage(org.eclipse.ui.ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * Obtient une image pour un package.
	 * 
	 * @return Image de package.
	 */
	public static Image getPackageImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PACKAGE);
	}

	/**
	 * Obtient une image pour une route de ws.
	 * 
	 * @return Image de package.
	 */
	public static Image getWsImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_ANNOTATION);
	}

	/**
	 * Obtient une image pour une implémentation de service.
	 * 
	 * @return Image d'implémentation de service.
	 */
	public static Image getServiceImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC);
	}

	/**
	 * Obtient une image pour une méthode de DAO/PAO.
	 * 
	 * @return Image de méthode de DAO/PAO.
	 */
	public static Image getDaoImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_PUBLIC);
	}

	/**
	 * Obtient une image pour un DTO.
	 * 
	 * @return Image d'un DTO.
	 */
	public static Image getDtoImage() {
		return JavaUI.getSharedImages().getImage(org.eclipse.jdt.ui.ISharedImages.IMG_OBJS_CLASS);
	}

	/**
	 * Obtient une image pour un bouton de tri.
	 * 
	 * @return Image de tri.
	 */
	public static ImageDescriptor getSortImage() {
		return Activator.getImageDescriptor("icons/alphab_sort_co.gif");
	}
}
