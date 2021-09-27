package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.MobileSecInfoMenuListener;
import de.persosim.simulator.cardobjects.MasterFile;

public class MasterFileHandler extends DedicatedFileHandler {

	private HandlerProvider provider;
	private MasterFile mf;

	public MasterFileHandler(MasterFile df, HandlerProvider provider) {
		this.mf = df;
		this.provider = provider;
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		menu.addMenuListener(new MobileSecInfoMenuListener(menu, item.getParent(), mf, provider));
	}

}
