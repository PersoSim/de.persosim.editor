package de.persosim.editor.ui.editor.handlers;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult;
import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;
import de.persosim.editor.ui.editor.checker.TextFieldChecker;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

public class TlvContainerChecker implements TextFieldChecker {

	@Override
	public FieldCheckResult check(Text field) {
		try {
			new TlvDataObjectContainer(HexString.toByteArray(field.getText()));
		} catch (Exception e) {
			return new FieldCheckResult("Does not contain valid tlv objects", State.ERROR);
		}
		return FieldCheckResult.OK;
	}

}
