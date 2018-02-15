package de.persosim.editor.ui.editor;

import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.persosim.simulator.tlv.TlvConstants;
import de.persosim.simulator.tlv.TlvDataObject;
import de.persosim.simulator.tlv.TlvDataObjectContainer;
import de.persosim.simulator.utils.HexString;

public class TlvEditor {

	public TlvEditor(Composite parent, TlvDataObject tlvObject) {
		parent.setLayout(new GridLayout(3, false));

		addObject(parent, tlvObject);
	}

	private void addObject(Composite parent, TlvDataObject tlvObject) {

		Composite currentComposite = new Composite(parent, SWT.NONE);
		currentComposite.setLayout(new GridLayout(3, false));

		Label lblTag = new Label(currentComposite, SWT.NONE);
		lblTag.setText("Tag: " + HexString.encode(tlvObject.getTlvTag().toByteArray()));

		Label lblLength = new Label(currentComposite, SWT.NONE);
		lblLength.setText("Length: " + HexString.encode(tlvObject.getTlvLength().toByteArray()));

		if (tlvObject.getTlvValue() instanceof TlvDataObjectContainer) {
			//dummy composite for positioning
			new Composite(currentComposite, SWT.NONE);
			
			Composite subElements = new Composite(currentComposite, SWT.NONE);
			subElements.setLayout(new GridLayout(1, false));
			GridData subElementsLayoutData = new GridData();
			subElementsLayoutData.grabExcessHorizontalSpace = true;
			subElementsLayoutData.horizontalSpan = 2;
			subElements.setLayoutData(subElementsLayoutData);

			for (TlvDataObject current : ((TlvDataObjectContainer) tlvObject.getTlvValue()).getTlvObjects()) {
				addObject(subElements, current);
			}
		} else {
			Text txtValue = new Text(parent, SWT.NONE);
			if (tlvObject.getTlvTag().equals(TlvConstants.TAG_IA5_STRING) || tlvObject.getTlvTag().equals(TlvConstants.TAG_PRINTABLE_STRING) || tlvObject.getTlvTag().equals(TlvConstants.TAG_NUMERIC_STRING)) {
				txtValue.setText(new String(tlvObject.getValueField(), StandardCharsets.US_ASCII));
			} else if (tlvObject.getTlvTag().equals(TlvConstants.TAG_UTF8_STRING)) {
				txtValue.setText(new String(tlvObject.getValueField(), StandardCharsets.UTF_8));
			} else {
				txtValue.setText(HexString.encode(tlvObject.getValueField()));
			}

			GridData txtValueGridData = new GridData();
			txtValueGridData.grabExcessHorizontalSpace = true;
		}

	}
}
