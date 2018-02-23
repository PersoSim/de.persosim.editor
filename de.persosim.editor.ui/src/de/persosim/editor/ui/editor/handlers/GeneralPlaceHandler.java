package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvTag;

public class GeneralPlaceHandler extends ConstructedTlvHandler {

	public GeneralPlaceHandler(boolean compress) {
		super(compress);
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ConstructedTlvDataObject){
			ConstructedTlvDataObject generalPlace = (ConstructedTlvDataObject)object;
			if (TlvConstants.TAG_SEQUENCE.equals(generalPlace.getTlvTag())){
				//XXX possible necessary to check deeper if it is used in a HandlerProvider
				return true;
			}
		}
		return false;
	}

	@Override
	public void setText(TreeItem item) {
		item.setText("GeneralPlace");
	}

	@Override
	protected String getType() {
		return "GeneralPlace";
	}
	
	@Override
	protected void handleItem(ConstructedTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		item.setData(tlv);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		ConstructedTlvDataObject set = (ConstructedTlvDataObject) item.getData();
		
		
		createField(false, composite, set, TlvConstants.TAG_AA, TlvConstants.TAG_UTF8_STRING, StandardCharsets.UTF_8, "Street");
		createField(true, composite, set, TlvConstants.TAG_AB, TlvConstants.TAG_UTF8_STRING, StandardCharsets.UTF_8, "City");
		createField(false, composite, set, TlvConstants.TAG_AC, TlvConstants.TAG_UTF8_STRING, StandardCharsets.UTF_8, "State or region");
		createField(true, composite, set, TlvConstants.TAG_AD, TlvConstants.TAG_PRINTABLE_STRING, StandardCharsets.US_ASCII, "Country code");
		createField(false, composite, set, TlvConstants.TAG_AE, TlvConstants.TAG_PRINTABLE_STRING, StandardCharsets.US_ASCII, "Zipcode");
	}


	private void createField(boolean mandatory, Composite composite, ConstructedTlvDataObject set, TlvTag tlvTag, TlvTag typeTag, Charset charset, String infoText) {
		Label info = new Label(composite, SWT.NONE);
		info.setText(infoText);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		info.setLayoutData(gd);
		
		Button fieldUsed = new Button(composite, SWT.CHECK);
		fieldUsed.setEnabled(!mandatory);
		
		Text field = new Text(composite, SWT.NONE);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (mandatory | set.containsTlvDataObject(tlvTag)){
			fieldUsed.setSelection(true);
			field.setEnabled(true);
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) set.getTlvDataObject(tlvTag);
			field.setText(new String(ctlv.getTlvDataObject(typeTag).getValueField(), charset));
		} else {
			field.setEnabled(false);
		}
		
		field.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				if (set.containsTlvDataObject(tlvTag)){
					set.removeTlvDataObject(tlvTag);
				}
				
				PrimitiveTlvDataObject newContent = new PrimitiveTlvDataObject(typeTag, field.getText().getBytes(charset));
				set.addTlvDataObject(new ConstructedTlvDataObject(tlvTag, newContent));
			}
		});
		
		fieldUsed.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				field.setEnabled(fieldUsed.getSelection());
				if (!fieldUsed.getSelection()){
					if (set.containsTlvDataObject(tlvTag)){
						set.removeTlvDataObject(tlvTag);
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {				
			}
		});
		
	}
}
