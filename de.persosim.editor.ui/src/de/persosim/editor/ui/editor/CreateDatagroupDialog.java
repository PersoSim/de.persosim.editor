package de.persosim.editor.ui.editor;

import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
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
		Map<String,Integer> mappingToNumber = EidDgMapping.getMappingToNumber();
		
		datagroupTemplates.supportedDgNumbers().stream().forEach(current -> {dgTypes.add(mapping.get(current));});
		
		dgTypes.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dgTypes.getSelectionIndex() >= 0){
					setSelected(mappingToNumber.get(dgTypes.getSelection()[0]));
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return parent;
	}
	
	protected void setSelected(int number) {
		lastSelected = number;
	}

	public ElementaryFile getElementaryFile(){
		return datagroupTemplates.getDgForNumber(lastSelected);
	}
}
