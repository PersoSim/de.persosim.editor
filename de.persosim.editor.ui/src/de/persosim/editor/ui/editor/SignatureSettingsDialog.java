package de.persosim.editor.ui.editor;

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class SignatureSettingsDialog extends Dialog{

	private Text txtDsCertPath;
	private Text txtDsKeyPath;
	private Button btnEfCardAccess;
	private Button btnEfCardSecurity;
	private Button btnEfChipSecurity;
	private Combo algoSelection;

	public SignatureSettingsDialog(Shell parent) {
		super(parent);
	}

	@Override
	protected Control createDialogArea(Composite settings) {
		settings.setLayout(new GridLayout(1, false));
		Group dgSelection = new Group(settings, SWT.NONE);
		dgSelection.setText("Data Groups to be updated");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 350;
		gd.heightHint = 90;
		dgSelection.setLayoutData(gd);
		dgSelection.setLayout(new RowLayout(SWT.VERTICAL));

		btnEfCardAccess = new Button(dgSelection, SWT.CHECK);
		btnEfCardAccess.setText("EF.CardAccess");
		btnEfCardAccess.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS)));

		btnEfCardSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfCardSecurity.setText("EF.CardSecurity");
		btnEfCardSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY)));

		btnEfChipSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfChipSecurity.setText("EF.ChipSecurity");
		btnEfChipSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY)));


		Group certificates = new Group(settings, SWT.NONE);
		certificates.setText("DS Certificate");
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd2.widthHint = 350;
		gd2.heightHint = 130;
		certificates.setLayoutData(gd2);

		certificates.setLayout(new GridLayout(3, false));

		new Label(certificates, SWT.NONE).setText("DS Cert:");
		txtDsCertPath = new Text(certificates, SWT.BORDER);
		Button dsCertBrowse = new Button(certificates, SWT.NONE);
		dsCertBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		txtDsCertPath.setLayoutData(gd);
		String path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSCERT);
		if (path != null) {
			txtDsCertPath.setText(path);
		}

		dsCertBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.der", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selection = fd.open();
		        if (selection != null){
		        	txtDsCertPath.setText(selection);
		        }
			}
		});


		new Label(certificates, SWT.NONE).setText("DS Key:");
		txtDsKeyPath = new Text(certificates, SWT.BORDER);
		Button dsKeyBrowse = new Button(certificates, SWT.NONE);
		dsKeyBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		txtDsKeyPath.setLayoutData(gd);

		path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSKEY);
		if (path != null) {
			txtDsKeyPath.setText(path);
		}

		dsKeyBrowse.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.pkcs8", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selection = fd.open();
		        if (selection != null){
		        	txtDsKeyPath.setText(selection);
		        }
			}
		});

		Composite selectionAlgo = new Composite(certificates, SWT.NONE);
		selectionAlgo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		selectionAlgo.setLayout(layout);
		new Label(selectionAlgo, SWT.NONE).setText("DS Algo:");

		algoSelection = new Combo(selectionAlgo, SWT.DROP_DOWN | SWT.READ_ONLY);
		algoSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false, 1, 1));
		String[] algos = Stream.of("SHA256withECDSA", "SHA384withECDSA", "SHA512withECDSA").toArray(String[]::new);
		Arrays.sort(algos);
		algoSelection.setItems(algos);
		String algo = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSALGO);
		int algoAsInt = 1;
		if (algo != null) {
			switch (algo) {
			case "SHA256withECDSA":
				algoAsInt = 0;
				break;
			case "SHA512withECDSA":
				algoAsInt = 2;
				break;
			case "SHA384withECDSA":
			default:
				algoAsInt = 1;
				break;
			}
		}
		algoSelection.select(algoAsInt);
		algoSelection.setVisibleItemCount(5);

		return settings;
	}


	@Override
	protected void okPressed() {
		PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS, Boolean.toString(btnEfCardAccess.getSelection()));
        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY, Boolean.toString(btnEfCardSecurity.getSelection()));
        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY, Boolean.toString(btnEfChipSecurity.getSelection()));

        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSCERT, txtDsCertPath.getText());
		PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSKEY, txtDsKeyPath.getText());
		PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSALGO, algoSelection.getText());

        super.okPressed();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Signature Settings");
	}

}
