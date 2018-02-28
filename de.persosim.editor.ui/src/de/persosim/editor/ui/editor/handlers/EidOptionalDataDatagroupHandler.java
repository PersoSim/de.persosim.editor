package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.EidDataTemplateProvider;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;

public class EidOptionalDataDatagroupHandler extends DatagroupHandler {
	
	private int dgNumber;
	private EidDataTemplateProvider templateProvider;

	public EidOptionalDataDatagroupHandler(Map<Integer, String> dgMapping, int dgNumber, EidDataTemplateProvider templateProvider) {
		super(dgMapping);
		this.dgNumber = dgNumber;
		this.templateProvider = templateProvider;
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			if (new ShortFileIdentifier(dgNumber).matches(ef)){
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		if (tlvObject instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
			if (ctlv.containsTlvDataObject(TlvConstants.TAG_SET)){
				OptionalDataHandler handler = new OptionalDataHandler();
				for (TlvDataObject current : (ConstructedTlvDataObject)(ctlv.getTlvDataObject(TlvConstants.TAG_SET))){
					handler.createItem(item, current, provider);
				}
			}
		}
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		super.createMenu(menu, item);
		MenuItem mitem = new MenuItem(menu, SWT.NONE);
		mitem.setText("Add new optional data");
		mitem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				ObjectHandler handler = new OptionalDataHandler();
				ConstructedTlvDataObject defaultOptionalData = templateProvider.getDefaultOptionalData();
				ConstructedTlvDataObject tlv = (ConstructedTlvDataObject) item.getData(EXTRACTED_TLV);
				tlv.addTlvDataObject(defaultOptionalData);
				TreeItem newItem = handler.createItem(item, defaultOptionalData, null);
				if (newItem != null){
					handler.updateTextRecursively(newItem);	
				}
				((ObjectHandler) item.getData(HANDLER)).persist(item);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public void removeItem(TreeItem item) {
		ConstructedTlvDataObject dgContent = (ConstructedTlvDataObject) item.getParentItem().getData(EXTRACTED_TLV);
		ConstructedTlvDataObject set = (ConstructedTlvDataObject) dgContent.getTlvDataObject(TlvConstants.TAG_SET);
		set.remove((TlvDataObject) item.getData());
		((ObjectHandler) item.getParentItem().getData(HANDLER)).updateTextRecursively(item.getParentItem());
		((ObjectHandler) item.getParentItem().getData(HANDLER)).persist(item.getParentItem());
		item.dispose();
	}
}
