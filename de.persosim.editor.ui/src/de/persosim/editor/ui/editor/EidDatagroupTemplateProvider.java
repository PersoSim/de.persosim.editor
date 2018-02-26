package de.persosim.editor.ui.editor;

import java.util.Collection;
import java.util.LinkedList;
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
import de.persosim.simulator.utils.BitField;
import de.persosim.simulator.utils.HexString;

public class EidDatagroupTemplateProvider implements DataGroupTemplateProvider {
	
	private LinkedList<Integer> numbers;

	public EidDatagroupTemplateProvider(Set<Integer> dgNumbersToFilter) {
		numbers = new LinkedList<Integer>();
		for (int i = 1; i <= 14; i++){
			numbers.add(i);
		}
		for (int i = 17; i <= 22; i++){
			numbers.add(i);
		}

		numbers.removeAll(dgNumbersToFilter);
	}
	
	@Override
	public Collection<Integer> supportedDgNumbers() {
		return new LinkedList<Integer>(numbers);
	}

	@Override
	public ElementaryFile getDgForNumber(int number) {
		if (!numbers.contains(number)){
			return null;
		}
		
		switch (number){
		case 1:
			return getDg((byte)number, HexString.toByteArray("610413025450"));
		case 2:
			return getDg((byte)number, HexString.toByteArray("6203130144"));
		case 3:
			return getDg((byte)number, HexString.toByteArray("630A12083230323031303331"));
		case 4:
			return getDg((byte)number, HexString.toByteArray("64070C054572696B61"));
		case 5:
			return getDg((byte)number, HexString.toByteArray("650C0C0A4D75737465726D616E6E"));
		case 6:
			return getDg((byte)number, HexString.toByteArray("66110C0F4C61647920436F6E666F726D697479"));
		case 7:
			return getDg((byte)number, HexString.toByteArray("67020C00"));
		case 8:
			return getDg((byte)number, HexString.toByteArray("680A12083139363430383132"));
		case 9:
			return getDg((byte)number, HexString.toByteArray("691B3019AB080C064265726C696EAC080C064265726C696EAD03130144"));
		case 10:
			return getDg((byte)number, HexString.toByteArray("6A03130144"));
		case 11:
			return getDg((byte)number, HexString.toByteArray("6B03130146"));
		case 12:
			return getDg((byte)number, HexString.toByteArray("6C5E315C302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377"));
		case 13:
			return getDg((byte)number, HexString.toByteArray("6D090C074D75656C6C6572"));
		case 14:
			return getDg((byte)number, HexString.toByteArray("6E020400"));
		case 15:
			//return getDg((byte)number, HexString.toByteArray(""));
			return null;
		case 16:
			//return getDg((byte)number, HexString.toByteArray(""));
			return null;
		case 17:
			return getDgWritable((byte)number, HexString.toByteArray("712E302CAA110C0F486569646573747261737365203137AB080C064265726C696EAC080C064265726C696EAD03130144"));
		case 18:
			return getDgWritable((byte)number, HexString.toByteArray("7209040702761100000000"));
		case 19:
			return getDgWritable((byte)number, HexString.toByteArray("730EA10C0C0A5265735065726D697431"));
		case 20:
			return getDgWritable((byte)number, HexString.toByteArray("740EA10C0C0A5265735065726D697432"));
		case 21:
			return getDgWritable((byte)number, HexString.toByteArray("76021300"));
		case 22:
			return getDgWritable((byte)number, HexString.toByteArray("77021600"));
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

}
