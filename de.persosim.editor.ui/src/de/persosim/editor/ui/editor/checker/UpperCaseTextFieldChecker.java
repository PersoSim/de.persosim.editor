package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

public class UpperCaseTextFieldChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().matches(".*[a-z].*")){
			return FieldCheckResult.WARNING;
		}
		return FieldCheckResult.OK;
	}

}
