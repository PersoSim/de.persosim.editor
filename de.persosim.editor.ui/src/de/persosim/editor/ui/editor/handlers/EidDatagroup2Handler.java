package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.AndChecker;
import de.persosim.editor.ui.editor.checker.OrChecker;
import de.persosim.editor.ui.editor.checker.UpperCaseTextFieldChecker;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class EidDatagroup2Handler extends DatagroupHandler {
	
	public EidDatagroup2Handler(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			
			return new ShortFileIdentifier(2).matches(ef);
		}
		return false;
	}
	
	@Override
	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
			StringTlvHandler handler = new StringTlvHandler(false, new AndChecker(new OrChecker(new LengthChecker(1,1), new LengthChecker(3,3)), new UpperCaseTextFieldChecker()));
			handler.createItem(item, tlvObject, provider);
		}
	}
}
