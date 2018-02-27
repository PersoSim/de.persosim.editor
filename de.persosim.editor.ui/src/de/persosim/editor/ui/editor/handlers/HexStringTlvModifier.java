package de.persosim.editor.ui.editor.handlers;

import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class HexStringTlvModifier implements TlvModifier {

	private PrimitiveTlvDataObject tlv;

	public HexStringTlvModifier(PrimitiveTlvDataObject tlv) {
		this.tlv = tlv;
	}

	@Override
	public void setValue(String value) {
		try{
			tlv.setValue(HexString.toByteArray(value));
		} catch (NumberFormatException e){
			// just ignore this, the mechanism for input check will communicate this to the user
		}
	}

	@Override
	public void remove() {
	}

	@Override
	public String getValue() {
		return HexString.encode(tlv.getValueField());
	}

}
