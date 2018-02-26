package de.persosim.editor.ui.editor;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.persosim.simulator.cardobjects.ElementaryFile;

public class CreateDatagroupDialog extends Dialog {

	private DataGroupTemplateProvider datagroupTemplates;
	private List dgTypes;
	private String lastSelected = null;

	public CreateDatagroupDialog(Shell parent, DataGroupTemplateProvider datagroupTemplates) {
		super(parent);
		this.datagroupTemplates = datagroupTemplates;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		dgTypes = new List(composite, SWT.NONE);

		datagroupTemplates.supportedDgNames().stream().sorted().forEach(current -> {dgTypes.add(current);});
		
		dgTypes.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dgTypes.getSelectionIndex() >= 0){
					setSelected(dgTypes.getSelection()[0]);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return composite;
	}
	
	protected void setSelected(String string) {
		lastSelected = string;
	}

	public ElementaryFile getElementaryFile(){
		if (lastSelected != null){
			return datagroupTemplates.getDgForName(lastSelected);
		}
		return null;
	}
}
