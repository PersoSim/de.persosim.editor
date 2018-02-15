package de.persosim.editor.ui.editor;

import java.nio.file.Paths;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.ProviderBc;

public class StandaloneLauncher {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		PersoEditorView editor = new PersoEditorView(shell);


		Crypto.setCryptoProvider(new ProviderBc().getCryptoProviderObject());
		
		editor.updateContent(Paths.get("../../de.persosim.simulator/de.persosim.simulator/personalization/profiles/DefaultPersoGt.perso"));
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
