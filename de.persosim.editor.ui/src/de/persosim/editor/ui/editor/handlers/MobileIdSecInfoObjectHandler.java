package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.globaltester.logging.BasicLogger;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.editor.ui.editor.checker.NullChecker;
import de.persosim.simulator.cardobjects.SecInfoObject;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.TlvDataObject;

public class MobileIdSecInfoObjectHandler extends AbstractObjectHandler {

	public static final String EXTRACTED_TLV = "EXTRACTED_TLV";

	public MobileIdSecInfoObjectHandler() {
		super();
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof SecInfoObject) {
			return true;
		}
		return false;
	}

	@Override
	public TreeItem createItem(Tree parentItem, Object object, HandlerProvider provider) {
		if (object instanceof SecInfoObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((SecInfoObject) object, provider, item);
			return item;
		}
		return null;
	}

	protected void handleItem(SecInfoObject sio, HandlerProvider provider, TreeItem item) {
		item.setData(sio);
		setText(item);
		item.setData(HANDLER, this);
		TlvDataObject tlvObject = sio.getSecInfoContent();
		item.setData(EXTRACTED_TLV, tlvObject);
		handleItem(provider, item, tlvObject);
	}

	protected void handleItem(HandlerProvider provider, TreeItem item, TlvDataObject tlvObject) {
		ObjectHandler handler = provider.get(tlvObject);
		if (handler != null) {
			handler.createItem(item, tlvObject, provider);
		}
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof SecInfoObject) {
			String newText = "Mobile ID SecInfo";

			item.setText(newText + getChangedText(item));
		}
	}

	@Override
	public void createMenu(Menu menu, TreeItem item) {
		super.createMenu(menu, item);
		MenuItem mitem = new MenuItem(menu, SWT.NONE);
		mitem.setText("Remove SecInfo object");
		mitem.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				SecInfoObject sio = (SecInfoObject) item.getData();
				try {
					sio.getParent().removeChild(sio);
					item.dispose();
				} catch (AccessDeniedException e1) {
					BasicLogger.logException(getClass(), e1, LogLevel.WARN);
				}
			}
		});
	}

	@Override
	protected String getType() {
		return "Mobile ID SecInfo";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		SecInfoObject data = (SecInfoObject) item.getData();
		EditorFieldHelper.createField(item, true, false, composite, new AbstractObjectModifier() {
			
			@Override
			public void setValue(String string) {
				// not intended
			}
			
			@Override
			public void remove() {
				// not intended
			}
			
			@Override
			public String getValue() {
				return data.getPrimaryIdentifier().getOid().toString();
			}
		}, new NullChecker(), "Mobile ID SecInfo is not modifiable, only replaceable");
	}

}
