package de.persosim.editor.ui.launcher;

import java.nio.file.Paths;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.ProviderBc;

import de.persosim.editor.ui.editor.ConfigurationConstants;
import de.persosim.editor.ui.editor.IniPreferenceStoreAccessor;
import de.persosim.editor.ui.editor.PersoEditorView;
import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class StandaloneLauncher {

	public static void main(String[] args) {
		Crypto.setCryptoProvider(new ProviderBc().getCryptoProviderObject());
		PersoSimPreferenceManager.setPreferenceAccessor(new IniPreferenceStoreAccessor(Paths.get("config.properties")));

		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY);
		
		Display display = new Display();
		Shell shell = new Shell(display);
		PersoEditorView editor = new PersoEditorView();
		editor.createPartControl(shell);
		
		editor.updateContent(new Profile01());
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private static void setDefault(String key) {
		String updateDg = PersoSimPreferenceManager.getPreference(key);
		if (updateDg == null) {
			updateDg = Boolean.TRUE.toString();
		}
		PersoSimPreferenceManager.storePreference(key, updateDg);
	}

}
