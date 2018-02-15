package de.persosim.editor.ui.editor;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class PersoEditorView {
    public static final String ID = "de.persosim.editor.e4.ui.plugin.partdescriptor.persoeditor";
	
    public PersoEditorView(Shell shell) {
    	createPartControl(shell);
	}
    
    @PostConstruct
    public void createPartControl(Composite parent) {
    	GridLayout gl_parent = new GridLayout(1, false);
    	gl_parent.horizontalSpacing = 0;
    	parent.setLayout(gl_parent);
    	
    	Group grpData = new Group(parent, SWT.NONE);
    	grpData.setLayout(new FillLayout(SWT.HORIZONTAL));
    	grpData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    	grpData.setText("Data");
    	grpData.setBounds(0, 0, 66, 66);
    	
    	TabFolder tabFolder = new TabFolder(grpData, SWT.NONE);
    	
    	TabItem tbtmmf = new TabItem(tabFolder, SWT.NONE);
    	tbtmmf.setText("Masterfile");
    	
    	TabItem tbtmNPa = new TabItem(tabFolder, SWT.NONE);
    	tbtmNPa.setText("nPA");
    	
    	TabItem tbtmEPa = new TabItem(tabFolder, SWT.NONE);
    	tbtmEPa.setText("ePassport");
    	
    	Group grpControl = new Group(parent, SWT.NONE);
    	grpControl.setLayout(new RowLayout(SWT.HORIZONTAL));
    	grpControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    	grpControl.setText("Signature Options");
    	
    	Button btnResignEfCardSecurity = new Button(grpControl, SWT.CHECK);
    	btnResignEfCardSecurity.setText("Resign EF.CardSecurity");
    	
    	Button btnResignEfchipsecurity = new Button(grpControl, SWT.CHECK);
    	btnResignEfchipsecurity.setText("Resign EF.ChipSecurity");
    	
    	Button btnUpdateEfsod = new Button(grpControl, SWT.CHECK);
    	btnUpdateEfsod.setText("Update EF.SOD");
    }
	
    @Focus
    public void setFocus() {
    }
    
    @Persist
    void doSave(@Optional IProgressMonitor monitor) {
    }
}
