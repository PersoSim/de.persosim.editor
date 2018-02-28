package de.persosim.editor.ui.editor.checker;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class IcaoSexChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		if (field.getText().matches("[MF ]*")){
			return FieldCheckResult.OK;
		}
		return new FieldCheckResult("Only 'F', 'M' and ' ' are allowed", State.WARNING);
	}

}
