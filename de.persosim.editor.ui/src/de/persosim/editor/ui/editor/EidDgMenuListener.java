package de.persosim.editor.ui.editor;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;

public class EidDgMenuListener extends DefaultMenuListener {
	
	private DedicatedFile df;
	private HandlerProvider provider;

	public EidDgMenuListener(Menu menu, Tree dfTree, DedicatedFile df, HandlerProvider provider) {
		super(menu, dfTree);
		this.df = df;
		this.provider = provider;
	}

	@Override
	protected void addAddtionalEntries(Menu menu) {
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText("Add datagroup");
		item.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Collection<CardObject> children = df.findChildren(new TypeIdentifier(ElementaryFile.class));

				HashSet<Integer> toFilter = new HashSet<>();

				for (CardObject current : children) {
					for (CardObjectIdentifier identifier : current.getAllIdentifiers()) {
						if (identifier instanceof ShortFileIdentifier) {
							toFilter.add(((ShortFileIdentifier) identifier).getShortFileIdentifier());
						}
					}
				}

				CreateDatagroupDialog dialog = new CreateDatagroupDialog(Display.getCurrent().getActiveShell(),
						new EidDatagroupTemplateProvider(toFilter));
				if (dialog.open() == Dialog.OK) {
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

	}
}