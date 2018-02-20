package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.HandlerProvider;
import de.persosim.editor.ui.editor.ObjectHandler;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.utils.HexString;

public class ConstructedTlvHandler implements ObjectHandler {

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ConstructedTlvDataObject) {
			return true;
		}
		return false;
	}

	@Override
	public void createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof ConstructedTlvDataObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((ConstructedTlvDataObject) object, provider, item);
		}
	}

	@Override
	public void createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof ConstructedTlvDataObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((ConstructedTlvDataObject) object, provider, item);
		}
	}

	private void handleItem(ConstructedTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		item.setData(tlv);
		setText(item, tlv);
		for (TlvDataObject current : tlv.getTlvDataObjectContainer().getTlvObjects()) {
			ObjectHandler handler = provider.get(current);
			if (handler != null) {
				handler.createItem(item, current, provider);
			}
		}
	}

	@Override
	public void createEditor(Composite parent, TreeItem item, HandlerProvider provider) {
		if (item.getData() instanceof ConstructedTlvDataObject) {
			Label typeLabel = new Label(parent, SWT.NONE);
			Text text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			text.setLayoutData(layoutData);
			typeLabel.setText("Type: constructed, not editable");
		}
	}

	@Override
	public void setText(TreeItem item, Object object) {
		if (item.getData() instanceof ConstructedTlvDataObject) {
			ConstructedTlvDataObject tlv = (ConstructedTlvDataObject) object;
			item.setText(HexString.encode(tlv.getTlvTag().toByteArray()) + " "
					+ HexString.encode(tlv.getTlvLength().toByteArray()));
		}
	}

}
