package de.persosim.editor.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

public class UiHelper {


	/**
	 * Sets the background color of a text field to indicate lower case letters
	 * @param field
	 * @return true, iff the field contains lower case letters
	 */
	public static boolean setColorIfLowerCase(Text field) {
		if (field.getText().matches(".*[a-z].*")){
			field.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			return true;
		}
		return false;
	}


	/**
	 * Sets the background color of a text field to indicate lower case letters
	 * @param field
	 * @return true, iff the field contains lower case letters
	 */
	public static boolean setColorIfMatch(Text field, String regex) {
		if (field.getText().matches(regex)){
			field.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
			return true;
		}
		return false;
	}
}
