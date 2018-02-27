package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

public class IcaoCountryChecker extends UpperCaseTextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		FieldCheckResult superResult = super.check(field);
		if (superResult != FieldCheckResult.OK){
			return superResult;
		}
		if (field.getText().length() == 1 || field.getText().length() == 3){
			return FieldCheckResult.OK;
		}
		return FieldCheckResult.WARNING;
	}

}
