package io.vertigo.chroma.kspplugin.boot;

import io.vertigo.chroma.kspplugin.legacy.LegacyManager;
import io.vertigo.chroma.kspplugin.model.Manager;
import io.vertigo.chroma.kspplugin.resources.DaoManager;
import io.vertigo.chroma.kspplugin.resources.DtoManager;
import io.vertigo.chroma.kspplugin.resources.KspManager;
import io.vertigo.chroma.kspplugin.resources.ServiceManager;
import io.vertigo.chroma.kspplugin.resources.WsRouteManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IStartup;

/**
 * Classe publiant une méthode exécutée au démarrage d'Eclipse.
 */
public class KspPluginStartup implements IStartup {

	@Override
	public void earlyStartup() {

		/* Analyse les projets pour deviner la version du framework de chacun. */
		LegacyManager.getInstance().init();

		/* Liste et instancie les singletons des managers de ressources. */
		Manager[] managers = new Manager[] { KspManager.getInstance(), DaoManager.getInstance(), DtoManager.getInstance(), ServiceManager.getInstance(),
				WsRouteManager.getInstance() };

		/* Créé et programme un job pour démarrer chacun des managers de ressources. */
		for (Manager pluginManager : managers) {

			Job job = new Job("KspPluginInit" + pluginManager.getClass().getSimpleName()) {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					pluginManager.init();
					return Status.OK_STATUS;
				}

			};

			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

}
