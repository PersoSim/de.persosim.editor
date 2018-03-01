package de.persosim.editor.ui.editor.checker;

import java.util.Arrays;
import java.util.StringJoiner;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class MultiLengthChecker implements TextFieldChecker {

	private int[] lengths;
	private String message;

	public MultiLengthChecker(int... allowedLengths) {
		Arrays.sort(allowedLengths);
		this.lengths = allowedLengths;
		StringJoiner joiner = new StringJoiner(", ");
		for (int current : lengths) {
			joiner.add(Integer.toString(current));
		}
		message = "Only the following number of characters are allowed: " + joiner.toString();
	}

	@Override
	public FieldCheckResult check(Text field) {
		StringJoiner joiner = new StringJoiner(", ");
		boolean valid = false;
		for (int current : lengths) {
			joiner.add(Integer.toString(current));
			valid |= field.getText().length() == current;
		}
		
		if (!valid) {
			return new FieldCheckResult(message, State.WARNING);
		}
		return FieldCheckResult.OK;
	}

}
