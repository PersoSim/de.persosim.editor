package de.persosim.editor.ui.launcher;

import static org.globaltester.logging.BasicLogger.log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.AboutDialog;
import de.persosim.editor.ui.editor.ConfigurationConstants;
import de.persosim.editor.ui.editor.PersoEditorView;
import de.persosim.editor.ui.editor.SignatureSettingsDialog;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.export.ProfileHelper;
import de.persosim.simulator.preferences.IniPreferenceStoreAccessor;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import jakarta.annotation.PostConstruct;

public class StandaloneLauncher {


	public static void main(String[] args) {
		Crypto.setCryptoProvider(ProviderBc.getInstance().getCryptoProviderObject());
		PersoSimPreferenceManager.setPreferenceAccessorIfNotAvailable(new IniPreferenceStoreAccessor(Paths.get("config.properties")));

		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY);
		setDefault(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY);

		System.out.println(Paths.get("config.properties").toFile().getAbsolutePath());
		System.out.println(Paths.get("config.properties").toFile().exists());
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("PersoSim Editor");

		String currentAbsolutePath = Paths.get("").toAbsolutePath().toString();
		log(StandaloneLauncher.class, "Current absolute path is: " + currentAbsolutePath, LogLevel.TRACE);
		ProfileHelper.setRootPathPersoFiles(Path.of(currentAbsolutePath + File.separator + "../../de.persosim.simulator/de.persosim.simulator/personalization/" + ProfileHelper.PERSO_FILES_PARENT_DIR));

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

		ProfileHelper.createAllMissingOverlayProfileFiles(ProfileHelper.getRootPathPersoFiles());

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
		exportProfile.setText("Export profile to JSON");
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

		populateProfilesMenu(parent, editor, topLevelMenu, profilesItem);

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

		Personalization perso = Persos.getPerso(1);
		editor.updateContent(perso);

		parent.addDisposeListener(e -> checkAndSave(parent.getShell(), editor));
	}


	private static void populateProfilesMenu(Composite parent, PersoEditorView editor, Menu topLevelMenu, MenuItem profilesItem)
	{
		Menu profilesMenu = new Menu(topLevelMenu);
		profilesItem.setMenu(profilesMenu);

		// populateProfilesSubMenus(parent, editor, profilesMenu);

		MenuItem profileItem = new MenuItem(profilesMenu, SWT.NONE);
		profileItem.setText("Default Profile");
		profileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkAndSave(parent.getShell(), editor);
				editor.updateContent(Persos.getPerso(0));
			}
		});
		for (int i = 1; i <= Persos.NUMBER_OF_PROFILES_CLASSIC; i++) {
			int currentNumber = i;
			profileItem = new MenuItem(profilesMenu, SWT.NONE);
			profileItem.setText("Profile " + currentNumber);
			profileItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber));
				}
			});
		}
		for (int i = 1; i <= Persos.NUMBER_OF_PROFILES_UB; i++) {
			int currentNumber = i;
			profileItem = new MenuItem(profilesMenu, SWT.NONE);
			profileItem.setText("ProfileUB " + currentNumber);
			profileItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber + Persos.NUMBER_OF_PROFILES_CLASSIC));
				}
			});
		}
		for (int i = 1; i <= Persos.NUMBER_OF_PROFILES_OA; i++) {
			int currentNumber = i;
			profileItem = new MenuItem(profilesMenu, SWT.NONE);
			profileItem.setText("ProfileOA " + i);
			profileItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber + Persos.NUMBER_OF_PROFILES_CLASSIC + Persos.NUMBER_OF_PROFILES_UB));
				}
			});
		}
	}


	private static void populateProfilesSubMenus(Composite parent, PersoEditorView editor, Menu profilesMenu)
	{
		Menu profileSubMenuBetaPKI = new Menu(profilesMenu);
		MenuItem profileSubMenuItemBetaPKI = new MenuItem(profilesMenu, SWT.CASCADE);
		profileSubMenuItemBetaPKI.setText("Beta-PKI");
		profileSubMenuItemBetaPKI.setMenu(profileSubMenuBetaPKI);

		for (int i = 1; i <= Persos.NUMBER_OF_PROFILES_BETA_PKI; i++) {
			int currentNumber = i;
			MenuItem profileSubItemBetaPKI = new MenuItem(profileSubMenuBetaPKI, SWT.NONE);
			profileSubItemBetaPKI.setText("Profile" + String.format("%02d", i) + "BetaPki");
			profileSubItemBetaPKI.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber + Persos.NUMBER_OF_PROFILES_CLASSIC + Persos.NUMBER_OF_PROFILES_UB + Persos.NUMBER_OF_PROFILES_OA));
				}
			});
		}


		Menu profileSubMenuTr03124 = new Menu(profilesMenu);
		MenuItem profileSubMenuItemTR03124 = new MenuItem(profilesMenu, SWT.CASCADE);
		profileSubMenuItemTR03124.setText("TR-03124");
		profileSubMenuItemTR03124.setMenu(profileSubMenuTr03124);

		for (int i = 1; i <= Persos.NUMBER_OF_PROFILES_BETA_PKI; i++) {
			int currentNumber = i;
			MenuItem profileSubItemBetaTR03124 = new MenuItem(profileSubMenuTr03124, SWT.NONE);
			profileSubItemBetaTR03124.setText("Profile" + String.format("%02d", i) + "Tr03124");
			profileSubItemBetaTR03124.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					checkAndSave(parent.getShell(), editor);
					editor.updateContent(Persos.getPerso(currentNumber + Persos.NUMBER_OF_PROFILES_CLASSIC + Persos.NUMBER_OF_PROFILES_UB + Persos.NUMBER_OF_PROFILES_OA + Persos.NUMBER_OF_PROFILES_BETA_PKI));
				}
			});
		}
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
