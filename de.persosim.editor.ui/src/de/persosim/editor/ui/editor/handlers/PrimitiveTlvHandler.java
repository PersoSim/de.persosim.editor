package de.persosim.editor.ui.editor.handlers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.HexChecker;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.utils.HexString;

public class PrimitiveTlvHandler extends AbstractObjectHandler {

	protected boolean compress;

	public PrimitiveTlvHandler(boolean compress) {
		this.compress = compress;
	}

	@Override
	public boolean canHandle(Object object) {
		if (object instanceof PrimitiveTlvDataObject) {
			return true;
		}
		return false;
	}

	@Override
	public void createItem(TreeItem parentItem, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentItem, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
		}
	}

	@Override
	public void createItem(Tree parentTree, Object object, HandlerProvider provider) {
		if (object instanceof PrimitiveTlvDataObject) {
			TreeItem item = new TreeItem(parentTree, SWT.NONE);
			handleItem((PrimitiveTlvDataObject) object, provider, item);
		}
	}

	private void handleItem(PrimitiveTlvDataObject tlv, HandlerProvider provider, TreeItem item) {
		item.setData(tlv);
		setText(item);
		item.setData(HANDLER, this);
	}

	@Override
	public void setText(TreeItem item) {
		if (item.getData() instanceof PrimitiveTlvDataObject) {
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();

			String text = "";

			if (!compress) {
				text = HexString.encode(tlv.getTlvTag().toByteArray()) + " "
						+ HexString.encode(tlv.getTlvLength().toByteArray()) + " ";
			}

			text = HexString.encode(tlv.getValueField());

			if (text.isEmpty()) {
				if (compress) {
					text = "<empty>";
				}
			} else {
				if (text.length() > 32) {
					text = text.substring(0, 31);
				}

			}
			item.setText(text);
		}
	}

	@Override
	protected String getType() {
		return "primitive value field, editable";
	}

	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		if (item.getData() instanceof PrimitiveTlvDataObject) {
			composite.setLayout(new GridLayout(2, false));
			PrimitiveTlvDataObject tlv = (PrimitiveTlvDataObject) item.getData();

			TlvModifier modifier = new TlvModifier() {

				@Override
				public void setValue(String value) {
					tlv.setValue(HexString.toByteArray(value));
				}

				@Override
				public void remove() {
				}

				@Override
				public String getValue() {
					return HexString.encode(tlv.getValueField());
				}
			};
			
			Text field = EditorFieldHelper.createField(item, true, composite, modifier, new HexChecker(), "binary data as hexadecimal string");

			//dummy to fill empty cell
			new Composite(composite, SWT.NONE);
			
			Composite buttons = new Composite(composite, SWT.NONE);
			buttons.setLayout(new RowLayout());
			
			Button replace = new Button(buttons, SWT.PUSH);
			replace.setText("Browse for Replacement");
			replace.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					fd.setText("Open");
					fd.setFilterPath("C:/");
					String[] filterExt = { "*.bin", "*.*" };
					fd.setFilterExtensions(filterExt);
					String selection = fd.open();
					if (selection != null) {
						try {
							tlv.setValue(Files.readAllBytes(Paths.get(selection)));
							ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
							if (handler != null) {
								handler.updateTextRecursively(item);
							}
							field.setText(HexString.encode(tlv.getValueField()));
							for (Control control : composite.getChildren()){
								control.notifyListeners(SWT.Modify, new Event());
							}
						} catch (IOException e1) {
							MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "File could not be read.");
						}
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			Button save = new Button(buttons, SWT.PUSH);
			save.setText("Save data");
			save.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
					fd.setText("Save");
					fd.setFilterPath("C:/");
					String[] filterExt = { "*.bin", "*.*" };
					fd.setFilterExtensions(filterExt);
					String selection = fd.open();
					if (selection != null) {
						try {
							Files.write(Paths.get(selection), tlv.getValueField());
						} catch (IOException e1) {
							MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", "File could not be read.");
						}
					}
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			composite.pack();
		}
	}

}
