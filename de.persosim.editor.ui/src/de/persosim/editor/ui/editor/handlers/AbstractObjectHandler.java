package de.persosim.editor.ui.editor.handlers;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;

public abstract class AbstractObjectHandler implements ObjectHandler{
	
	private static final String CHANGED_MARKER= " *";

	@Override
	public void updateTextRecursively(TreeItem item) {
		setText(item);
		if (item.getParentItem() != null) {
			((ObjectHandler)item.getParentItem().getData(HANDLER)).updateTextRecursively(item.getParentItem());
		}
	}
	
	@Override
	public void persist(TreeItem item) {
		if (item.getParentItem() != null) {
			((ObjectHandler)item.getParentItem().getData(HANDLER)).persist(item.getParentItem());
		}
	}
	
	@Override
	public void createEditor(Composite parent, TreeItem item) {
		parent.setLayout(new GridLayout(1, false));
		new Label(parent, SWT.NONE).setText("Type: " + getType());
		Composite editor = new Composite(parent, SWT.NONE);
		editor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createEditingComposite(editor, item);
	}
	
	@Override
	public void createMenu(Menu menu, TreeItem item) {
		//Intentionally do nothing
	}
	
	@Override
	public void removeItem(TreeItem item) {
		//Intentionally do nothing
	}
	
	@Override
	public boolean hasChanges(TreeItem item) {
		return item.getData(ObjectHandler.CHANGED) != null;
	}

	protected String getChangedText(TreeItem item) {
		if (hasChanges(item)) {
			return CHANGED_MARKER;
		}
		for (TreeItem current : item.getItems()) {
			ObjectHandler handler = (ObjectHandler) current.getData(HANDLER);
			if (handler != null && handler.hasChanges(current)) {
				return CHANGED_MARKER;
			}
		}
		return "";
	}
	
	@Override
	public void changed(TreeItem item) {
		item.setData(ObjectHandler.CHANGED, "");
	}
	
	abstract protected String getType();
	
	abstract protected void createEditingComposite(Composite composite, TreeItem item);
}
