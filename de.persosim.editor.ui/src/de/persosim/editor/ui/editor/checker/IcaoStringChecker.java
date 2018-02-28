package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class IcaoStringChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {

		if (field.getText().matches("[A-Z ]*")){
			return FieldCheckResult.OK;
		}
		return new FieldCheckResult("Only A-Z and ' ' are allowed", State.WARNING);
	}

}
