package de.persosim.editor.ui.editor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.FileIdentifier;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.protocols.ta.CertificateRole;
import de.persosim.simulator.protocols.ta.RelativeAuthorization;
import de.persosim.simulator.protocols.ta.TerminalType;
import de.persosim.simulator.seccondition.OrSecCondition;
import de.persosim.simulator.seccondition.SecCondition;
import de.persosim.simulator.seccondition.TaSecurityCondition;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

public class EidDataTemplateProvider implements DataGroupTemplateProvider {
	
	private Map<Integer, Set<String>> dgVariants;

	public EidDataTemplateProvider(Set<Integer> dgNumbersToFilter) {
		dgVariants = new HashMap<Integer, Set<String>>();
		
		for (int i = 1; i <= 14; i++){
			dgVariants.put(i, new HashSet<>());
			dgVariants.get(i).add("Default");
		}
		for (int i = 17; i <= 22; i++){
			dgVariants.put(i, new HashSet<>());
			dgVariants.get(i).add("Default");
		}

		for (Integer current : dgNumbersToFilter){
			dgVariants.remove(current);
		}
	}
	
	@Override
	public Collection<Integer> supportedDgNumbers() {
		return new LinkedList<Integer>(dgVariants.keySet());
	}
	
	@Override
	public Collection<String> getVariants(int number){
		if (dgVariants.containsKey(number)){
			return new LinkedList<>(dgVariants.get(number));	
		}
		return Collections.emptyList();
	}
	
	@Override
	public ElementaryFile getDgForNumber(int number, String variant) {
		if (!dgVariants.keySet().contains(number)){
			return null;
		}
		
		switch(number){
		case 1:
			return getDg((byte)number,HexString.toByteArray("610413024944"));
		case 2:
			return getDg((byte)number,HexString.toByteArray("6203130144"));
		case 3:
			return getDg((byte)number,HexString.toByteArray("630A12083230323031303331"));
		case 4:
			return getDg((byte)number,HexString.toByteArray("64070C054552494B41"));
		case 5:
			return getDg((byte)number,HexString.toByteArray("650C0C0A4D55535445524D414E4E"));
		case 6:
			return getDg((byte)number,HexString.toByteArray("66110C0F4C41445920434F4E464F524D495459"));
		case 7:
			return getDg((byte)number,HexString.toByteArray("67050C0344522E"));
		case 8:
			return getDg((byte)number,HexString.toByteArray("680A12083139363430383132"));
		case 9:
			return getDg((byte)number,HexString.toByteArray("691B3019AB080C064245524C494EAC080C064245524C494EAD03130144"));
		case 10:
			return getDg((byte)number,HexString.toByteArray("6A03130144"));
		case 11:
			return getDg((byte)number,HexString.toByteArray("6B03130146"));
		case 12:
			return getDg((byte)number,HexString.toByteArray("6C30312E302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377"));
		case 13:
			return getDg((byte)number,HexString.toByteArray("6D090C074D55454C4C4552"));
		case 14:
			return getDg((byte)number,HexString.toByteArray("6E020400"));
		case 15:
			return null;
		case 16:
			return null;
		case 17:
			return getDgWritable((byte)number,HexString.toByteArray("712E302CAA110C0F484549444553545241C39F45203137AB080C064245524C494EAC080C064245524C494EAD03130144"));
		case 18:
			return getDgWritable((byte)number,HexString.toByteArray("7209040702761100000000"));
		case 19:
			return getDgWritable((byte)number,HexString.toByteArray("7316A1140C125245534944454E4345205045524D49542031"));
		case 20:
			return getDgWritable((byte)number,HexString.toByteArray("7416A1140C125245534944454E4345205045524D49542031"));
		case 21:
			return getDgWritable((byte)number,HexString.toByteArray("7515131374656C3A2B34392D3033302D31323334353637"));
		case 22:
			return getDgWritable((byte)number,HexString.toByteArray("761516136572696B61406D75737465726D616E6E2E6465"));
		}
		return null;
	}
	
	private ElementaryFile getDgWritable(byte number, byte[] content) {
		return new ElementaryFile(new FileIdentifier(0x0100 + number),
				new ShortFileIdentifier(number),
				content,
				getAccessRightReadEidDg(number),
				getAccessRightUpdateEidDg(number),
				SecCondition.DENIED);
	}
	
	private ElementaryFile getDg(byte number, byte[] content) {
		return new ElementaryFile(new FileIdentifier(0x0100 + number),
				new ShortFileIdentifier(number),
				content,
				getAccessRightReadEidDg(number),
				SecCondition.DENIED,
				SecCondition.DENIED);
	}

	private SecCondition getAccessRightUpdateEidDg(int dgNr) {
		return new TaSecurityCondition(TerminalType.AT,
				new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(54 - dgNr)));
	}

	private SecCondition getAccessRightReadEidDg(int dgNr) {
		return new OrSecCondition(new TaSecurityCondition(TerminalType.IS, null),
				new TaSecurityCondition(TerminalType.AT, 
						new RelativeAuthorization(CertificateRole.TERMINAL, new BitField(38).flipBit(dgNr + 7))));
	}

	public ConstructedTlvDataObject getDefaultOptionalData() {
		return (ConstructedTlvDataObject) TlvDataObjectFactory.createTLVDataObject("302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377");
	}
	
	public ConstructedTlvDataObject getDefaultPlace(){
		return (ConstructedTlvDataObject) TlvDataObjectFactory.createTLVDataObject("302CAA110C0F486569646573747261737365203137AB080C064265726C696EAC080C064265726C696EAD03130144");
	}

}
