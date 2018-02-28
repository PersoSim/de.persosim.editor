package de.persosim.editor.ui.editor.handlers;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.HexChecker;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class PrimitiveTlvHandler extends AbstractObjectHandler {

	protected boolean compress;

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
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
			return item;
		}
		return null;
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

			text = HexString.encode(tlv.getValueField());

			if (text.isEmpty()) {
				if (compress) {
					text = "<empty>";
				}
			} else {
				if (text.length() > 32) {
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

			TlvModifier modifier = new HexStringTlvModifier(tlv);
			
			EditorFieldHelper.createBinaryField(item, true, composite, modifier, new HexChecker(), "binary data as hexadecimal string");

			
		}
	}

}
