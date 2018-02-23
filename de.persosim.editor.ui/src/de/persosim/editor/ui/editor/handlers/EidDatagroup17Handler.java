package de.persosim.editor.ui.editor.handlers;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;

public class EidDatagroup17Handler extends DatagroupHandler implements ObjectHandler {
	
	public EidDatagroup17Handler(Map<Integer, String> dgMapping) {
		super(dgMapping);
	}
	
	@Override
	public boolean canHandle(Object object) {
		if (object instanceof ElementaryFile) {
			ElementaryFile ef = (ElementaryFile)object;
			
			return new ShortFileIdentifier(17).matches(ef);
		}
		return false;
	}
	
	@Override
	protected void createEditingComposite(Composite composite, TreeItem item) {
		composite.setLayout(new GridLayout(2, false));
		
		Button streetUsed = new Button(composite, SWT.CHECK);
		Text street = new Text(composite, SWT.NONE);
		street.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button cityUsed = new Button(composite, SWT.CHECK);
		cityUsed.setEnabled(false);
		cityUsed.setSelection(true);
		Text city = new Text(composite, SWT.NONE);
		city.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button stateUsed = new Button(composite, SWT.CHECK);
		Text state = new Text(composite, SWT.NONE);
		state.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button countryUsed = new Button(composite, SWT.CHECK);
		countryUsed.setEnabled(false);
		countryUsed.setSelection(true);
		Text country = new Text(composite, SWT.NONE);
		country.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button zipcodeUsed = new Button(composite, SWT.CHECK);
		Text zipcode = new Text(composite, SWT.NONE);
		zipcode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		SelectionListener selectionListener = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		};

		zipcodeUsed.addSelectionListener(selectionListener);
		stateUsed.addSelectionListener(selectionListener);
		streetUsed.addSelectionListener(selectionListener);
		
	}
}
