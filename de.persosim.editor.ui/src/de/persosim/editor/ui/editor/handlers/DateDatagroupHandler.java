package de.persosim.editor.ui.editor.handlers;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.IcaoDateChecker;
import de.persosim.editor.ui.editor.checker.LengthChecker;
import de.persosim.simulator.cardobjects.AuxDataObject;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DateAuxObject;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.tlv.TlvDataObject;

public class DateDatagroupHandler extends EidStringDatagroupHandler {
	
	private Oid oid;

	public DateDatagroupHandler(Map<Integer, String> dgMapping, int shortFileID, Oid newOid, String hint) {
		super(dgMapping, shortFileID, new AndChecker(new LengthChecker(8, 8), new IcaoDateChecker(true)), hint);
		this.oid = newOid;
	}
	
	public DateDatagroupHandler(Map<Integer, String> dgMapping, int shortFileID, Oid newOid) {
		this(dgMapping, shortFileID, newOid, null);
	}
	
	@Override
	public void persist(TreeItem item) {
		// TODO Auto-generated method stub
		super.persist(item);
		

		System.out.println("Update AuxData for fileId: "+shortFileId );
		
		ElementaryFile ef = (ElementaryFile) item.getData();
		System.out.println("Update AuxData for file: "+ef );
		
		CardObject mf = ef.getParent();
		while (mf.getParent() != null) {
			mf = mf.getParent();
		}
		
		Collection<CardObject> auxObjects = mf.findChildren(new OidIdentifier(oid));
		System.out.println("Update AuxData: "+auxObjects );
		
		for (Iterator<CardObject> iterator = auxObjects.iterator(); iterator.hasNext();) {
			CardObject cardObject = iterator.next();
			if (!(cardObject instanceof DateAuxObject)) continue;
			DateAuxObject auxObject = (DateAuxObject) cardObject;
			

			TlvDataObject tlv = (TlvDataObject) item.getData(EXTRACTED_TLV);
			try {
				auxObject.setDate(tlv);
			} catch (AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
