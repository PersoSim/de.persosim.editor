package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.DefaultMenuListener;
import de.persosim.simulator.cardobjects.DedicatedFile;

public class DedicatedFileHandler extends AbstractObjectHandler {

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof DedicatedFile){
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		return null;
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		// do nothing
		return null;
	}

	@Override
	public void setText(TreeItem item) {
		// do nothing
	}

	@Override
	protected String getType() {
		return "Dedicated File";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		// do nothing
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		super.createMenu(menu, item);
		menu.addMenuListener(new DefaultMenuListener(menu, item.getParent()));
	}

}
