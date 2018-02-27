package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class OrChecker implements TextFieldChecker {

	private TextFieldChecker[] checkers;

	public OrChecker(TextFieldChecker ... checkers) {
		this.checkers = checkers;
	}

	@Override
	public FieldCheckResult check(Text field) {
		String firstReason = null;
		State firstState = null;
		for (TextFieldChecker checker : checkers){
			FieldCheckResult check = checker.check(field);
			if (check.getState() == State.OK){
				return check;
			} else {
				if (firstReason == null){
					firstReason = check.getReason();	
					firstState = check.getState();
				}
			}
		}
		return new FieldCheckResult(firstReason, firstState);
	}

}
