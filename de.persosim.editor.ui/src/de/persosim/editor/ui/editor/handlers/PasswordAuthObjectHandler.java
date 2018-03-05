package de.persosim.editor.ui.editor.handlers;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.NumberChecker;
import de.persosim.simulator.cardobjects.PasswordAuthObject;

public class PasswordAuthObjectHandler extends AbstractObjectHandler {

	private Collection<Integer> allowedIds;

	public PasswordAuthObjectHandler(Collection<Integer> allowedIds) {
		this.allowedIds = allowedIds;
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof PasswordAuthObject){
			if (allowedIds.contains(((PasswordAuthObject)object).getPasswordIdentifier())){
				return true;
			}
		}
		return false;
	}

	@Override
	public TreeItem createItem(Tree parentTree, Object object, HandlerProvider provider) {
		return handleItem(new TreeItem(parentTree, SWT.NONE), object, provider);
	}

	@Override
	public TreeItem createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		return handleItem(new TreeItem(parentItem, SWT.NONE), object, provider);
	}

	private TreeItem handleItem(TreeItem treeItem, Object object, HandlerProvider provider) {
		treeItem.setData(object);
		setText(treeItem);
		treeItem.setData(ObjectHandler.HANDLER, this);
		return treeItem;
	}

	@Override
	public void setText(TreeItem item) {
		PasswordAuthObject authObject = (PasswordAuthObject) item.getData();
		item.setText(authObject.getPasswordName());
	}

	@Override
	protected String getType() {
		return "Password";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		PasswordAuthObject data = (PasswordAuthObject) item.getData();
		EditorFieldHelper.createField(item, true, false, composite, new TlvModifier() {
			
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
				return new String(data.getPassword(), StandardCharsets.US_ASCII);
			}
		}, new NumberChecker(), data.getPasswordName() + " is not modifiable");
	}
}
