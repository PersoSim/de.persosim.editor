package de.persosim.editor.ui.editor.handlers;


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

import de.persosim.simulator.cardobjects.KeyIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.crypto.CryptoUtil;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.utils.HexString;

public class KeyPairObjectHandler extends AbstractObjectHandler {

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof KeyPairObject) {
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof KeyPairObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((KeyPairObject) object, provider, item);
			return item;
		}
		return null;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof KeyPairObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((KeyPairObject) object, provider, item);
			return item;
		}
		return null;
	}

	private void handleItem(KeyPairObject kpo, HandlerProvider provider, TreeItem item) {
		item.setData(kpo);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof KeyPairObject) {
			KeyPairObject keyPairObject = (KeyPairObject) item.getData();

			String text = getType();
			
			KeyIdentifier keyIdentifier = keyPairObject.getPrimaryIdentifier();
			if (keyIdentifier != null) {
				text += " (keyId "+keyIdentifier.getKeyReference()+")";
			}
			
			ObjectHandler handler = (ObjectHandler) item.getData(HANDLER);
			if (handler != null) {
				text += getChangedText(item);
			}
			
			item.setText(text);
		}
	}

	@Override
	protected String getType() {
		return "Key pair";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
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
				public void widgetSelected(SelectionEvent e) {
					generateNewKeyMaterial((KeyPairObject) item.getData());
					
					showValues(item, pubKeyText, privKeyText);
					
				}
			});
			
			composite.pack();
			
		}
	}

	private void showValues(TreeItem item, Text pubKeyText, Text privKeyText) {
		KeyPairObject keyPairObject = (KeyPairObject) item.getData();
		
		ECPublicKey pubKey = (ECPublicKey)keyPairObject.getKeyPair().getPublic();
		int referenceLength = CryptoUtil.getPublicPointReferenceLengthL(((ECFieldFp) pubKey.getParams().getCurve().getField()).getP());
		byte[] pubKeyBytes = CryptoUtil.encode(pubKey.getW(), referenceLength, CryptoUtil.ENCODING_UNCOMPRESSED);
		
		ECPrivateKey privKey = (ECPrivateKey)keyPairObject.getKeyPair().getPrivate();
		byte[] privKeyBytes = privKey.getS().toByteArray();
		
		pubKeyText.setText(HexString.dump(pubKeyBytes));
		privKeyText.setText(HexString.dump(privKeyBytes));
	}

	private void generateNewKeyMaterial(KeyPairObject keyPairObject) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("ECDH");
		    AlgorithmParameterSpec params = ((ECPublicKey)keyPairObject.getKeyPair().getPublic()).getParams();
		    kpg.initialize(params);
		    KeyPair newKeyPair = kpg.generateKeyPair();
		
			keyPairObject.setKeyPair(newKeyPair);
		} catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to generate new key material:\n"+e.getMessage());
		} catch (AccessDeniedException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "Unable to store new key material in KeyPairObject:\n"+e.getMessage());
		}
		
	}


}
