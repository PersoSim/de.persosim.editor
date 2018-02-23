package de.persosim.editor.ui.editor;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.handlers.ConstructedTlvHandler;
import de.persosim.editor.ui.editor.handlers.DatagroupDumpHandler;
import de.persosim.editor.ui.editor.handlers.DatagroupHandler;
import de.persosim.editor.ui.editor.handlers.DefaultHandlerProvider;
import de.persosim.editor.ui.editor.handlers.EidDatagroup17Handler;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.editor.ui.editor.handlers.PrimitiveTlvHandler;
import de.persosim.editor.ui.editor.signing.SecInfoCmsBuilder;
import de.persosim.editor.ui.editor.signing.SecInfoFileUpdater;
import de.persosim.editor.ui.editor.signing.SignedSecInfoFileUpdater;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.utils.HexString;

public class PersoEditorView {
	public static final String ID = "de.persosim.editor.e4.ui.plugin.partdescriptor.persoeditor";
	private TabFolder tabFolder;
	private Collection<DfEditor> toBePersisted = new HashSet<>();
	private Personalization perso;
	private Path persoFile;

	public void updateContent(Path personalizationFile) {
		if (!Files.exists(personalizationFile)) {
			throw new IllegalArgumentException("Personalization file does not exist");
		}

		this.persoFile = personalizationFile;

		try (Reader reader = Files.newBufferedReader(personalizationFile)) {
			Personalization perso = (Personalization) PersonalizationFactory.unmarshal(reader);
			updateContent(perso);
		} catch (IOException e) {
			BasicLogger.logException(getClass(), "Reading the personalization file failed.", e, LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "File error",
					"Reading the personalization file failed.");
		}
	}

	public void updateContent(Personalization perso) {
		updateUi(perso);
	}

	private void updateUi(Personalization perso) {
		this.perso = perso;

		toBePersisted = new HashSet<>();

		for (TabItem current : tabFolder.getItems()) {
			current.dispose();
		}

		TabItem tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("Masterfile");
		Composite editor = new Composite(tabFolder, SWT.NONE);

		Map<Integer, String> dgMapping = new HashMap<>();
		dgMapping.put((Integer) 0x1C, "EF.CardAccess");
		dgMapping.put((Integer) 0x1D, "EF.CardSecurity");
		dgMapping.put((Integer) 0x1B, "EF.ChipSecurity");

		List<ObjectHandler> objectHandlers = new LinkedList<>();
		objectHandlers.add(new DatagroupDumpHandler(Collections.emptyMap()));
		objectHandlers.add(new ConstructedTlvHandler(false));
		objectHandlers.add(new PrimitiveTlvHandler(false));

		toBePersisted
				.add(DatagroupEditorBuilder.build(editor, perso, null, new DefaultHandlerProvider(objectHandlers)));
		tbtmmf.setControl(editor);

		dgMapping = new HashMap<>();
		dgMapping.put((Integer) 0x01, "Document Type");
		dgMapping.put((Integer) 0x02, "Issuing State, Region and Municipality");
		dgMapping.put((Integer) 0x03, "Date of Expiry, Date of Issuance");
		dgMapping.put((Integer) 0x04, "Given Names");
		dgMapping.put((Integer) 0x05, "Family Names");
		dgMapping.put((Integer) 0x06, "Nom de Plume");
		dgMapping.put((Integer) 0x07, "Academic Title");
		dgMapping.put((Integer) 0x08, "Date of Birth");
		dgMapping.put((Integer) 0x09, "Place of Birth");
		dgMapping.put((Integer) 0x0A, "Nationality");
		dgMapping.put((Integer) 0x0B, "Sex");
		dgMapping.put((Integer) 0x0C, "Optional Data");
		dgMapping.put((Integer) 0x0D, "Birth Name");
		dgMapping.put((Integer) 0x0E, "Written Signature");
		dgMapping.put((Integer) 0x0F, "RFU");
		dgMapping.put((Integer) 0x10, "RFU");
		dgMapping.put((Integer) 0x11, "Normal Place of Residence (multiple)");
		dgMapping.put((Integer) 0x12, "Municipality ID");
		dgMapping.put((Integer) 0x13, "Residence Permit I");
		dgMapping.put((Integer) 0x14, "Residence Permit II");
		dgMapping.put((Integer) 0x15, "Phone Number");
		dgMapping.put((Integer) 0x16, "Email Address");

		objectHandlers = new LinkedList<>();
		objectHandlers.add(new EidDatagroup17Handler(dgMapping));
		objectHandlers.add(new DatagroupHandler(dgMapping));
		objectHandlers.add(new ConstructedTlvHandler(true));
		objectHandlers.add(new PrimitiveTlvHandler(true));

		tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("eID");
		editor = new Composite(tabFolder, SWT.NONE);
		toBePersisted.add(DatagroupEditorBuilder.build(editor, perso,
				new DedicatedFileIdentifier(HexString.toByteArray(DefaultPersonalization.AID_EID)),
				new DefaultHandlerProvider(objectHandlers)));
		tbtmmf.setControl(editor);
	}

	@PostConstruct
	public void createPartControl(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		parent.setLayout(gl_parent);

		Group grpData = new Group(parent, SWT.NONE);
		grpData.setLayout(new FillLayout(SWT.HORIZONTAL));
		grpData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpData.setText("Data");
		grpData.setBounds(0, 0, 66, 66);

		tabFolder = new TabFolder(grpData, SWT.NONE);

		Group grpControl = new Group(parent, SWT.NONE);
		grpControl.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpControl.setText("Actions");

		Button btnOpen = new Button(grpControl, SWT.NONE);
		btnOpen.setText("Open");
		btnOpen.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.perso", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selection = fd.open();
				if (selection != null) {
					updateContent(Paths.get(selection));
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button btnSave = new Button(grpControl, SWT.NONE);
		btnSave.setText("Save");
		btnSave.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				doSave(null);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button btnSignatureSettings = new Button(grpControl, SWT.NONE);
		btnSignatureSettings.setText("Signature Settings");

		btnSignatureSettings.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				new SignatureSettingsDialog(Display.getCurrent().getActiveShell()).open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Focus
	public void setFocus() {
	}

	@Persist
	void doSave(@Optional IProgressMonitor monitor) {
		for (DfEditor editor : toBePersisted) {
			editor.persist();
		}

		if (Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS))) {
			new SecInfoFileUpdater(null, new FileIdentifier(0x011c), SecInfoPublicity.PUBLIC).execute(perso);
		}

		String dscert = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSCERT);
		String dskey = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSKEY);

		boolean updateEfCardSecurity = Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY));
		boolean updateEfChipSecurity = Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY));

		if (updateEfCardSecurity || updateEfChipSecurity) {
			if (dscert == null || dskey == null) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error on document signer certificates",
						"Please check the document signer certificate settings.");
			}
		}

		if (updateEfCardSecurity) {
			try {
				SecInfoCmsBuilder builder = new SecInfoCmsBuilder(Files.readAllBytes(Paths.get(dscert)),
						Files.readAllBytes(Paths.get(dskey)));
				new SignedSecInfoFileUpdater(null, new FileIdentifier(0x011d), SecInfoPublicity.PRIVILEGED, builder)
						.execute(perso);
			} catch (InvalidKeySpecException | IOException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Error on reading document signer certificates",
						"Please check the document signer certificate settings.");
			}
		}

		if (updateEfChipSecurity) {
			try {
				SecInfoCmsBuilder builder = new SecInfoCmsBuilder(Files.readAllBytes(Paths.get(dscert)),
						Files.readAllBytes(Paths.get(dskey)));
				new SignedSecInfoFileUpdater(null, new FileIdentifier(0x011b), SecInfoPublicity.AUTHENTICATED, builder)
						.execute(perso);
			} catch (InvalidKeySpecException | IOException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(),
						"Error on reading document signer certificates",
						"Please check the document signer certificate settings.");
			}
		}

		if (perso != null && persoFile != null) {
			PersonalizationFactory.marshal(perso, persoFile.toAbsolutePath().toString());
			updateContent(perso);
		}
	}
}
