package de.persosim.editor.rcp;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import de.persosim.simulator.preferences.EclipsePreferenceAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class Activator extends Plugin implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new EclipsePreferenceAccessor());
	}
	
}
