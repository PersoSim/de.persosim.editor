package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class NumberChecker implements TextFieldChecker {

	private boolean allowSpaces;

	public NumberChecker(boolean allowSpaces) {
		this.allowSpaces = allowSpaces;
	}

	public NumberChecker() {
		this(false);
	}

	@Override
	public FieldCheckResult check(Text field) {
		if (!field.getText().matches(allowSpaces ? ".*[^0-9 ].*" : ".*[^0-9].*")){
			return FieldCheckResult.OK;
		}
		return new FieldCheckResult("Numbers can only contain digits", State.WARNING);
	}

}
