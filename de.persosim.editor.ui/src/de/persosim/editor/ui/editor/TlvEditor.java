package de.persosim.editor.ui.editor;

import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

public class TlvEditor {

	private Tree tlvTree;

	public TlvEditor(Composite viewer, TlvDataObject tlvObject, NewEditorCallback editor) {
		viewer.setLayout(new FillLayout());

		tlvTree = new Tree(viewer, SWT.NONE);
		
		tlvTree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!(e.item instanceof TreeItem)) {
					return;
				}
				
				Composite localEditor = editor.getParent();
				localEditor.setLayout(new GridLayout(1, false));
				showEditor((TreeItem) e.item, localEditor);
			}

			private void showEditor(TreeItem item, Composite localEditor) {
				if (!(item.getData() instanceof PrimitiveTlvDataObject)) {
					new Label(localEditor, SWT.NONE).setText("Not editable");
					return;
				}
				PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();
				Text text = new Text(localEditor, SWT.NONE);
				if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
						|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
						|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {

					text.setText(new String(tlv.getValueField(), StandardCharsets.US_ASCII));
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							tlv.setValue(text.getText().getBytes(StandardCharsets.US_ASCII));
							setItemText(item);
						}
					});
				} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
					text.setText(new String(tlv.getValueField(), StandardCharsets.UTF_8));
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							tlv.setValue(text.getText().getBytes(StandardCharsets.UTF_8));
							setItemText(item);
						}
					});
				} else {
					text.setText(HexString.encode(tlv.getValueField()));
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							tlv.setValue(HexString.toByteArray(text.getText()));
							setItemText(item);
						}
					});
				}

				text.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
				text.setEditable(true);

				localEditor.pack();
				localEditor.requestLayout();
				localEditor.redraw();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		addObject(tlvTree, tlvObject);
		tlvTree.pack();
	}

	private void addObject(Tree tlvTree, TlvDataObject tlvObject) {
		TreeItem item = createItem(tlvTree);
		item.setData(tlvObject);
		handleItem(tlvObject, item);
	}

	private void addObject(TreeItem parent, TlvDataObject tlvObject) {
		TreeItem item = createItem(parent);
		item.setData(tlvObject);
		handleItem(tlvObject, item);

	}

	private void handleItem(TlvDataObject tlvObject, TreeItem item) {
		if (tlvObject.getTlvValue() instanceof TlvDataObjectContainer) {
			for (TlvDataObject current : ((TlvDataObjectContainer) tlvObject.getTlvValue()).getTlvObjects()) {
				addObject(item, current);
			}
		} else {
			setItemText(item);
		}
		tlvTree.showItem(item);
	}

	private void setItemText(TreeItem item) {
		TlvDataObject tlvObject = (TlvDataObject) item.getData();
		String itemText = HexString.encode((tlvObject).getTlvTag().toByteArray()) + " "
				+ HexString.encode(tlvObject.getTlvLength().toByteArray()) + " ";
		
		if (tlvObject instanceof PrimitiveTlvDataObject) {
			if (tlvObject.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
					|| tlvObject.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
					|| tlvObject.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				item.setText(itemText + new String(tlvObject.getValueField(), StandardCharsets.US_ASCII));
			} else if (tlvObject.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				item.setText(itemText + new String(tlvObject.getValueField(), StandardCharsets.UTF_8));
			} else {
				item.setText(itemText + HexString.encode(tlvObject.getValueField()));
			}
		} else {
			item.setText(itemText);
		}

		if (item.getParentItem() != null) {
			setItemText(item.getParentItem());
		}
		
		
	}

	private TreeItem createItem(TreeItem parent) {
		TreeItem treeItem = new TreeItem(parent, SWT.NONE);
		return treeItem;
	}

	private TreeItem createItem(Tree tlvTree) {
		TreeItem treeItem = new TreeItem(tlvTree, SWT.NONE);
		return treeItem;
	}
}
