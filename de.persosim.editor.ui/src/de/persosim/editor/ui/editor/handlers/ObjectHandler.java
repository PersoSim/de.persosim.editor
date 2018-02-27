package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public interface ObjectHandler {
	public static final String HANDLER = "HANDLER";

	public boolean canHandle(Object object);

	void createEditor(Composite parent, TreeItem item);

	/**
	 * Creates a new item describing the given object.
	 * 
	 * @param parentTree
	 * @param object
	 * @param provider
	 */
	void createItem(Tree parentTree, Object object, HandlerProvider provider);

	/**
	 * Creates a new item describing the given object.
	 * 
	 * @param parentItem
	 * @param object
	 * @param provider
	 */
	void createItem(TreeItem parentItem, Object object, HandlerProvider provider);

	/**
	 * Updates the text of the given item, using the object retrieved by
	 * {@link TreeItem#getData}.
	 * 
	 * @param item
	 */
	void setText(TreeItem item);

	/**
	 * Walks up the Tree of items and updates all texts along the way using
	 * {@link #setText(TreeItem)}.
	 * 
	 * @param item
	 */
	void updateTextRecursively(TreeItem item);

	/**
	 * Stores all data that is stored in an ephemeral way into the object that
	 * is retrieved by {@link TreeItem#getData()}. This could be for example
	 * data that is parsed from {@link TreeItem#getData()} and stored in
	 * {@link TreeItem#setData(String, Object)}.
	 * 
	 * @param item
	 */
	void persist(TreeItem item);

	/**
	 * This modifies the given menu with additional entries specific to the given {@link TreeItem}.
	 * @param menu
	 * @param item
	 */
	public void createMenu(Menu menu, TreeItem item);
}
