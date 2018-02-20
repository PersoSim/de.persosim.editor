package de.persosim.editor.ui.editor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.persosim.editor.ui.editor.handlers.HandlerProvider;
import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;

public class DatagroupEditorBuilder{

	public static void build(Composite parent, Personalization perso, CardObjectIdentifier fileIdentifier, HandlerProvider provider) {
		
		parent.setLayout(new FillLayout());
		
		
		SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		
		sashForm.setLayout(new FillLayout());

		Composite overview = new Composite(sashForm, SWT.NONE);
		
		overview.setLayout(new GridLayout(2, false));
		
		Composite editor = new Composite(sashForm, SWT.NONE);
		
		editor.setLayout(new FillLayout());
		
		MasterFile mf = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getObjectTree();
		DedicatedFile df = mf;
		
		if (fileIdentifier != null) {
			Collection<CardObject> currentDfCandidates = mf.findChildren(fileIdentifier);
			
			//FIXME check for size, type etc.
			if (currentDfCandidates.isEmpty()) {
				return;
			}
			df = (DedicatedFile) currentDfCandidates.iterator().next();
		}
		

		
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
		
		new DfEditor(overview, df, callback, false, provider);
		
		
		parent.pack();
		parent.requestLayout();
	}
}
