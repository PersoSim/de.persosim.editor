package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

public class AndChecker implements TextFieldChecker {
	
	private TextFieldChecker[] checkers;

	public AndChecker(TextFieldChecker ... checkers) {
		this.checkers = checkers;
	}

	@Override
	public FieldCheckResult check(Text field) {
		for (TextFieldChecker checker : checkers){
			if (checker.check(field) != FieldCheckResult.OK){
				return FieldCheckResult.ERROR;
			}
		}
		return FieldCheckResult.OK;
	}

}
