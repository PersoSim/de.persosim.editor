package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class NumberChecker implements TextFieldChecker {

	private boolean allowSpaces;
	private State state;

	public NumberChecker(boolean allowSpaces, State stateOnMismatch) {
		this.allowSpaces = allowSpaces;
		this.state = stateOnMismatch;
	}

	public NumberChecker(State stateOnMismatch) {
		this(false, stateOnMismatch);
	}

	public NumberChecker(boolean allowSpaces) {
		this(allowSpaces, State.WARNING);
	}

	public NumberChecker() {
		this(false);
	}

	@Override
	public FieldCheckResult check(Text field) {
		if (!field.getText().matches(allowSpaces ? ".*[^0-9 ].*" : ".*[^0-9].*")){
			return FieldCheckResult.OK;
		}
		return new FieldCheckResult("Numbers can only contain digits", state);
	}

}
