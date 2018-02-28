package de.persosim.editor.ui.editor;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.cardobjects.ElementaryFile;

public class CreateDatagroupDialog extends Dialog {

	private DataGroupTemplateProvider datagroupTemplates;
	private List dgTypes;
	private int lastSelected = -1;
	private String lastSelectedVariant = null;

	public CreateDatagroupDialog(Shell parent, DataGroupTemplateProvider datagroupTemplates) {
		super(parent);
		this.datagroupTemplates = datagroupTemplates;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		dgTypes = new List(parent, SWT.V_SCROLL);
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		layoutData.minimumHeight = 200;
		layoutData.minimumWidth = 100;
		dgTypes.setLayoutData(layoutData);

		Map<Integer, String> mapping = EidDgMapping.getMapping();
		Map<String, Integer> mappingToNumber = EidDgMapping.getMappingToNumber();

		datagroupTemplates.supportedDgNumbers().stream().forEach(current -> {
			Collection<String> variants = datagroupTemplates.getVariants(current);
			variants.stream().forEach(variant -> {dgTypes.add(mapping.get(current) + " (" + variant + ")");});
		});
		
		dgTypes.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				updateFromSelection(mappingToNumber);
				close();
			}
		});

		dgTypes.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateFromSelection(mappingToNumber);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		return parent;
	}

	protected void setSelected(int number, String variant) {
		lastSelected = number;
		lastSelectedVariant = variant;
	}

	public ElementaryFile getElementaryFile() {
		return datagroupTemplates.getDgForNumber(lastSelected, lastSelectedVariant);
	}

	private void updateFromSelection(Map<String, Integer> mappingToNumber) {
		if (dgTypes.getSelectionIndex() >= 0) {
			String selected = dgTypes.getSelection()[0];
			String variant = selected.substring(selected.lastIndexOf('(') + 1, selected.lastIndexOf(')'));
			Integer number = mappingToNumber.get(selected.substring(0, selected.lastIndexOf('(')).trim());
			setSelected(number, variant);
		}
	}
}
