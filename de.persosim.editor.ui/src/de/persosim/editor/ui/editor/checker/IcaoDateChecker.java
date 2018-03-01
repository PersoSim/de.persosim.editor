package de.persosim.editor.ui.editor.checker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Text;

import de.persosim.editor.ui.editor.checker.FieldCheckResult.State;

public class IcaoDateChecker implements TextFieldChecker {

	private LinkedList<DateFormat> allowedFormats;

	public IcaoDateChecker(boolean allowIncomplete) {
		allowedFormats = new LinkedList<DateFormat>();
		allowedFormats.add(new SimpleDateFormat("yyyyMMdd"));
		if (allowIncomplete) {
			allowedFormats.add(new SimpleDateFormat("    MMdd"));
			allowedFormats.add(new SimpleDateFormat("yyyy  dd"));
			allowedFormats.add(new SimpleDateFormat("yyyyMM  "));
			allowedFormats.add(new SimpleDateFormat("      dd"));
			allowedFormats.add(new SimpleDateFormat("    MM  "));
			allowedFormats.add(new SimpleDateFormat("yyyy    "));
		}
	}

	@Override
	public FieldCheckResult check(Text field) {

		for (DateFormat format : allowedFormats) {
			try {
				Date date = format.parse(field.getText());
				if (format.format(date).equals(field.getText())) {
					return FieldCheckResult.OK;
				}
			} catch (Exception e) {
			}
		}

		return new FieldCheckResult(
				"Only valid dates in the format YYYYMMDD with missing fields filled with ' ' are allowed",
				State.WARNING);
	}

}
