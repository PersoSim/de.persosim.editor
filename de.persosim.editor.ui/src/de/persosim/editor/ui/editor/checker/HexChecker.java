package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

public class HexChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().length() % 2 == 1){
			return FieldCheckResult.ERROR;
		}
		if (field.getText().matches("[a-fA-F0-9\\s].*")){
			return FieldCheckResult.OK;
		}
		return FieldCheckResult.WARNING;
	}

}
