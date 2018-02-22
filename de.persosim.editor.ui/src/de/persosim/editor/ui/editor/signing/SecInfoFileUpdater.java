package de.persosim.editor.ui.editor.signing;

import java.util.Collection;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.Protocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.Asn1;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

/**
 * This {@link Modifier} implementation updates a file using
 * 
 * @author mboonk
 *
 */
public class SecInfoFileUpdater {

	private CardObjectIdentifier parentIdentifier;
	private SecInfoPublicity publicity;
	private FileIdentifier fileToChangeIdentifier;

	/**
	 * @param parent
	 *            the parent object to search in or <code>null</code> if the
	 *            root element should be searched
	 * @param fileToChange
	 *            the identifier to find the file to be updated
	 * @param publicity
	 *            the {@link SecInfoPublicity} level to be used
	 */
	public SecInfoFileUpdater(CardObjectIdentifier parent, FileIdentifier fileToChange,
			SecInfoPublicity publicity) {
		this.parentIdentifier = parent;
		this.publicity = publicity;
		this.fileToChangeIdentifier = fileToChange;
	}

	public void execute(Personalization perso) {
		DedicatedFile parent;
		
		MasterFile mf = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getObjectTree();
		
		if (parentIdentifier != null) {
			Collection<CardObject> parentCandidates = mf.findChildren(parentIdentifier);

			if (parentCandidates.size() != 1) {
				throw new IllegalArgumentException("The chosen parent is ambigous");
			}

			parent = (DedicatedFile) parentCandidates.iterator().next();
		} else {
			parent = mf;
		}

		if (!(parent instanceof DedicatedFile)) {
			throw new IllegalArgumentException("Can only search in dedicated files");
		}

		Collection<CardObject> objectsToModify = parent.findChildren(fileToChangeIdentifier);

		if (objectsToModify.size() > 1) {
			throw new IllegalArgumentException("The chosen file for modification is ambigous");
		} else if (objectsToModify.size() == 0){
			throw new IllegalArgumentException("Could not find file to change");
		}

		CardObject object = objectsToModify.iterator().next();

		if (!(object instanceof ElementaryFile)) {
			throw new IllegalArgumentException("Only elementary files can be updated");
		}

		try {
			((ElementaryFile) object).replace(createSecInfoData(perso, publicity).toByteArray());
		} catch (AccessDeniedException e) {
			throw new IllegalArgumentException("Access to the elementary file was denied");
		}
	}

	/**
	 * Creates the security info TLV data that will be written to the modified
	 * file.
	 * 
	 * @param perso
	 *            the {@link Personalization} containing the protocols to be
	 *            used for security info creation
	 * @param publicity
	 *            the {@link SecInfoPublicity} level
	 * @return the security infos to be written
	 * @throws ModifierFailedException 
	 */
	protected ConstructedTlvDataObject createSecInfoData(Personalization perso, SecInfoPublicity publicity) {
		return getSecInfos(perso, publicity);
	}

	/**
	 * Extracts the TLV data from the protocols.
	 * 
	 * @param perso
	 *            the {@link Personalization} containing the protocols to be
	 *            used for security info creation
	 * @param secInfoPublicity
	 *            the {@link SecInfoPublicity} level
	 * @return the security infos as delivered by the protocols
	 */
	protected ConstructedTlvDataObject getSecInfos(Personalization perso, SecInfoPublicity secInfoPublicity) {
		CommandProcessor cp = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class);
		
		// collect SecInfos from protocols
		ConstructedTlvDataObject secInfos = new ConstructedTlvDataObject(new TlvTag(Asn1.SET));
		for (Protocol curProtocol : cp.getProtocolList()) {
			secInfos.addAll(curProtocol.getSecInfos(secInfoPublicity, cp.getObjectTree()));
		}

		return secInfos;
	}

}
