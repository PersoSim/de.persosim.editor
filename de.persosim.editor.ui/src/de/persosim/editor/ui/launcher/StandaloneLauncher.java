package de.persosim.editor.ui.launcher;

import java.nio.file.Paths;

import jakarta.annotation.PostConstruct;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
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
import de.persosim.editor.ui.launcher.Persos.Profile01;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;

public class StandaloneLauncher {

	public static void main(String[] args) {
		Crypto.setCryptoProvider(ProviderBc.getInstance().getCryptoProviderObject());
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new IniPreferenceStoreAccessor(Paths.get("config.properties")));

		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY);

		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PersoSim Editor");

		createGui(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!shell.getDisplay().readAndDispatch()) {
				shell.getDisplay().sleep();
			}
		}
	}

	@PostConstruct
	private static void createGui(Composite parent) {
		PersoEditorView editor = new PersoEditorView();

		Menu topLevelMenu = new Menu(parent.getShell(), SWT.BAR);
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
				checkAndSave(parent.getShell(), editor);

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

		MenuItem exportProfile = new MenuItem(fileMenu, SWT.NONE);
		exportProfile.setText("Export profile");
		exportProfile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] filterExt = { "*.json", "*.*" };
				openSaveDialog(editor, filterExt, true);
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
				parent.getShell().close();
			}
		});

		Menu settingsMenu = new Menu(topLevelMenu);
		settingsItem.setMenu(settingsMenu);

		MenuItem signingItem = new MenuItem(settingsMenu, SWT.NONE);
		signingItem.setText("Signature settings");
		signingItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				new SignatureSettingsDialog(parent.getShell()).open();
			}
		});

		Menu profilesMenu = new Menu(topLevelMenu);
		profilesItem.setMenu(profilesMenu);

		MenuItem profileItem = new MenuItem(profilesMenu, SWT.NONE);
		profileItem.setText("Default Profile");
		profileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkAndSave(parent.getShell(), editor);
				editor.updateContent(Persos.getPerso(0));
			}
		});

		int numberOfProfiles = 19;
		for (int i = 1; i <= numberOfProfiles; i++) {
			int currentNumber = i;
			profileItem = new MenuItem(profilesMenu, SWT.NONE);
			if (i <= 10) {
				profileItem.setText("Profile " + i);
			} else if ( i <= 15) {
				profileItem.setText("ProfileUB " + (i-10));
			} else {
				profileItem.setText("ProfileOA " + (i-15));
			}
			profileItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber));
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
				new AboutDialog(parent.getShell()).open();
			}
		});

		parent.getShell().setMenuBar(topLevelMenu);

		editor.createEditor(parent);

		editor.updateContent(new Profile01());

		parent.addDisposeListener(e -> checkAndSave(parent.getShell(), editor));
	}

	protected static void openSaveDialog(PersoEditorView editor) {
		String[] filterExt = { "*.perso", "*.*" };
		openSaveDialog(editor, filterExt, false);
	}

	protected static void openSaveDialog(PersoEditorView editor, String[] filterExt, boolean isExport) {
		FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
		fd.setText("Save");
		fd.setFilterPath(editor.getPath() == null ? "C:/" : editor.getPath().toString());
		if (filterExt != null)
			fd.setFilterExtensions(filterExt);
		String selection = fd.open();
		if (selection != null) {
			boolean write = true;
			if (Paths.get(selection).toFile().exists()) {
				write = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "File exists",
						"Do you want to overwrite the file at " + selection + "?");
			}
			if (write) {
				editor.save(Paths.get(selection), isExport);
			}
		}
	}

	private static void setDefault(String key) {
		String updateDg = PersoSimPreferenceManager.getPreference(key);
		if (updateDg == null) {
			updateDg = Boolean.FALSE.toString();
		}
		PersoSimPreferenceManager.storePreference(key, updateDg);
	}

	/**
	 * Check if there are unsaved changes and allow to save if needed.
	 * @param shell
	 * @param editor
	 */
	private static void checkAndSave(Shell shell, PersoEditorView editor) {
		if (editor.hasUnsavedChanges() && MessageDialog.openQuestion(shell, "Unsaved changes",
				"There are unsaved changes, do you want to save them now?")) {
			openSaveDialog(editor);
		}
	}

}
