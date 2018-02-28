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

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.NumberChecker;
import de.persosim.editor.ui.editor.checker.OrChecker;
import de.persosim.editor.ui.editor.checker.UpperCaseTextFieldChecker;
import de.persosim.editor.ui.editor.handlers.ConstructedTlvHandler;
import de.persosim.editor.ui.editor.handlers.DatagroupDumpHandler;
import de.persosim.editor.ui.editor.handlers.DatagroupHandler;
import de.persosim.editor.ui.editor.handlers.DefaultHandlerProvider;
import de.persosim.editor.ui.editor.handlers.EidDatagroup17HandlerSingularGeneralPlace;
import de.persosim.editor.ui.editor.handlers.EidDatagroup17SetOfGeneralPlaceHandler;
import de.persosim.editor.ui.editor.handlers.EidDatagroup9Handler;
import de.persosim.editor.ui.editor.handlers.EidDedicatedFileHandler;
import de.persosim.editor.ui.editor.handlers.EidOptionalDataDatagroupHandler;
import de.persosim.editor.ui.editor.handlers.EidStringDatagroupHandler;
import de.persosim.editor.ui.editor.handlers.LengthChecker;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.editor.ui.editor.handlers.PrimitiveTlvHandler;
import de.persosim.editor.ui.editor.handlers.StringTlvHandler;
import de.persosim.editor.ui.editor.signing.SecInfoCmsBuilder;
import de.persosim.editor.ui.editor.signing.SecInfoFileUpdater;
import de.persosim.editor.ui.editor.signing.SignedSecInfoFileUpdater;
import de.persosim.simulator.cardobjects.CardFile;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.DefaultPersonalization;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.PersonalizationFactory;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.utils.BitField;
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
		dgMapping.put((Integer) 0x1E, "EF.DIR");

		List<ObjectHandler> objectHandlers = new LinkedList<>();
		objectHandlers.add(new DatagroupDumpHandler(dgMapping));

		DefaultHandlerProvider provider = new DefaultHandlerProvider(objectHandlers);

		toBePersisted.add(DatagroupEditorBuilder.build(editor, perso, getMf(), provider));
		tbtmmf.setControl(editor);

		dgMapping = EidDgMapping.getMapping();

		DedicatedFile df = getDf(HexString.toByteArray(DefaultPersonalization.AID_EID));

		objectHandlers = new LinkedList<>();

		provider = new DefaultHandlerProvider(objectHandlers);
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 1,
				new AndChecker(new LengthChecker(2, 2), new UpperCaseTextFieldChecker())));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 2, new AndChecker(
				new OrChecker(new LengthChecker(1, 1), new LengthChecker(3, 3)), new UpperCaseTextFieldChecker())));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 3,
				new AndChecker(new LengthChecker(8, 8), new NumberChecker())));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 4, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 5, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 6, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 7, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 8,
				new AndChecker(new LengthChecker(8, 8), new NumberChecker())));
		objectHandlers.add(new EidDatagroup9Handler(dgMapping));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 10, new AndChecker(
				new OrChecker(new LengthChecker(1, 1), new LengthChecker(3, 3)), new UpperCaseTextFieldChecker())));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 11,
				new AndChecker(new LengthChecker(1, 1), new UpperCaseTextFieldChecker())));
		objectHandlers.add(new EidOptionalDataDatagroupHandler(dgMapping, 12,
				new EidDataTemplateProvider(Collections.emptySet())));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 13, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new EidDatagroup17HandlerSingularGeneralPlace(dgMapping));
		objectHandlers.add(new EidDatagroup17SetOfGeneralPlaceHandler(dgMapping,
				new EidDataTemplateProvider(Collections.emptySet())));
		objectHandlers.add(new EidOptionalDataDatagroupHandler(dgMapping, 21,
				new EidDataTemplateProvider(Collections.emptySet())));
		objectHandlers.add(new DatagroupHandler(dgMapping));
		objectHandlers.add(new EidDedicatedFileHandler(df, provider));
		objectHandlers.add(new ConstructedTlvHandler(true));
		objectHandlers.add(new StringTlvHandler(true, new UpperCaseTextFieldChecker()));
		objectHandlers.add(new PrimitiveTlvHandler(true));

		tbtmmf = new TabItem(tabFolder, SWT.NONE);
		tbtmmf.setText("eID");
		editor = new Composite(tabFolder, SWT.NONE);
		toBePersisted.add(DatagroupEditorBuilder.build(editor, perso, df, provider));
		tbtmmf.setControl(editor);
	}

	private DedicatedFile getDf(byte[] aid) {
		MasterFile mf = getMf();

		Collection<CardObject> currentDfCandidates = mf.findChildren(new DedicatedFileIdentifier(aid));

		if (currentDfCandidates.isEmpty()) {
			return null;
		}

		return (DedicatedFile) currentDfCandidates.iterator().next();
	}

	private MasterFile getMf() {
		return PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class)
				.getObjectTree();
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
				FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
				fd.setText("Save");
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.perso", "*.*" };
				fd.setFilterExtensions(filterExt);
				String selection = fd.open();
				if (selection != null) {
					persoFile = Paths.get(selection);
					doSave(null);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		Button btnUpdateSignatures = new Button(grpControl, SWT.NONE);
		btnUpdateSignatures.setText("Update signed files");
		btnUpdateSignatures.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSignedFiles();
				updateContent(perso);
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

	protected void updateSignedFiles() {
		if (Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS))) {

			if (getMf().findChildren(new FileIdentifier(0x011c)).isEmpty()) {
				try {
					TlvDataObject efCardAccessTlv = TlvDataObjectFactory
							.createTLVDataObject(HexString.toByteArray("3000"));

					CardFile eidDgCardAccess = new ElementaryFile(new FileIdentifier(0x011C),
							new ShortFileIdentifier(0x1C), efCardAccessTlv.toByteArray(), SecCondition.ALLOWED,
							SecCondition.DENIED, SecCondition.DENIED);
					getMf().addChild(eidDgCardAccess);
				} catch (AccessDeniedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

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
				if (getMf().findChildren(new FileIdentifier(0x011D)).isEmpty()) {
					try {
						CardFile eidDgCardSecurity = new ElementaryFile(new FileIdentifier(0x011D),
								new ShortFileIdentifier(0x1D), HexString.toByteArray("3100"), new TaSecurityCondition(),
								SecCondition.DENIED, SecCondition.DENIED);
						getMf().addChild(eidDgCardSecurity);
					} catch (AccessDeniedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

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
				if (getMf().findChildren(new FileIdentifier(0x011B)).isEmpty()) {
					try {
						SecCondition taWithIs = new TaSecurityCondition(TerminalType.IS, null);
						SecCondition taWithAtPrivileged = new TaSecurityCondition(TerminalType.AT,
								new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(3)));

						CardFile eidDgChipSecurity = new ElementaryFile(new FileIdentifier(0x011B),
								new ShortFileIdentifier(0x1B), HexString.toByteArray("3100"),
								new OrSecCondition(taWithIs, taWithAtPrivileged), SecCondition.DENIED,
								SecCondition.DENIED);

						getMf().addChild(eidDgChipSecurity);
					} catch (AccessDeniedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

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

	}

	@Focus
	public void setFocus() {
	}

	@Persist
	void doSave(@Optional IProgressMonitor monitor) {
		for (DfEditor editor : toBePersisted) {
			editor.persist();
		}

		updateSignedFiles();

		if (perso != null && persoFile != null) {
			PersonalizationFactory.marshal(perso, persoFile.toAbsolutePath().toString());
			updateContent(perso);
		}
	}
}
