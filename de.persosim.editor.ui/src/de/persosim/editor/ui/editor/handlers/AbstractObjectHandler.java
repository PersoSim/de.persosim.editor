package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.TreeItem;

public abstract class AbstractObjectHandler implements ObjectHandler{

	@Override
	public void updateTextRecursively(TreeItem item) {
		setText(item);
		if (item.getParentItem() != null) {
			((ObjectHandler)item.getParentItem().getData(HANDLER)).updateTextRecursively(item.getParentItem());
		}
	}
}
