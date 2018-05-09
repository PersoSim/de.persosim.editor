package de.persosim.editor.ui.editor;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

public class AboutDialog extends Dialog {

	public static final String PERSOSIM_URL = "https://persosim.secunet.com";

	private static final String FONT_NAME = "Helvetica";

	public AboutDialog(Shell parentShell) {
		super(parentShell);

	}

	@Override
	protected Control createDialogArea(Composite parentComposite) {
		final Composite parent = parentComposite;

		parent.setLayout(new GridLayout(1, false));

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, true));

		Label label2 = new Label(container, SWT.NONE);
		label2.setText("PersoSim Editor");
		label2.setFont(new Font(container.getDisplay(), FONT_NAME, 24, SWT.BOLD));

		Label label3 = new Label(container, SWT.NONE);
		label3.setText(getProductVersion());
		label3.setFont(new Font(container.getDisplay(), FONT_NAME, 12, SWT.NONE));

		Link link = new Link(container, SWT.NONE);
		link.setText("Visit our web site at " + PERSOSIM_URL);
		link.setSize(link.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("About PersoSim Editor");
	}
	
	/**
	 * This method returns the bundleversion as a String
	 * @return version
	 */
	public static String getProductVersion() {
        final IProduct product = Platform.getProduct();
        final Bundle bundle = product.getDefiningBundle();
        final Version version = bundle.getVersion();
        return version.toString();
    }

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}
}
