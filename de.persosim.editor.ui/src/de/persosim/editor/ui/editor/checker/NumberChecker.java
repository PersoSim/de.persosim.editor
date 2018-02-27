package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

public class NumberChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (!field.getText().matches(".*[^0-9].*")){
			return FieldCheckResult.OK;
		}
		return FieldCheckResult.WARNING;
	}

}
