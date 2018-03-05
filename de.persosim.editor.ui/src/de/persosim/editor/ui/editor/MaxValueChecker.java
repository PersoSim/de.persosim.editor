package de.persosim.editor.ui.editor;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult;
import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;
import de.persosim.editor.ui.editor.checker.TextFieldChecker;

public class MaxValueChecker implements TextFieldChecker {

	private int maxValue;

	public MaxValueChecker(int maxValue) {
		this.maxValue = maxValue;
	}

	@Override
	public FieldCheckResult check(Text field) {
		try {
			if (Integer.parseInt(field.getText()) > maxValue) {
				return new FieldCheckResult("Value can not exceed " + maxValue, State.ERROR);
			}
		} catch (Exception e) {
			return new FieldCheckResult("Not a valid number", State.ERROR);
		}
		return FieldCheckResult.OK;
	}

}
