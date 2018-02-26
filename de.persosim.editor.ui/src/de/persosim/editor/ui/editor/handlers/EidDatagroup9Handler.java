package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;

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
	protected void handleItem(ElementaryFile ef, HandlerProvider provider, TreeItem item) {
		item.setData(ef);
		setText(item);
		item.setData(HANDLER, this);
		try {
			TlvDataObject tlvObject = TlvDataObjectFactory.createTLVDataObject(ef.getContent());
			if (tlvObject instanceof ConstructedTlvDataObject){
				tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
				GeneralPlaceHandler handler = new GeneralPlaceHandler(false);
				handler.createItem(item, tlvObject, provider);
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
