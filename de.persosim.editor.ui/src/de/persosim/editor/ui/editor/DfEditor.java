package de.persosim.editor.ui.editor;

import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.tlv.TlvDataObjectFactory;
import de.persosim.simulator.utils.HexString;

public class DfEditor {

	private Tree dfTree;
	private boolean compress = true;

	public DfEditor(Composite viewer, DedicatedFile df, NewEditorCallback editor, boolean compress) {
		this.compress = compress;
		
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
				if (!(item.getData() instanceof PrimitiveTlvDataObject)) {
					new Label(localEditor, SWT.NONE).setText("Not editable");
					return;
				}
				PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();
				Label typeLabel = new Label(localEditor, SWT.NONE);
				Text text = new Text(localEditor, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
				GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
				// layoutData.heightHint = 100;
				text.setLayoutData(layoutData);
				if (tlv.getTlvTag().equals(TlvConstants.TAG_IA5_STRING)
						|| tlv.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING)
						|| tlv.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
					typeLabel.setText("Type: IA5,PRINTABLE or NUMERIC string");
					text.setText(new String(tlv.getValueField(), StandardCharsets.US_ASCII));
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							tlv.setValue(text.getText().getBytes(StandardCharsets.US_ASCII));
							setItemText(item);
						}
					});
				} else if (tlv.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
					typeLabel.setText("Type: UTF8 string");
					text.setText(new String(tlv.getValueField(), StandardCharsets.UTF_8));
					text.addModifyListener(new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							tlv.setValue(text.getText().getBytes(StandardCharsets.UTF_8));
							setItemText(item);
						}
					});
				} else {
					typeLabel.setText("Type: binary data as hexadecimal string");
					text.setText(HexString.encode(tlv.getValueField()));
					text.addModifyListener(new ModifyListener() {
						Color defaultColor = text.getBackground();

						@Override
						public void modifyText(ModifyEvent e) {
							try {
								tlv.setValue(HexString.toByteArray(text.getText()));
								text.setBackground(defaultColor);
							} catch (Exception ex) {
								text.getBackground();
								text.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
							}
							setItemText(item);
						}
					});
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

		for (CardObject elementaryFile : df.findChildren(new TypeIdentifier(ElementaryFile.class))) {
			try {
				ElementaryFile ef = (ElementaryFile) elementaryFile;
				byte[] content = ef.getContent();

				TreeItem newItem = new TreeItem(dfTree, SWT.NONE);
				newItem.setData(ef);
				addObject(newItem, TlvDataObjectFactory.createTLVDataObject(content));

			} catch (AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		dfTree.pack();
	}

	private void addObject(TreeItem parent, TlvDataObject tlvObject) {
		if (tlvObject.getTlvValue() instanceof TlvDataObjectContainer) {
			TreeItem item = parent;
			if (!compress) {
				item = createItem(parent);
				item.setData(tlvObject);
				setItemText(item);
				dfTree.showItem(item);
			}
			for (TlvDataObject current : ((TlvDataObjectContainer) tlvObject.getTlvValue())) {
				addObject(item, current);
			}
		} else {
			TreeItem item = createItem(parent);
			item.setData(tlvObject);
			setItemText(item);
			dfTree.showItem(item);
		}

	}

	private void setItemText(TreeItem item) {
		if (item.getData() instanceof CardObject) {
			item.setText(
					"CardObject " + ((CardObject) item.getData()).getAllIdentifiers().iterator().next().toString());
		} else if (item.getData() instanceof TlvDataObject) {

			TlvDataObject tlvObject = (TlvDataObject) item.getData();
			
			String itemText = "";
			
			if (!compress) {
				itemText = HexString.encode((tlvObject).getTlvTag().toByteArray()) + " "
						+ HexString.encode(tlvObject.getTlvLength().toByteArray()) + " ";

			}
			
			
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

		}

		if (item.getParentItem() != null) {
			setItemText(item.getParentItem());
		}

	}

	private TreeItem createItem(TreeItem parent) {
		TreeItem treeItem = new TreeItem(parent, SWT.NONE);
		return treeItem;
	}
}
