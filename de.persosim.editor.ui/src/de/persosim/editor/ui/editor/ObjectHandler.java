package de.persosim.editor.ui.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public interface ObjectHandler {
	public boolean canHandle(Object object);
	void createEditor(Composite parent, TreeItem item, HandlerProvider provider);
	void createItem(Tree parentTree, Object object, HandlerProvider provider);
	void createItem(TreeItem parentItem, Object object, HandlerProvider provider);
	void setText(TreeItem item, Object object);
}
