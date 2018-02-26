package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.EidDgMenuListener;
import de.persosim.simulator.cardobjects.DedicatedFile;

public class EidDedicatedFileHandler extends DedicatedFileHandler {

	private HandlerProvider provider;
	private DedicatedFile df;

	public EidDedicatedFileHandler(DedicatedFile df, HandlerProvider provider) {
		this.df = df;
		this.provider = provider;
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		menu.addMenuListener(new EidDgMenuListener(menu, item.getParent(), df, provider));
	}

}
