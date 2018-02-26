package de.persosim.editor.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;

public class DfEditor {

	private Tree dfTree;

	public DfEditor(Composite viewer, DedicatedFile df, NewEditorCallback editor, boolean compress,
			HandlerProvider provider) {

		viewer.setLayout(new FillLayout());

		dfTree = new Tree(viewer, SWT.NONE);

		dfTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(e.item instanceof TreeItem)) {
					return;
				}

				Composite localEditor = editor.getParent();
				localEditor.setLayout(new GridLayout(1, false));
				showEditor((TreeItem) e.item, localEditor);
				localEditor.requestLayout();
				localEditor.pack();
				editor.done();
			}

			private void showEditor(TreeItem item, Composite localEditor) {
				ObjectHandler objectHandler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);

				if (objectHandler != null) {
					objectHandler.createEditor(localEditor, item);
				}

				localEditor.pack();
				localEditor.requestLayout();
				localEditor.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		
		Menu menu = new Menu(dfTree);
		dfTree.setMenu(menu);
		menu.addMenuListener(new MenuListener() {
			
			@Override
			public void menuShown(MenuEvent e) {
				MenuItem[] items = menu.getItems();
				for (MenuItem item : items){
					item.dispose();
				}
				editor.getParent();
				
				MenuItem item = new MenuItem(menu, SWT.NONE);
				item.setText("Add datagroup");
				item.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						CreateDatagroupDialog dialog = new CreateDatagroupDialog(Display.getCurrent().getActiveShell(), new EidDatagroupTemplateProvider());
						if (dialog.open() == Dialog.OK){
							ElementaryFile ef = dialog.getElementaryFile();
							try {
								df.addChild(ef);
								provider.get(ef).createItem(dfTree, ef, provider);
							} catch (AccessDeniedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
				
				if (dfTree.getSelectionCount() > 0){
					ObjectHandler handler = (ObjectHandler) dfTree.getSelection()[0].getData(ObjectHandler.HANDLER);
					handler.createMenu(menu, dfTree.getSelection()[0]);
				}
			}
			
			@Override
			public void menuHidden(MenuEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		for (CardObject elementaryFile : df.findChildren(new TypeIdentifier(ElementaryFile.class))) {
			provider.get(elementaryFile).createItem(dfTree, elementaryFile, provider);
		}
		
		show();

		dfTree.pack();
	}

	public void show() {
		for (TreeItem current : dfTree.getItems()) {
			dfTree.showItem(current);
			show(current.getItems());
		}
	}

	private void show(TreeItem[] items) {
		for (TreeItem current : items) {
			dfTree.showItem(current);
			show(current.getItems());
		}
	}

	public void persist() {
		for (TreeItem current : dfTree.getItems()) {
			ObjectHandler handler = (ObjectHandler) current.getData(ObjectHandler.HANDLER);
			if (handler != null) {
				handler.persist(current);
			}
			
			persist(current.getItems());
		}
	}

	private void persist(TreeItem[] items) {
		for (TreeItem current : items) {
			ObjectHandler handler = (ObjectHandler) current.getData(ObjectHandler.HANDLER);
			if (handler != null) {
				handler.persist(current);
			}
			
			persist(current.getItems());
		}
	}
}
