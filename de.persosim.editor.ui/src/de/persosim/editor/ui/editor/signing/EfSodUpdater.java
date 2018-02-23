package de.persosim.editor.ui.editor.signing;

import static org.globaltester.logging.BasicLogger.logException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;

import org.globaltester.cryptoprovider.Crypto;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.protocols.NpaProtocol;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.tlv.Asn1PrintableString;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.utils.HexString;
import de.persosim.simulator.utils.Utils;

public class EfSodUpdater extends SignedSecInfoFileUpdater {

	int version = 0;
	String ldsVersion = null;
	String unicodeVersion = null;
	
	public EfSodUpdater(SecInfoCmsBuilder cmsBuilder) {
		super(new DedicatedFileIdentifier(HexString.toByteArray("A0 00 00 02 47 10 01")), new FileIdentifier(0x011D), null, cmsBuilder);
	}
	
	public EfSodUpdater setSecurityObjectVersion(int version) {
		this.version = version;
		return this;
	}
	
	public EfSodUpdater setLdsVersionInfoData(String ldsVersion, String unicodeVersion) {
		this.ldsVersion = ldsVersion;
		this.unicodeVersion = unicodeVersion;
		return this;
	}
	
	@Override
	protected ConstructedTlvDataObject getSecInfos(Personalization perso, SecInfoPublicity secInfoPublicity) {		
		PrimitiveTlvDataObject versionTlv = new PrimitiveTlvDataObject(TlvConstants.TAG_INTEGER, Utils.toShortestUnsignedByteArray(version));
		
		PrimitiveTlvDataObject hashAlgOid = new PrimitiveTlvDataObject(TlvConstants.TAG_06, HexString.toByteArray("60 86 48 01 65 03 04 02 01"));

		ConstructedTlvDataObject dgHashesSequence = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
		
		String hashAlg = "SHA256";

		DedicatedFileIdentifier epassAppIdentifier = new DedicatedFileIdentifier(
				HexString.toByteArray("A0 00 00 02 47 10 01"));
		CommandProcessor cp = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class);
		Collection<CardObject> apps = cp.getMasterFile().findChildren(epassAppIdentifier);
		DedicatedFile epassApp = (DedicatedFile) apps.iterator().next();

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(hashAlg, Crypto.getCryptoProvider());
		
			NpaProtocol.createDgHashes(md, dgHashesSequence, epassApp, Arrays.asList(new Integer [] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16}));
			
			ConstructedTlvDataObject algorithmIdentifier = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE, hashAlgOid, new PrimitiveTlvDataObject(TlvConstants.TAG_NULL));
			ConstructedTlvDataObject content = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE, versionTlv,  algorithmIdentifier, dgHashesSequence);
			
			if (ldsVersion != null){
				ConstructedTlvDataObject ldsVersionInfo = new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
				ldsVersionInfo.addTlvDataObject(Asn1PrintableString.getInstance().encode(ldsVersion));
				ldsVersionInfo.addTlvDataObject(Asn1PrintableString.getInstance().encode(unicodeVersion));
				content.addTlvDataObject(ldsVersionInfo);
			}
			
			return content;
		}catch (NoSuchAlgorithmException e){
			logException(getClass(), e);
		}
		
		return new ConstructedTlvDataObject(TlvConstants.TAG_SEQUENCE);
	}
	
	protected ConstructedTlvDataObject buildSignedDataFile(ConstructedTlvDataObject secInfos) {
		return new ConstructedTlvDataObject(new TlvTag(new byte [] {0x77}), super.buildSignedDataFile(secInfos));
	}
	
}
