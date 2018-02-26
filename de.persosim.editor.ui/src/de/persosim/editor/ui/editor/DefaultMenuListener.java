package de.persosim.editor.ui.editor;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import de.persosim.editor.ui.editor.handlers.ObjectHandler;

public class DefaultMenuListener implements MenuListener {

	protected Menu menu;
	protected Tree dfTree;

	public DefaultMenuListener(Menu menu, Tree dfTree) {
		this.menu = menu;
		this.dfTree = dfTree;
	}

	@Override
	public void menuShown(MenuEvent e) {
		MenuItem[] items = menu.getItems();
		for (MenuItem item : items) {
			item.dispose();
		}

		addAddtionalEntries(menu);
		
		if (dfTree.getSelectionCount() > 0) {
			ObjectHandler handler = (ObjectHandler) dfTree.getSelection()[0].getData(ObjectHandler.HANDLER);
			handler.createMenu(menu, dfTree.getSelection()[0]);
		}
	}

	protected void addAddtionalEntries(Menu menu) {
		//Do nothing
	}

	@Override
	public void menuHidden(MenuEvent e) {
		//Do nothing
	}
}
