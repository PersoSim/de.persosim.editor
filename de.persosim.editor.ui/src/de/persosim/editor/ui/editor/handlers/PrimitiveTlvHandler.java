package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
			composite.setLayout(new GridLayout(2, false));
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();
			
			if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "IA5 string", new UpperCaseTextFieldChecker());
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "PRINTABLE string", new UpperCaseTextFieldChecker());
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "NUMERIC string", new UpperCaseTextFieldChecker());
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "UTF8 string", new UpperCaseTextFieldChecker());
			} else {
				EditorFieldHelper.createField(item, true, composite, new TlvModifier() {
					
					@Override
					public void setValue(String value) {
						tlv.setValue(HexString.toByteArray(value));
					}
					
					@Override
					public void remove() {
					}
					
					@Override
					public String getValue() {
						return HexString.encode(tlv.getValueField());
					}
				}, new HexChecker(), "binary data as hexadecimal string");
			}
			
			composite.pack();
		}
	}
	

	private void createSimpleField(TreeItem item, boolean mandatory, Composite composite, PrimitiveTlvDataObject tlv, Charset charset, String infoText, TextFieldChecker checker) {

			EditorFieldHelper.createField(item, mandatory, composite, new TlvModifier() {

				@Override
				public String getValue() {
					return new String(tlv.getValueField(), charset);
				}

				@Override
				public void setValue(String string) {
					tlv.setValue(string.getBytes(charset));
				}

				@Override
				public void remove() {
				}}, checker, infoText);
		

	}

}
