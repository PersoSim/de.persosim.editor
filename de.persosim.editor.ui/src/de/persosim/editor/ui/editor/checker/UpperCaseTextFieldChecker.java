package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class UpperCaseTextFieldChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().matches(".*[a-z].*")){
			return new FieldCheckResult("Only uppercase letters are allowed", State.WARNING);
		}
		return FieldCheckResult.OK;
	}

}
