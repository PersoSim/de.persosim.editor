package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class HexChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().length() % 2 == 1){
			return new FieldCheckResult("Hex string needs to be of even length", State.ERROR);
		}
		if (!field.getText().matches("[a-fA-F0-9\\s].*")){
			return new FieldCheckResult("Hex string can only contain letters, digits and whitespace", State.WARNING);
		}
		return FieldCheckResult.OK;
	}

}
