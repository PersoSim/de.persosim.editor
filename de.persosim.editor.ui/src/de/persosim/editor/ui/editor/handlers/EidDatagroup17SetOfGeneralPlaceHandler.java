package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.EidDataTemplateProvider;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;

public class EidDatagroup17SetOfGeneralPlaceHandler extends EidDatagroup17HandlerSingularGeneralPlace {
	
	private EidDataTemplateProvider templateProvider;

	public EidDatagroup17SetOfGeneralPlaceHandler(Map<Integer, String> dgMapping, EidDataTemplateProvider templateProvider) {
		super(dgMapping);
		this.templateProvider = templateProvider;
	}
	
	@Override
	protected boolean checkGeneralPlace(TlvDataObject tlvObject) {
		tlvObject = ((ConstructedTlvDataObject) tlvObject).getTlvDataObjectContainer().getTlvObjects().get(0);
		if (tlvObject instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
			if (TlvConstants.TAG_SET.equals(ctlv.getTlvTag())){
				return true;
			} 
		}
		return false;
	}
	
	@Override
	protected void handleGeneralPlace(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		GeneralPlaceHandler handler = new GeneralPlaceHandler(false);
		ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) tlvObject;
		if (TlvConstants.TAG_SET.equals(ctlv.getTlvTag())){
			// multiple GeneralPlace contained
			for (TlvDataObject current : ctlv.getTlvDataObjectContainer().getTlvObjects()){
				handler.createItem(item, current, provider);
			}
		}
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		super.createMenu(menu, item);
		MenuItem mitem = new MenuItem(menu, SWT.NONE);
		mitem.setText("Add Place");
		mitem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				TlvDataObject tlvObject = (TlvDataObject) item.getData(DatagroupHandler.EXTRACTED_TLV);
				if (tlvObject instanceof ConstructedTlvDataObject){
					ConstructedTlvDataObject ctlv = ((ConstructedTlvDataObject) tlvObject);
					ctlv.addTlvDataObject(templateProvider.getDefaultPlace());
				}
			}
		});
	}
}
