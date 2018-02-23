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

public class EidDatagroup17Handler extends DatagroupHandler implements ObjectHandler {
	
	public EidDatagroup17Handler(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			
			return new ShortFileIdentifier(17).matches(ef);
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
				if (tlvObject instanceof ConstructedTlvDataObject){
					ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
					GeneralPlaceHandler handler = new GeneralPlaceHandler(false);
					if (TlvConstants.TAG_SET.equals(ctlv.getTlvTag())){
						// multiple GeneralPlace contained
						for (TlvDataObject current : ctlv.getTlvDataObjectContainer().getTlvObjects()){
							handler.createItem(item, current, provider);
						}
					} else if (TlvConstants.TAG_SEQUENCE.equals(ctlv.getTlvTag())){
						// singular GeneralPlace contained
						handler.createItem(item, ctlv, provider);
					}
					//XXX: check and handle sets of GeneralPlace as well as application tag UTF8 freetextPlace and noPlaceInfo 
				}
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
