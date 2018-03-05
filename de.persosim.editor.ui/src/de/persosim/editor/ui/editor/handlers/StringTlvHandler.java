package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.TextFieldChecker;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.utils.HexString;

public class StringTlvHandler extends PrimitiveTlvHandler {

	private TextFieldChecker checker;

	public StringTlvHandler(boolean compress, TextFieldChecker checker) {
		super(compress);
		this.checker = checker;
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof PrimitiveTlvDataObject) {
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) object;

			if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)
					|| tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
			return item;
		}
		return null;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
			return item;
		}
		return null;
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
			
			text += getStringFromTlv(tlv);

			if (text.isEmpty()) {
				if (compress) {
					text = "<empty>";
				}
			} else {
				if (text.length() > 32) {
					text = text.substring(0, 31) + "...";
				}
			}
			
			ObjectHandler handler = (ObjectHandler) item.getData(HANDLER);
			if (handler != null) {
				text += getChangedText(item);
			}
			
			item.setText(text);
		}
	}

	public static String getStringFromTlv(PrimitiveTlvDataObject tlv) {
		if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
				|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
				|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
			return new String(tlv.getValueField(), StandardCharsets.US_ASCII);
		} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
			return new String(tlv.getValueField(), StandardCharsets.UTF_8);
		}
		return new String(tlv.getValueField());
	}

	@Override
	protected String getType() {
		return "primitive string value field, editable";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		if (item.getData() instanceof PrimitiveTlvDataObject) {
			composite.setLayout(new GridLayout(2, false));
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();

			if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "IA5 string");
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "PRINTABLE string");
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.US_ASCII, "NUMERIC string");
			} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				createSimpleField(item, true, composite, tlv, StandardCharsets.UTF_8, "UTF8 string");
			}
			composite.pack();
		}
	}

	private void createSimpleField(TreeItem item, boolean mandatory, Composite composite, PrimitiveTlvDataObject tlv,
			Charset charset, String infoText) {

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
			}
		}, checker, infoText);

	}

}
