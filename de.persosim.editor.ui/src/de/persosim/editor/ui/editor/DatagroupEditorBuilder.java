package de.persosim.editor.ui.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.perso.Personalization;

public class DatagroupEditorBuilder{

	public static DfEditor build(Composite parent, Personalization perso, DedicatedFile df, HandlerProvider provider) {
		
		parent.setLayout(new FillLayout());
		
		
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		
		sashForm.setLayout(new FillLayout());

		Composite overview = new Composite(sashForm, SWT.NONE);
		
		overview.setLayout(new GridLayout(2, false));
		
		Composite editor = new Composite(sashForm, SWT.NONE);
		
		editor.setLayout(new FillLayout());
		
		NewEditorCallback callback = new NewEditorCallback() {
			
			@Override
			public Composite getParent() {
				for (Control current : editor.getChildren()) {
					current.dispose();
				}
				
				return new Composite(editor, SWT.NONE);
			}

			@Override
			public void done() {
				editor.requestLayout();
				editor.redraw();
			}
		};
		
		DfEditor result = new DfEditor(overview, df, callback, false, provider);
		
		
		parent.pack();
		parent.requestLayout();
		
		return result;
	}
}
