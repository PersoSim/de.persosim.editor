package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.TextFieldChecker;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;

public class EidStringDatagroupHandler extends DatagroupHandler {
	
	private int shortFileId;
	private TextFieldChecker checker;
	private String hint;
	
	public EidStringDatagroupHandler(Map<Integer, String> dgMapping, int shortFileID, TextFieldChecker checker, String hint) {
		super(dgMapping);
		this.shortFileId = shortFileID;
		this.checker = checker;
		this.hint = hint;
	}
	
	public EidStringDatagroupHandler(Map<Integer, String> dgMapping, int shortFileID, TextFieldChecker checker) {
		this(dgMapping, shortFileID, checker, null);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			
			return new ShortFileIdentifier(shortFileId).matches(ef);
		}
		return false;
	}
	
	@Override
	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
			StringTlvHandler handler = new StringTlvHandler(true, checker, hint);
			handler.createItem(item, tlvObject, provider);
		}
	}
}
