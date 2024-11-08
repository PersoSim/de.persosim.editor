package de.persosim.editor.rcp;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.persosim.simulator.perso.export.ProfileHelper;
import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator extends Plugin implements BundleActivator
{

	@Override
	public void start(BundleContext context) throws Exception
	{
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());

		Bundle plugin = de.persosim.simulator.Activator.getContext().getBundle();
		URL url = plugin.getEntry("personalization/" + ProfileHelper.PERSO_FILES_PARENT_DIR);
		URL resolvedUrl = FileLocator.resolve(url);
		File folder = new File(resolvedUrl.getFile());
		String pathString = folder.getAbsolutePath();

		ProfileHelper.setRootPathPersoFiles(Path.of(pathString));
		ProfileHelper.getPreferenceStoreAccessorInstance();
	}

}
