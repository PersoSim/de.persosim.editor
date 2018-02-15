package de.persosim.editor.ui.editor;

import javax.annotation.PostConstruct;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.widgets.Composite;

public class EditorView {
    public static final String ID = "de.persosim.editor.e4.ui.plugin.partdescriptor.persoeditor";
	
    @PostConstruct
    public void createPartControl(Composite parent) {
    }
	
    @Focus
    public void setFocus() {
    }
    
    @Persist
    void doSave(@Optional IProgressMonitor monitor) {
    }
}
