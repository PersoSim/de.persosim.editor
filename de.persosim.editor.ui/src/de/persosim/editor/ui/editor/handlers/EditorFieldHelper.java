package de.persosim.editor.ui.editor.handlers;

import java.awt.GridLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.editor.ui.editor.checker.TextFieldChecker;

public class EditorFieldHelper {
	
	/**
	 * @param item
	 * @param mandatory
	 * @param composite Expected to have a {@link GridLayout} with 2 columns
	 * @param modifier
	 * @param charset
	 * @param infoText
	 */
	public static void createField(TreeItem item, boolean mandatory, Composite composite, TlvModifier modifier, TextFieldChecker checker, String infoText) {
		Label info = new Label(composite, SWT.NONE);
		info.setText(infoText);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		info.setLayoutData(gd);

		Button fieldUsed = new Button(composite, SWT.CHECK);
		fieldUsed.setEnabled(!mandatory);

		Text field = new Text(composite, SWT.NONE);
		field.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		Color defaultColor = field.getBackground();


		String value = modifier.getValue();
		
		if (mandatory || value != null) {
			fieldUsed.setSelection(true);
			field.setEnabled(true);
			field.setText(value);
			checkAndModify(field, modifier, checker, defaultColor);
		} else {
			field.setEnabled(false);
		}

		field.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
		
				checkAndModify(field, modifier, checker, defaultColor);
				
				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null) {
					handler.updateTextRecursively(item);
				}
			}
		});

		fieldUsed.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				field.setEnabled(fieldUsed.getSelection());
				if (!fieldUsed.getSelection()) {
					modifier.remove();
				} else {
					modifier.setValue(field.getText());
				}

				ObjectHandler handler = (ObjectHandler) item.getData(ObjectHandler.HANDLER);
				if (handler != null) {
					handler.updateTextRecursively(item);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		
	}

	static void checkAndModify(Text field, TlvModifier modifier, TextFieldChecker checker, Color def){
		switch (checker.check(field)){
		case ERROR:
			field.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
			break;
		case OK:
			field.setBackground(def);
			modifier.setValue(field.getText());
			break;
		case WARNING:
			field.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_YELLOW));
			modifier.setValue(field.getText());
			break;
		default:
			break;
		}
	}
}
