package de.persosim.editor.ui.launcher;

import java.nio.file.Paths;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.ProviderBc;

import de.persosim.editor.ui.editor.PersoEditorView;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class StandaloneLauncher {

	public static void main(String[] args) {
		Crypto.setCryptoProvider(new ProviderBc().getCryptoProviderObject());
		PersoSimPreferenceManager.setPreferenceAccessor(new IniPreferenceStoreAccessor(Paths.get("config.properties")));
		
		Display display = new Display();
		Shell shell = new Shell(display);
		PersoEditorView editor = new PersoEditorView();
		editor.createPartControl(shell);

		editor.updateContent(Paths.get("../../de.persosim.simulator/de.persosim.simulator/personalization/profiles/DefaultPersoGt.perso").toAbsolutePath());
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
