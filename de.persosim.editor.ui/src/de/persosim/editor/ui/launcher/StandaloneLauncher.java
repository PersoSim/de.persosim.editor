package de.persosim.editor.ui.launcher;

import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.cryptoprovider.Crypto;
import org.globaltester.cryptoprovider.bc.ProviderBc;

import de.persosim.editor.ui.editor.AboutDialog;
import de.persosim.editor.ui.editor.ConfigurationConstants;
import de.persosim.editor.ui.editor.IniPreferenceStoreAccessor;
import de.persosim.editor.ui.editor.PersoEditorView;
import de.persosim.editor.ui.editor.SignatureSettingsDialog;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.TrustPointCardObject;
import de.persosim.simulator.cardobjects.TrustPointIdentifier;
import de.persosim.simulator.crypto.certificates.CardVerifiableCertificate;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.exception.CertificateNotParseableException;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.Profile01;
import de.persosim.simulator.perso.Profile02;
import de.persosim.simulator.perso.Profile03;
import de.persosim.simulator.perso.Profile04;
import de.persosim.simulator.perso.Profile05;
import de.persosim.simulator.perso.Profile06;
import de.persosim.simulator.perso.Profile07;
import de.persosim.simulator.perso.Profile08;
import de.persosim.simulator.perso.Profile09;
import de.persosim.simulator.perso.Profile10;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class StandaloneLauncher {

	public static void main(String[] args) {
		Crypto.setCryptoProvider(new ProviderBc().getCryptoProviderObject());
		PersoSimPreferenceManager.setPreferenceAccessor(new IniPreferenceStoreAccessor(Paths.get("config.properties")));

		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY);

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PersoSim Editor");

		PersoEditorView editor = new PersoEditorView();
		
		Menu topLevelMenu = new Menu(shell, SWT.BAR);
		MenuItem fileItem = new MenuItem(topLevelMenu, SWT.CASCADE);
		fileItem.setText("File");
		MenuItem settingsItem = new MenuItem(topLevelMenu, SWT.CASCADE);
		settingsItem.setText("Settings");
		MenuItem profilesItem = new MenuItem(topLevelMenu, SWT.CASCADE);
		profilesItem.setText("Profiles");
		MenuItem aboutItem = new MenuItem(topLevelMenu, SWT.CASCADE);
		aboutItem.setText("About");
		
		Menu fileMenu = new Menu(topLevelMenu);
		fileItem.setMenu(fileMenu);

		MenuItem open = new MenuItem(fileMenu, SWT.NONE);
		open.setText("Open");
		open.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.perso", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selection = fd.open();
				if (selection != null) {
					editor.updateContent(Paths.get(selection));
				}
			}
		});
		
		MenuItem saveas = new MenuItem(fileMenu, SWT.NONE);
		saveas.setText("Save as...");
		saveas.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
				fd.setText("Open");
				fd.setFilterPath(editor.getPath() == null ? "C:/" : editor.getPath().toString());
				String[] filterExt = { "*.perso", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selection = fd.open();
				if (selection != null) {
					editor.save(Paths.get(selection));
				}
			}
		});

		Menu settingsMenu = new Menu(topLevelMenu);
		settingsItem.setMenu(settingsMenu);
		
		MenuItem signingItem = new MenuItem(settingsMenu, SWT.NONE);
		signingItem.setText("Signature settings");
		signingItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new SignatureSettingsDialog(shell).open();
			}
		});

		Menu profilesMenu = new Menu(topLevelMenu);
		profilesItem.setMenu(profilesMenu);
		
		for (Personalization perso : new Personalization [] {new Profile01(), new Profile02(), new Profile03(), new Profile04(), new Profile05(), new Profile06(), new Profile07(), new Profile08(), new Profile09(), new Profile10()}){
			MenuItem profileItem = new MenuItem(profilesMenu, SWT.NONE);
			profileItem.setText(perso.getClass().getSimpleName());
			profileItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					editor.updateContent(perso);
				}
			});
		}

		Menu aboutMenu = new Menu(topLevelMenu);
		aboutItem.setMenu(aboutMenu);
		
		MenuItem aboutItem2 = new MenuItem(aboutMenu, SWT.NONE);
		aboutItem2.setText("About");
		aboutItem2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new AboutDialog(shell).open();
			}
		});
		
		shell.setMenuBar(topLevelMenu);
		
		editor.createEditor(shell);

		editor.updateContent(new DefaultPersonalization() {
			@Override
			protected void addTaTrustPoints(MasterFile mf)
					throws CertificateNotParseableException, AccessDeniedException {
				// use BSI Test-PKI CVCA root certificate
				byte [] certData = HexString.toByteArray("7F218201B67F4E82016E5F290100420E44455445535465494430303030357F4982011D060A04007F000702020202038120A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E537782207D5A0975FC2C3057EEF67530417AFFE7FB8055C126DC5C6CE94A4B44F330B5D9832026DC5C6CE94A4B44F330B5D9BBD77CBF958416295CF7E1CE6BCCDC18FF8C07B68441048BD2AEB9CB7E57CB2C4B482FFC81B7AFB9DE27E1E3BD23C23A4453BD9ACE3262547EF835C3DAC4FD97F8461A14611DC9C27745132DED8E545C1D54C72F0469978520A9FB57DBA1EEA9BC3E660A909D838D718C397AA3B561A6F7901E0E82974856A78641049BFEBA8DC7FAAB6E3BDEB3FF794DBB800848FE4F6940A4CC7EECB5159C87DA5395505892026D420A22596CD014ED1FD872DADA597DB0F8D64441041198F62D448701015F200E44455445535465494430303030357F4C12060904007F0007030102025305FC0F13FFFF5F25060105000500045F24060108000500045F374058B4E65598EFB9CA2CAFC05C80F5A907E8B69C3897C704739320896DC53492E47766841A9C3D4EAC85CE653D166B53DB06A70E735AB93C88858811EF69D6B543");
				TrustPointCardObject trustPointAt = new TrustPointCardObject(new TrustPointIdentifier(TerminalType.AT),
						new CardVerifiableCertificate(new ConstructedTlvDataObject(certData)));
				mf.addChild(trustPointAt);
			}
		});
				
		
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
