package de.persosim.editor.ui.editor;

import java.util.Collection;
import java.util.LinkedList;

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

	private static final String DG_1 = "DG 1";
	private static final String DG_2 = "DG 2";
	private static final String DG_3 = "DG 3";
	private static final String DG_4 = "DG 4";
	private static final String DG_5 = "DG 5";
	private static final String DG_6 = "DG 6";
	private static final String DG_7 = "DG 7";
	private static final String DG_8 = "DG 8";
	private static final String DG_9 = "DG 9";
	private static final String DG_10 = "DG 10";
	private static final String DG_11 = "DG 11";
	private static final String DG_12 = "DG 12";
	private static final String DG_13 = "DG 13";
	private static final String DG_14 = "DG 14";
	private static final String DG_15 = "DG 15";
	private static final String DG_16 = "DG 16";
	private static final String DG_17 = "DG 17";
	private static final String DG_18 = "DG 18";
	private static final String DG_19 = "DG 19";
	private static final String DG_20 = "DG 20";
	private static final String DG_21 = "DG 21";
	private static final String DG_22 = "DG 22";
	
	private LinkedList<String> names;

	public EidDatagroupTemplateProvider() {
		names = new LinkedList<String>();
		for (int i = 1; i <= 14; i++){
			names.add("DG " + i);
		}
		for (int i = 17; i <= 22; i++){
			names.add("DG " + i);
		}
	}
	
	@Override
	public Collection<String> supportedDgNames() {
		return new LinkedList<String>(names);
	}

	@Override
	public ElementaryFile getDgForName(String name) {
		
		switch (name){
		case DG_1:
			return getDg((byte)1, HexString.toByteArray("610413025450"));
		case DG_2:
			return getDg((byte)2, HexString.toByteArray("6203130144"));
		case DG_3:
			return getDg((byte)3, HexString.toByteArray("630A12083230323031303331"));
		case DG_4:
			return getDg((byte)4, HexString.toByteArray("64070C054572696B61"));
		case DG_5:
			return getDg((byte)5, HexString.toByteArray("650C0C0A4D75737465726D616E6E"));
		case DG_6:
			return getDg((byte)6, HexString.toByteArray("66110C0F4C61647920436F6E666F726D697479"));
		case DG_7:
			return getDg((byte)7, HexString.toByteArray("67020C00"));
		case DG_8:
			return getDg((byte)8, HexString.toByteArray("680A12083139363430383132"));
		case DG_9:
			return getDg((byte)9, HexString.toByteArray("691B3019AB080C064265726C696EAC080C064265726C696EAD03130144"));
		case DG_10:
			return getDg((byte)10, HexString.toByteArray("6A03130144"));
		case DG_11:
			return getDg((byte)11, HexString.toByteArray("6B03130146"));
		case DG_12:
			return getDg((byte)12, HexString.toByteArray("6C5E315C302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377302C06072A8648CE3D0101022100A9FB57DBA1EEA9BC3E660A909D838D726E3BF623D52620282013481D1F6E5377"));
		case DG_13:
			return getDg((byte)13, HexString.toByteArray("6D090C074D75656C6C6572"));
		case DG_14:
			return getDg((byte)14, HexString.toByteArray("6E020400"));
		case DG_15:
			//return getDg((byte)15, HexString.toByteArray(""));
			return null;
		case DG_16:
			//return getDg((byte)16, HexString.toByteArray(""));
			return null;
		case DG_17:
			return getDgWritable((byte)17, HexString.toByteArray("712E302CAA110C0F486569646573747261737365203137AB080C064265726C696EAC080C064265726C696EAD03130144"));
		case DG_18:
			return getDgWritable((byte)18, HexString.toByteArray("7209040702761100000000"));
		case DG_19:
			return getDgWritable((byte)19, HexString.toByteArray("730EA10C0C0A5265735065726D697431"));
		case DG_20:
			return getDgWritable((byte)20, HexString.toByteArray("740EA10C0C0A5265735065726D697432"));
		case DG_21:
			return getDgWritable((byte)21, HexString.toByteArray("76021300"));
		case DG_22:
			return getDgWritable((byte)22, HexString.toByteArray("77021600"));
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
