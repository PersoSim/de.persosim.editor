package de.persosim.editor.ui.launcher;

import java.nio.file.Paths;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
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
import de.persosim.editor.ui.launcher.Persos.DefaultPerso;
import de.persosim.simulator.perso.Personalization;
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
				if (editor.hasUnsavedChanges()) {
					if (MessageDialog.openQuestion(shell, "Unsaved changes", "There are unsaved changes, do you want to save them now?")) {
						openSaveDialog(editor);
					}
				}
				
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
				openSaveDialog(editor);
			}
		});
		
		MenuItem exit = new MenuItem(fileMenu, SWT.NONE);
		exit.setText("Exit");
		exit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (editor.hasUnsavedChanges()) {
					if (MessageDialog.openQuestion(shell, "Unsaved changes", "There are unsaved changes, do you want to save them now?")) {
						openSaveDialog(editor);
					}
				}
				shell.close();
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
		
		for (Personalization perso : new Personalization [] {new Persos.Profile01(), new Persos.Profile02(), new Persos.Profile03(), new Persos.Profile04(), new Persos.Profile05(), new Persos.Profile06(), new Persos.Profile07(), new Persos.Profile08(), new Persos.Profile09(), new Persos.Profile10()}){
			
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

		editor.updateContent(new DefaultPerso());
				
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (editor.hasUnsavedChanges()) {
					event.doit = MessageDialog.openQuestion(shell, "Unsaved changes", "There are unsaved changes, do you want to close the application?");
				}
			}
		});
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected static void openSaveDialog(PersoEditorView editor) {
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

	private static void setDefault(String key) {
		String updateDg = PersoSimPreferenceManager.getPreference(key);
		if (updateDg == null) {
			updateDg = Boolean.FALSE.toString();
		}
		PersoSimPreferenceManager.storePreference(key, updateDg);
	}

}
