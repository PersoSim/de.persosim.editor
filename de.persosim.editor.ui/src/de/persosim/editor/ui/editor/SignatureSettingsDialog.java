package de.persosim.editor.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
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

	String dsCertPath;
	String dsKeyPath;
	boolean efCardAccess;
	boolean efCardSecurity;
	boolean efChipSecurity;

	public SignatureSettingsDialog(Shell parent) {
		super(parent);
	}
	
	@Override
	protected Control createDialogArea(Composite settings) {
		settings.setLayout(new GridLayout(1, false));
		Group dgSelection = new Group(settings, SWT.NONE);
		dgSelection.setText("Data groups to be updated");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 250;
		dgSelection.setLayoutData(gd);
		dgSelection.setLayout(new RowLayout(SWT.VERTICAL));

		Button btnEfCardAccess = new Button(dgSelection, SWT.CHECK);
		btnEfCardAccess.setText("EF.CardAccess");
		btnEfCardAccess.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS)));
		
		btnEfCardAccess.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        			efCardAccess = btnEfCardAccess.getSelection();	
			}
		});
		
		Button btnEfCardSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfCardSecurity.setText("EF.CardSecurity");
		
		btnEfCardSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY)));
		
		btnEfCardSecurity.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        efCardSecurity = btnEfCardAccess.getSelection();				
			}
		});
		
		Button btnEfChipSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfChipSecurity.setText("EF.ChipSecurity");

		btnEfChipSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY)));
		
		btnEfChipSecurity.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        efChipSecurity = btnEfChipSecurity.getSelection();				
			}
		});
		
		Group certificates = new Group(settings, SWT.NONE);
		certificates.setLayoutData(gd);
		
		certificates.setLayout(new GridLayout(3, false));
		
		new Label(certificates, SWT.NONE).setText("DS Cert:");
		Text dsCertPathText = new Text(certificates, SWT.NONE);
		Button dsCertBrowse = new Button(certificates, SWT.NONE);
		dsCertBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dsCertPathText.setLayoutData(gd);
		String path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSCERT);
		if (path != null) {
			dsCertPathText.setText(path);
		}
		
		dsCertPathText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				dsCertPath = dsCertPathText.getText();
			}
		});
		
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
		        	dsCertPathText.setText(selection);
		        	dsCertPath = selection;
		        }
			}
		});
		

		new Label(certificates, SWT.NONE).setText("DS Key:");
		Text dsKeyPathText = new Text(certificates, SWT.NONE);
		Button dsKeyBrowse = new Button(certificates, SWT.NONE);
		dsKeyBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dsKeyPathText.setLayoutData(gd);

		path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSKEY);
		if (path != null) {
			dsKeyPathText.setText(path);
		}
		
		dsKeyPathText.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				dsKeyPath = dsKeyPathText.getText();
			}
		});
		
		dsKeyBrowse.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.pkcs8", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selection = fd.open();
		        dsKeyPathText.setText(selection);
		        if (selection != null){
		        	dsKeyPathText.setText(selection);
		        	dsKeyPath = selection;
		        }
			}
		});
		
		return settings;
	}
	
	@Override
	protected void okPressed() {
		super.okPressed();
		if (dsCertPath != null){
			PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSCERT, dsCertPath);	
		}
		if (dsKeyPath != null){
	        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSKEY, dsKeyPath);	
		}
        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS, Boolean.toString(efCardAccess));
        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY, Boolean.toString(efCardSecurity));
        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY, Boolean.toString(efChipSecurity));
	}
	
}
