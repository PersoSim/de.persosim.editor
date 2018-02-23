package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Text;

public class NullChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		return FieldCheckResult.OK;
	}

}
