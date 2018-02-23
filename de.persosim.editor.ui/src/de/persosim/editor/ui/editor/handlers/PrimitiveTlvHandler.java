package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;

public class PrimitiveTlvHandler extends AbstractObjectHandler {

	private boolean compress;

	public PrimitiveTlvHandler(boolean compress) {
		this.compress = compress;
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof PrimitiveTlvDataObject) {
			return true;
		}
		return false;
	}

	@Override
	public void createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
		}
	}

	@Override
	public void createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
		}
	}

	private void handleItem(PrimitiveTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		item.setData(tlv);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof PrimitiveTlvDataObject) {
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();

			String text = "";
			
			if (!compress) {
				text = HexString.encode(tlv.getTlvTag().toByteArray()) + " "
						+ HexString.encode(tlv.getTlvLength().toByteArray()) + " ";
			}
			

			if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				text = new String(tlv.getValueField(), StandardCharsets.US_ASCII);
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				text = new String(tlv.getValueField(), StandardCharsets.UTF_8);
			} else {
				text = HexString.encode(tlv.getValueField());
			}
			

			if (text.isEmpty()){
				if (compress){
					text = "<empty>";	
				}	
			} else {
				if (text.length() > 32){
					text = text.substring(0, 31);
				}
				
			}
			item.setText(text);
		}
	}

	@Override
	protected String getType() {
		return "primitive value field, editable";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		if (item.getData() instanceof PrimitiveTlvDataObject) {
			composite.setLayout(new GridLayout(1, false));
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();
			Label typeLabel = new Label(composite, SWT.NONE);
			Text text = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			text.setLayoutData(layoutData);

			if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				typeLabel.setText("Data: IA5,PRINTABLE or NUMERIC string");
				text.setText(new String(tlv.getValueField(), StandardCharsets.US_ASCII));
				text.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						tlv.setValue(text.getText().getBytes(StandardCharsets.US_ASCII));
						updateTextRecursively(item);
					}
				});
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				typeLabel.setText("Data: UTF8 string");
				text.setText(new String(tlv.getValueField(), StandardCharsets.UTF_8));
				text.addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(ModifyEvent e) {
						tlv.setValue(text.getText().getBytes(StandardCharsets.UTF_8));
						updateTextRecursively(item);
					}
				});
			} else {
				typeLabel.setText("Data: binary data as hexadecimal string");
				text.setText(HexString.encode(tlv.getValueField()));
				text.addModifyListener(new ModifyListener() {
					Color defaultColor = text.getBackground();

					@Override
					public void modifyText(ModifyEvent e) {
						try {
							tlv.setValue(HexString.toByteArray(text.getText()));
							text.setBackground(defaultColor);
						} catch (Exception ex) {
							text.getBackground();
							text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						}
						updateTextRecursively(item);
					}
				});
			}
			composite.pack();
		}
	}

}
