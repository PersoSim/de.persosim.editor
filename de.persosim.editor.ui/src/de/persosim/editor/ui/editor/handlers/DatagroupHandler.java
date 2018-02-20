package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectFactory;

public class DatagroupHandler extends AbstractObjectHandler {

	
	private Map<Integer, String> dgMapping;

	public DatagroupHandler(Map<Integer, String> dgMapping) {
		this.dgMapping = dgMapping;
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			return true;
		}
		return false;
	}

	@Override
	public void createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof ElementaryFile) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((ElementaryFile) object, provider, item);
		}
	}

	@Override
	public void createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof ElementaryFile) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((ElementaryFile) object, provider, item);
		}
	}

	private void handleItem(ElementaryFile ef, HandlerProvider provider, TreeItem item) {
		item.setData(ef);
		setText(item);
		item.setData(HANDLER, this);
		try {
			TlvDataObject tlvObject = TlvDataObjectFactory.createTLVDataObject(ef.getContent());
			ObjectHandler handler = provider.get(tlvObject);
			if (handler != null) {
				handler.createItem(item, tlvObject, provider);
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createEditor(Composite parent, TreeItem item, HandlerProvider provider) {
		if (item.getData() instanceof ElementaryFile) {
			Label typeLabel = new Label(parent, SWT.NONE);
			Text text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
			text.setLayoutData(layoutData);
			typeLabel.setText("Type: elementary file, not editable");
		}
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile) item.getData();
			Integer sfid = null;
			for (CardObjectIdentifier current : ef.getAllIdentifiers()) {
				if (current instanceof ShortFileIdentifier) {
					sfid = ((ShortFileIdentifier) current).getShortFileIdentifier();
					break;
				}
			}

			if (dgMapping.get(sfid) != null) {
				item.setText("eID DG " + sfid + " " + dgMapping.get(sfid));
			} else {
				item.setText("Elementary file " + ef.toString());
			}
		}
	}

}
