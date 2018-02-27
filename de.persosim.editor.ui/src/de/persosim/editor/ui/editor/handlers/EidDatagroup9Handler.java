package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class EidDatagroup9Handler extends DatagroupHandler {
	
	public EidDatagroup9Handler(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			
			return new ShortFileIdentifier(9).matches(ef);
		}
		return false;
	}
	
	@Override
	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
			GeneralPlaceHandler handler = new GeneralPlaceHandler(false);
			handler.createItem(item, tlvObject, provider);
		}
	}
}
