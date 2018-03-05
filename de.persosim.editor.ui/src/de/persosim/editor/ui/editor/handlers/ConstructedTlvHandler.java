package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;

public class ConstructedTlvHandler extends AbstractObjectHandler {

	private boolean compress;

	public ConstructedTlvHandler(boolean compress) {
		this.compress = compress;
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ConstructedTlvDataObject) {
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof ConstructedTlvDataObject) {
			TreeItem item = getItem(parentItem);
			handleItem((ConstructedTlvDataObject) object, provider, item);
			return item;
		}
		return null;
	}

	private TreeItem getItem(TreeItem parentItem) {
		if (compress) {
			return parentItem;
		}
		return new TreeItem(parentItem, SWT.NONE);
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof ConstructedTlvDataObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((ConstructedTlvDataObject) object, provider, item);
			return item;
		}
		return null;
	}

	protected void handleItem(ConstructedTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		if (!compress) {
			item.setData(tlv);
			setText(item);
			item.setData(HANDLER, this);
		}
		for (TlvDataObject current : tlv.getTlvDataObjectContainer().getTlvObjects()) {
			ObjectHandler handler = provider.get(current);
			if (handler != null) {
				handler.createItem(item, current, provider);
			}
		}
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof ConstructedTlvDataObject) {
			ConstructedTlvDataObject tlv = (ConstructedTlvDataObject) item.getData();
			
			String newText = HexString.encode(tlv.getTlvTag().toByteArray()) + " "
					+ HexString.encode(tlv.getTlvLength().toByteArray());
			
			ObjectHandler handler = (ObjectHandler) item.getData(HANDLER);
			if (handler != null) {
				newText += getChangedText(item);
			}
			
			item.setText(newText);
		}
	}

	@Override
	protected String getType() {
		return "constructed, not editable";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		// no editor needed
	}

}
