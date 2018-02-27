package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class NumberChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (!field.getText().matches(".*[^0-9].*")){
			return FieldCheckResult.OK;
		}
		return new FieldCheckResult("Numbers can only contain digits", State.WARNING);
	}

}
