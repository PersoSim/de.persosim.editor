package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class LengthChecker implements TextFieldChecker {

	private int minLength;
	private int maxLength;

	public LengthChecker(int minLength, int maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
	}
	
	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().length() > maxLength){
			return new FieldCheckResult("Too many characters", State.WARNING);
		} else if (field.getText().length() < minLength){
			return new FieldCheckResult("Not enough characters", State.WARNING);
		}
		return FieldCheckResult.OK;
	}

}
