package de.persosim.editor.ui.editor.signing;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;

/**
 * This updates an elementary file with a signed
 * data object containing security infos.
 * 
 * @author mboonk
 *
 */
public class SignedSecInfoFileUpdater extends SecInfoFileUpdater {

	private SecInfoCmsBuilder cmsBuilder;

	/**
	 * @param parent
	 *            the parent object to search the file in
	 * @param fileToChange
	 *            the identifier to find the file to be updated
	 * @param publicity
	 *            the {@link SecInfoPublicity} level to be used
	 * @param cmsBuilder
	 *            the builder for creating the signature data
	 */
	public SignedSecInfoFileUpdater(CardObjectIdentifier parent, FileIdentifier fileToChangeIdentifier,
			SecInfoPublicity publicity, SecInfoCmsBuilder cmsBuilder) {
		super(parent, fileToChangeIdentifier, publicity);
		this.cmsBuilder = cmsBuilder;
	}
	
	protected ConstructedTlvDataObject createSecInfoData(Personalization perso, SecInfoPublicity publicity) {
		return buildSignedDataFile(super.createSecInfoData(perso, publicity));
	}

	/**
	 * Create the signed data file TLV structure.
	 * 
	 * @param secInfos
	 *            the secinfos to encapsulate
	 * @return the signed data file content
	 * @throws ModifierFailedException 
	 */
	protected ConstructedTlvDataObject buildSignedDataFile(ConstructedTlvDataObject secInfos) {

		
		
		
		TlvDataObject oidTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_OID,
				HexString.toByteArray("2A 86  48 86 F7 0D 01 07 02"));

		ConstructedTlvDataObject cmsContainer = new ConstructedTlvDataObject(TlvConstants.TAG_A0);
		try {
			cmsContainer.addTlvDataObject(cmsBuilder.buildSignedData(secInfos));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException e) {
			throw new IllegalArgumentException(e.getMessage());
		}

		ConstructedTlvDataObject signedDataFile = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		signedDataFile.addTlvDataObject(oidTlv);
		signedDataFile.addTlvDataObject(cmsContainer);

		return signedDataFile;
	}
}
