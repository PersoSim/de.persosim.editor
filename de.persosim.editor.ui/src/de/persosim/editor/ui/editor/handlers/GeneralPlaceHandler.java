package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.StringJoiner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.UiHelper;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvTag;

public class GeneralPlaceHandler extends ConstructedTlvHandler {

	public GeneralPlaceHandler(boolean compress) {
		super(compress);
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ConstructedTlvDataObject) {
			ConstructedTlvDataObject generalPlace = (ConstructedTlvDataObject) object;
			if (TlvConstants.TAG_SEQUENCE.equals(generalPlace.getTlvTag()) || TlvConstants.TAG_A1.equals(generalPlace.getTlvTag()) || TlvConstants.TAG_A2.equals(generalPlace.getTlvTag())) {
				return true;
			}

			// XXX possible necessary to check deeper if it is used in a
			// HandlerProvider
		}
		return false;
	}

	@Override
	public void setText(TreeItem item) {
		StringJoiner joiner = new StringJoiner(",");
		extractPrimitiveStrings(joiner, (TlvDataObject) item.getData());
		item.setText(joiner.toString());
	}

	private void extractPrimitiveStrings(StringJoiner joiner, TlvDataObject data) {
		if (data instanceof PrimitiveTlvDataObject) {
			joiner.add(new String(data.getValueField()));
		} else if (data instanceof ConstructedTlvDataObject) {
			for (TlvDataObject current : ((ConstructedTlvDataObject) data).getTlvDataObjectContainer()
					.getTlvObjects()) {
				extractPrimitiveStrings(joiner, current);
			}
		}
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
		ConstructedTlvDataObject tlv = (ConstructedTlvDataObject) item.getData();
		
		if (TlvConstants.TAG_SEQUENCE.equals(tlv.getTlvTag())){
			createField(item, false, composite, tlv, TlvConstants.TAG_AA, TlvConstants.TAG_UTF8_STRING,	StandardCharsets.UTF_8, "Street");
			createField(item, true, composite, tlv, TlvConstants.TAG_AB, TlvConstants.TAG_UTF8_STRING, StandardCharsets.UTF_8, "City");
			createField(item, false, composite, tlv, TlvConstants.TAG_AC, TlvConstants.TAG_UTF8_STRING,	StandardCharsets.UTF_8, "State or region");
			createField(item, true, composite, tlv, TlvConstants.TAG_AD, TlvConstants.TAG_PRINTABLE_STRING,	StandardCharsets.US_ASCII, "Country code");
			createField(item, false, composite, tlv, TlvConstants.TAG_AE, TlvConstants.TAG_PRINTABLE_STRING, StandardCharsets.US_ASCII, "Zipcode");
		} else if (TlvConstants.TAG_A1.equals(tlv.getTlvTag())){
			createPrimitiveField(item, composite, tlv, "Freetext Place");
		} else if (TlvConstants.TAG_A2.equals(tlv.getTlvTag())){
			createPrimitiveField(item, composite, tlv, "NoPlaceInfo");
		}
		

	}

	private void createPrimitiveField(TreeItem item, Composite composite, ConstructedTlvDataObject tlv, String infoText) {
		Label info = new Label(composite, SWT.NONE);
		info.setText(infoText);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		info.setLayoutData(gd);

		Button fieldUsed = new Button(composite, SWT.CHECK);
		fieldUsed.setEnabled(false);
		fieldUsed.setSelection(true);

		Text field = new Text(composite, SWT.NONE);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		field.setText(new String(((PrimitiveTlvDataObject) tlv.getTlvDataObjectContainer().getTlvObjects().get(0)).getValueField(), StandardCharsets.UTF_8));
		
		field.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				((PrimitiveTlvDataObject) tlv.getTlvDataObjectContainer().getTlvObjects().get(0)).setValue(field.getText().getBytes(StandardCharsets.UTF_8));
				
				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null) {
					handler.updateTextRecursively(item);
				}
			}
		});
	}

	private void createField(TreeItem item, boolean mandatory, Composite composite, ConstructedTlvDataObject generalPlaceSequence,
			TlvTag tlvTag, TlvTag typeTag, Charset charset, String infoText) {
		Label info = new Label(composite, SWT.NONE);
		info.setText(infoText);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		info.setLayoutData(gd);

		Button fieldUsed = new Button(composite, SWT.CHECK);
		fieldUsed.setEnabled(!mandatory);

		Text field = new Text(composite, SWT.NONE);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Color defaultColor = field.getBackground();

		if (mandatory || generalPlaceSequence.containsTlvDataObject(tlvTag)) {
			fieldUsed.setSelection(true);
			field.setEnabled(true);
			ConstructedTlvDataObject ctlv = (ConstructedTlvDataObject) generalPlaceSequence.getTlvDataObject(tlvTag);
			field.setText(new String(ctlv.getTlvDataObject(typeTag).getValueField(), charset));
			UiHelper.setColorIfLowerCase(field);
		} else {
			field.setEnabled(false);
		}

		field.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				modifyTlv(generalPlaceSequence, tlvTag, typeTag, charset, field);
				
				if (!UiHelper.setColorIfLowerCase(field)){
					field.setBackground(defaultColor);
				}
				
				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null) {
					handler.updateTextRecursively(item);
				}
			}
		});

		fieldUsed.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				field.setEnabled(fieldUsed.getSelection());
				if (!fieldUsed.getSelection()) {
					if (generalPlaceSequence.containsTlvDataObject(tlvTag)) {
						generalPlaceSequence.removeTlvDataObject(tlvTag);
					}
				} else {
					modifyTlv(generalPlaceSequence, tlvTag, typeTag, charset, field);
				}

				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null) {
					handler.updateTextRecursively(item);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void modifyTlv(ConstructedTlvDataObject generalPlaceSequence, TlvTag tlvTag, TlvTag typeTag,
			Charset charset, Text field) {
		if (generalPlaceSequence.containsTlvDataObject(tlvTag)) {
			generalPlaceSequence.removeTlvDataObject(tlvTag);
		}

		PrimitiveTlvDataObject newContent = new PrimitiveTlvDataObject(typeTag,
				field.getText().getBytes(charset));
		
		generalPlaceSequence.addTlvDataObject(new ConstructedTlvDataObject(tlvTag, newContent));

		generalPlaceSequence.sort(new Comparator<TlvDataObject>() {
			
			@Override
			public int compare(TlvDataObject o1, TlvDataObject o2) {
				return o1.getTagNo() - o2.getTagNo();
			}
		});
	}
}
