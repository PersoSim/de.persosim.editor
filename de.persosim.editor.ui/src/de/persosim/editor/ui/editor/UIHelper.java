package de.persosim.editor.ui.editor;

import static org.globaltester.logging.BasicLogger.log;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.tags.LogLevel;

public class UIHelper {

	private UIHelper() {
		// hide implicit public constructor
	}

	public static Shell getShell() {
		Shell shell = Display.getDefault().getActiveShell();
		if (shell == null && (Display.getDefault().getShells().length > 0)) {
			shell = Display.getDefault().getShells()[0];
		}
		if (shell == null) {
			String errorText = "No shell available. Cannot show dialog!";
			System.out.println(errorText); // NOSONAR
			log(UIHelper.class, errorText, LogLevel.ERROR);
		}
		return shell;
	}
}
