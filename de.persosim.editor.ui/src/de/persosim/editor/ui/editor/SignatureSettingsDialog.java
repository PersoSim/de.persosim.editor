package de.persosim.editor.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
		
		btnEfCardAccess.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS, Boolean.toString(((Button)e.getSource()).getSelection()));				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button btnEfCardSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfCardSecurity.setText("EF.CardSecurity");
		
		btnEfCardSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY)));
		
		btnEfCardSecurity.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY, Boolean.toString(((Button)e.getSource()).getSelection()));				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button btnEfChipSecurity = new Button(dgSelection, SWT.CHECK);
		btnEfChipSecurity.setText("EF.ChipSecurity");

		btnEfChipSecurity.setSelection(Boolean.parseBoolean(PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY)));
		
		btnEfChipSecurity.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY, Boolean.toString(((Button)e.getSource()).getSelection()));				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Group certificates = new Group(settings, SWT.NONE);
		certificates.setLayoutData(gd);
		
		certificates.setLayout(new GridLayout(3, false));
		
		new Label(certificates, SWT.NONE).setText("DS Cert:");
		Text dsCertPath = new Text(certificates, SWT.NONE);
		Button dsCertBrowse = new Button(certificates, SWT.NONE);
		dsCertBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dsCertPath.setLayoutData(gd);
		String path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSCERT);
		if (path != null) {
			dsCertPath.setText(path);
		}
		dsCertBrowse.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.der", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selection = fd.open();
		        dsCertPath.setText(selection);
		        if (selection != null){
			        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSCERT, selection);	
		        }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		

		new Label(certificates, SWT.NONE).setText("DS Key:");
		Text dsKeyPath = new Text(certificates, SWT.NONE);
		Button dsKeyBrowse = new Button(certificates, SWT.NONE);
		dsKeyBrowse.setText("Browse");
		gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		dsKeyPath.setLayoutData(gd);

		path = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSKEY);
		if (path != null) {
			dsKeyPath.setText(path);
		}
		dsKeyBrowse.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
		        FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
		        fd.setText("Open");
		        fd.setFilterPath("C:/");
		        String[] filterExt = { "*.pkcs8", "*.*" };
		        fd.setFilterExtensions(filterExt);
		        String selection = fd.open();
		        dsKeyPath.setText(selection);
		        if (selection != null){
			        PersoSimPreferenceManager.storePreference(ConfigurationConstants.CFG_DSKEY, selection);	
		        }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return settings;
	}
	
}
