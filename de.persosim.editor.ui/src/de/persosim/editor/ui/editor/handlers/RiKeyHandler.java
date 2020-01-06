package de.persosim.editor.ui.editor.handlers;


import java.util.Collection;

import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.KeyPairObject;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.protocols.ri.Ri;

public class RiKeyHandler extends KeyPairObjectHandler {

	public RiKeyHandler() {
		super();
	}

	@Override
	public boolean canHandle(Object object) {
		if (!super.canHandle(object)) return false;
		if (!(object instanceof KeyPairObject)) return false;
		
		KeyPairObject kpo = (KeyPairObject) object;
		Collection<CardObjectIdentifier> x = kpo.getAllIdentifiers();
		for (CardObjectIdentifier curIdentifier : x) {
			if ((curIdentifier instanceof OidIdentifier) && ((OidIdentifier)curIdentifier).getOid().startsWithPrefix(Ri.id_RI)) return true;
		}
		
		return false;
	}

	@Override
	protected String getType() {
		return "RI key pair";
	}

}
