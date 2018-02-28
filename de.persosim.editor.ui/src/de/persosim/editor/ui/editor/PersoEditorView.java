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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.NullChecker;
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
		this.persoFile = null;
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
				new AndChecker(new LengthChecker(8, 8), new NumberChecker(true))));
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
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 21, new NullChecker()));
		objectHandlers.add(new EidStringDatagroupHandler(dgMapping, 22, new NullChecker()));
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

	public void createEditor(Composite parent) {
		GridLayout gl_parent = new GridLayout(1, false);
		gl_parent.horizontalSpacing = 0;
		parent.setLayout(gl_parent);

		Composite compositeData = new Composite(parent, SWT.NONE);
		compositeData.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeData.setBounds(0, 0, 66, 66);

		tabFolder = new TabFolder(compositeData, SWT.NONE);

		Group grpControl = new Group(parent, SWT.NONE);
		grpControl.setLayout(new RowLayout(SWT.HORIZONTAL));
		grpControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpControl.setText("Actions");

		Button btnUpdateSignatures = new Button(grpControl, SWT.NONE);
		btnUpdateSignatures.setText("Update signed files");
		btnUpdateSignatures.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateSignedFiles(false);
				updateContent(perso);
			}
		});
	}

	protected void updateSignedFiles(boolean quiet) {
		boolean updateEfCardAccess = Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_ACCESS));
		boolean updateEfCardSecurity = Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CARD_SECURITY));
		boolean updateEfChipSecurity = Boolean.parseBoolean(
				PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_UPDATE_EF_CHIP_SECURITY));
		
		String dscert = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSCERT);
		String dskey = PersoSimPreferenceManager.getPreference(ConfigurationConstants.CFG_DSKEY);
		
		if (!quiet && !(updateEfCardAccess | updateEfCardSecurity | updateEfChipSecurity)){
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Info", "No files are selected to be updated, please review signature settings.");
		}
		
		if (updateEfCardAccess) {
			if (getMf().findChildren(new FileIdentifier(0x011c)).isEmpty()) {
				try {
					TlvDataObject efCardAccessTlv = TlvDataObjectFactory
							.createTLVDataObject(HexString.toByteArray("3000"));

					CardFile eidDgCardAccess = new ElementaryFile(new FileIdentifier(0x011C),
							new ShortFileIdentifier(0x1C), efCardAccessTlv.toByteArray(), SecCondition.ALLOWED,
							SecCondition.DENIED, SecCondition.DENIED);
					getMf().addChild(eidDgCardAccess);
				} catch (AccessDeniedException e) {
					BasicLogger.logException(getClass(), e, LogLevel.WARN);
				}
			}

			new SecInfoFileUpdater(null, new FileIdentifier(0x011c), SecInfoPublicity.PUBLIC).execute(perso);
		}

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
						BasicLogger.logException(getClass(), e, LogLevel.WARN);
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
						BasicLogger.logException(getClass(), e, LogLevel.WARN);
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

	public void save(Path path){
		for (DfEditor editor : toBePersisted) {
			editor.persist();
		}

		updateSignedFiles(true);

		if (perso != null) {
			if (path != null){
				persoFile = path;
				
				PersonalizationFactory.marshal(perso, persoFile.toAbsolutePath().toString());
				updateContent(perso);
			}
		}
	}

	public Path getPath() {
		return persoFile;
	}
}
