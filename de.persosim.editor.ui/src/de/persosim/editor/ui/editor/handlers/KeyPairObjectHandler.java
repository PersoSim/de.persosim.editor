package de.persosim.editor.ui.editor.handlers;


import static org.globaltester.logging.BasicLogger.log;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECFieldFp;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.launcher.Persos;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.perso.export.Key;
import de.persosim.simulator.perso.export.OverlayProfile;
import de.persosim.simulator.perso.export.ProfileHelper;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.GenericOid;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.utils.HexString;

public class KeyPairObjectHandler extends AbstractObjectHandler
{
	@Override
	public boolean canHandle(Object object)
	{
		if (object instanceof KeyPairObject) {
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider)
	{
		if (object instanceof KeyPairObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((KeyPairObject) object, provider, item);
			return item;
		}
		return null;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider)
	{
		if (object instanceof KeyPairObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((KeyPairObject) object, provider, item);
			return item;
		}
		return null;
	}

	private void handleItem(KeyPairObject kpo, HandlerProvider provider, TreeItem item)
	{
		item.setData(kpo);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	public void setText(TreeItem item)
	{
		if (item.getData() instanceof KeyPairObject) {
			KeyPairObject keyPairObject = (KeyPairObject) item.getData();

			String text = getType();

			KeyIdentifier keyIdentifier = keyPairObject.getPrimaryIdentifier();
			if (keyIdentifier != null) {
				text += " (keyId " + keyIdentifier.getKeyReference() + ")";
			}

			ObjectHandler handler = (ObjectHandler) item.getData(HANDLER);
			if (handler != null) {
				text += getChangedText(item);
			}

			item.setText(text);
		}
	}

	@Override
	protected String getType()
	{
		return "Key pair";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item)
	{
		if (item.getData() instanceof KeyPairObject) {
			composite.setLayout(new GridLayout(2, false));

			new Label(composite, SWT.NULL).setText("Public key: ");

			Text pubKeyText = new Text(composite, SWT.BORDER | SWT.MULTI);
			pubKeyText.setFont(JFaceResources.getTextFont());

			pubKeyText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			pubKeyText.setEditable(false);

			new Label(composite, SWT.NULL).setText("Private key: ");

			Text privKeyText = new Text(composite, SWT.BORDER | SWT.MULTI);
			privKeyText.setFont(JFaceResources.getTextFont());

			privKeyText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			privKeyText.setEditable(false);

			showValues(item, pubKeyText, privKeyText);

			Composite buttons = new Composite(composite, SWT.NONE);
			buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			buttons.setLayout(new RowLayout());

			Button replace = new Button(buttons, SWT.PUSH);
			replace.setText("Generate new key material");
			replace.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e)
				{
					generateNewKeyMaterial((KeyPairObject) item.getData());

					showValues(item, pubKeyText, privKeyText);

				}
			});

			Composite buttons2 = new Composite(composite, SWT.NONE);
			buttons2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			buttons2.setLayout(new RowLayout());

			Personalization selectedInternalPerso = Persos.getSelectedInternalPerso();
			if (selectedInternalPerso != null) {
				Button replacePersist = new Button(buttons2, SWT.PUSH);
				replacePersist.setText("Persist in " + selectedInternalPerso.getClass().getSimpleName());
				replacePersist.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e)
					{
						persistInOverlayProfileFile((KeyPairObject) item.getData(), selectedInternalPerso);

						showValues(item, pubKeyText, privKeyText);

					}
				});
			}

			composite.pack();

		}
	}

	private void showValues(TreeItem item, Text pubKeyText, Text privKeyText)
	{
		KeyPairObject keyPairObject = (KeyPairObject) item.getData();

		ECPublicKey pubKey = (ECPublicKey) keyPairObject.getKeyPair().getPublic();
		int referenceLength = CryptoUtil.getPublicPointReferenceLengthL(((ECFieldFp) pubKey.getParams().getCurve().getField()).getP());
		byte[] pubKeyBytes = CryptoUtil.encode(pubKey.getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED);

		ECPrivateKey privKey = (ECPrivateKey) keyPairObject.getKeyPair().getPrivate();
		byte[] privKeyBytes = privKey.getS().toByteArray();

		pubKeyText.setText(HexString.dump(pubKeyBytes));
		privKeyText.setText(HexString.dump(privKeyBytes));
	}

	private void generateNewKeyMaterial(KeyPairObject keyPairObject)
	{
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH");
			AlgorithmParameterSpec params = ((ECPublicKey) keyPairObject.getKeyPair().getPublic()).getParams();
			kpg.initialize(params);
			KeyPair newKeyPair = kpg.generateKeyPair();

			keyPairObject.setKeyPair(newKeyPair);
		}
		catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to generate new key material:\n" + e.getMessage());
		}
		catch (AccessDeniedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to store new key material in KeyPairObject:\n" + e.getMessage());
		}

	}

	private void persistInOverlayProfileFile(KeyPairObject keyPairObject, Personalization perso)
	{
		String overlayProfileFilePath = ProfileHelper.getOverlayProfileFilePathForPerso(perso);
		if (overlayProfileFilePath == null) {
			BasicLogger.log(KeyPairObjectHandler.class, "Unable to persist. Cannot get Overlay Profile file path for personalization '" + perso.getClass().getSimpleName() + "'.", LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist");
			return;
		}

		OverlayProfile overlayProfile = ProfileHelper.getOverlayProfile(overlayProfileFilePath);
		if (overlayProfile == null) {
			BasicLogger.log(KeyPairObjectHandler.class, "Unable to persist. Cannot get Overlay Profile for '" + overlayProfileFilePath + "'.", LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist");
			return;
		}
		MasterFile masterFile = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getMasterFile();

		GenericOid oid = null;
		for (CardObjectIdentifier curIdentifier : keyPairObject.getAllIdentifiers()) {
			if (curIdentifier instanceof OidIdentifier identifier) {
				Oid curOid = identifier.getOid();
				byte[] oidBytes = curOid.toByteArray();
				oid = new GenericOid(oidBytes);
			}
		}
		if (oid == null) {
			BasicLogger.log(KeyPairObjectHandler.class, "Unable to persist. Cannot get OID for key pair object.", LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist");
			return;
		}

		KeyPairObject existingKeyPairObject = ProfileHelper.findKeyPairObjectExt(masterFile, new OidIdentifier(oid), keyPairObject.isPrivilegedOnly(),
				keyPairObject.getPrimaryIdentifier().getInteger());
		if (existingKeyPairObject == null) {
			BasicLogger.log(KeyPairObjectHandler.class, "Unable to persist. Cannot get key pair object.", LogLevel.ERROR);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist");
			return;
		}

		try {
			existingKeyPairObject.setKeyPair(keyPairObject.getKeyPair());
		}
		catch (AccessDeniedException e) {
			BasicLogger.logException(KeyPairObjectHandler.class, e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist:\n" + e.getMessage());
			return;
		}

		for (Key current : overlayProfile.getKeys()) {
			if (current.getOidInternal().equals(oid) && current.getPrivilegedOnly().equals(keyPairObject.isPrivilegedOnly())
					&& current.getId().equals(keyPairObject.getPrimaryIdentifier().getInteger())) {
				current.setContent(HexString.encode(keyPairObject.getKeyPair().getPrivate().getEncoded()));
				break;
			}
		}

		boolean prettyPrint = false;
		String prettyPrintCfg = ProfileHelper.getPreferenceStoreAccessorInstance().get(ProfileHelper.OVERLAY_PROFILES_PREF_PRETTY_PRINT);
		if (prettyPrintCfg != null && ("true".equalsIgnoreCase(prettyPrintCfg.trim()) || "yes".equalsIgnoreCase(prettyPrintCfg.trim())))
			prettyPrint = true;
		String jsonSerialized = overlayProfile.serialize(prettyPrint);
		try {
			Files.write(Path.of(overlayProfileFilePath), jsonSerialized.getBytes(StandardCharsets.UTF_8));
		}
		catch (IOException e) {
			BasicLogger.logException(KeyPairObjectHandler.class, e);
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to persist:\n" + e.getMessage());
			return;
		}
		log(ProfileHelper.class, "Overlay Profile file '" + overlayProfileFilePath + "' updated.", LogLevel.DEBUG);

	}

}
