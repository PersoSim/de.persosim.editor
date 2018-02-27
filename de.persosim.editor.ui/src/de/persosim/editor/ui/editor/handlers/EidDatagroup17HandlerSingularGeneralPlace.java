package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;

public class EidDatagroup17HandlerSingularGeneralPlace extends DatagroupHandler {
	
	public EidDatagroup17HandlerSingularGeneralPlace(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			if (new ShortFileIdentifier(17).matches(ef)){
				TlvDataObject tlvObject;
				try {
					tlvObject = TlvDataObjectFactory.createTLVDataObject(ef.getContent());
					if (tlvObject instanceof ConstructedTlvDataObject){
						tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
						return checkGeneralPlace(tlvObject);
					}
				} catch (AccessDeniedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	protected boolean checkGeneralPlace(TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
			if (TlvConstants.TAG_SEQUENCE.equals(ctlv.getTlvTag())){
				return true;
			} 
		}
		return false;
	}
	
	@Override
	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
			handleGeneralPlace(provider, item, tlvObject);
		}
	}

	protected void handleGeneralPlace(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
			GeneralPlaceHandler handler = new GeneralPlaceHandler(false);
			if (TlvConstants.TAG_SEQUENCE.equals(ctlv.getTlvTag())){
				handler.createItem(item, ctlv, provider);
			}
		}
	}
}
