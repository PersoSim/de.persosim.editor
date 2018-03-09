package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class LengthChecker implements TextFieldChecker {

	private int minLength;
	private int maxLength;
	private State state;

	public LengthChecker(int minLength, int maxLength, State stateOnMismatch) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.state = stateOnMismatch;
	}
	
	public LengthChecker(int minLength, int maxLength) {
		this(minLength, maxLength, State.WARNING);
	}
	
	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().length() > maxLength){
			return new FieldCheckResult("Too many characters", state);
		} else if (field.getText().length() < minLength){
			return new FieldCheckResult("Not enough characters", state);
		}
		return FieldCheckResult.OK;
	}

}
