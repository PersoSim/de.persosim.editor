package de.persosim.editor.ui.editor;

import java.util.HashMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.cardobjects.OidIdentifier;
import de.persosim.simulator.cardobjects.SecInfoObject;
import de.persosim.simulator.protocols.Oid;
import de.persosim.simulator.protocols.SecInfoPublicity;
import de.persosim.simulator.protocols.Tr03110;
import de.persosim.simulator.tlv.ConstructedTlvDataObject;
import de.persosim.simulator.tlv.PrimitiveTlvDataObject;
import de.persosim.simulator.tlv.TlvTag;
import de.persosim.simulator.tlv.TlvValuePlain;

public class CreateMobileSecInfoDialog extends Dialog {

	private List secInfoOids;
	private String lastSelected = null;
	private HashMap<String, Oid> nameToOid = new HashMap<>();

	public CreateMobileSecInfoDialog(Shell parent) {
		super(parent);
		nameToOid = new HashMap<>();
		nameToOid.put("HWKeystore", Tr03110.id_mobileEIDType_HWKeystore);
		nameToOid.put("SECertified", Tr03110.id_mobileEIDType_SECertified);
		nameToOid.put("SEEndorsed", Tr03110.id_mobileEIDType_SEEndorsed);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		secInfoOids = new List(parent, SWT.V_SCROLL);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumHeight = 200;
		layoutData.minimumWidth = 100;
		secInfoOids.setLayoutData(layoutData);

		
		for (String s : nameToOid.keySet()) {
			secInfoOids.add(s);
		}
		
		secInfoOids.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				updateFromSelection();
				close();
			}
		});

		secInfoOids.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFromSelection();
			}
		});

		return parent;
	}

	public SecInfoObject getSecInfoObject() {
		if (lastSelected == null) return null;
		
		SecInfoObject mobileIdObject = new SecInfoObject(new OidIdentifier(nameToOid.get(lastSelected)));

		ConstructedTlvDataObject mobileEidTypeInfo = new ConstructedTlvDataObject(new TlvTag(TlvTag.SEQUENCE));
		mobileEidTypeInfo.addTlvDataObject(new PrimitiveTlvDataObject(new TlvTag(TlvTag.OBJECT_IDENTIFIER),
				new TlvValuePlain(nameToOid.get(lastSelected).toByteArray())));
		mobileIdObject.setSecInfoContent(mobileEidTypeInfo);
		mobileIdObject.addPublicity(SecInfoPublicity.PUBLIC);
		mobileIdObject.addPublicity(SecInfoPublicity.AUTHENTICATED);
		
		return mobileIdObject;
	}

	private void updateFromSelection() {
		if (secInfoOids.getSelectionIndex() >= 0) {
			lastSelected = secInfoOids.getSelection()[0];
		}
	}
}
