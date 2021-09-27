package de.persosim.editor.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.editor.ui.editor.handlers.ObjectHandler;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.SecInfoObject;
import de.persosim.simulator.exception.AccessDeniedException;

public class MobileSecInfoMenuListener extends DefaultMenuListener {

	private DedicatedFile df;
	private HandlerProvider provider;

	public MobileSecInfoMenuListener(Menu menu, Tree dfTree, DedicatedFile df, HandlerProvider provider) {
		super(menu, dfTree);
		this.df = df;
		this.provider = provider;
	}

	@Override
	protected void addAddtionalEntries(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText("Add SecInfo object");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				CreateMobileSecInfoDialog dialog = new CreateMobileSecInfoDialog(Display.getCurrent().getActiveShell());				if (dialog.open() == Dialog.OK) {
					SecInfoObject sio = dialog.getSecInfoObject();
					try {
						if (sio != null) {
							df.addChild(sio);
							ObjectHandler handler = provider.get(sio); 
							TreeItem item = handler.createItem(dfTree, sio, provider);
							handler.changed(item);
							handler.setText(item);
						}
					} catch (AccessDeniedException e1) {
						BasicLogger.logException(getClass(), e1, LogLevel.WARN);
					}
				}
			}
		});

	}
}
