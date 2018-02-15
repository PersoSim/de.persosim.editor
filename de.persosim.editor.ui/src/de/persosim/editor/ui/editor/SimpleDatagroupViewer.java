package de.persosim.editor.ui.editor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.persosim.simulator.cardobjects.CardObject;
import de.persosim.simulator.cardobjects.CardObjectIdentifier;
import de.persosim.simulator.cardobjects.DedicatedFile;
import de.persosim.simulator.cardobjects.DedicatedFileIdentifier;
import de.persosim.simulator.cardobjects.ElementaryFile;
import de.persosim.simulator.cardobjects.MasterFile;
import de.persosim.simulator.cardobjects.ShortFileIdentifier;
import de.persosim.simulator.cardobjects.TypeIdentifier;
import de.persosim.simulator.exception.AccessDeniedException;
import de.persosim.simulator.perso.Personalization;
import de.persosim.simulator.platform.CommandProcessor;
import de.persosim.simulator.platform.PersonalizationHelper;
import de.persosim.simulator.utils.HexString;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

public class SimpleDatagroupViewer extends Composite{

	public SimpleDatagroupViewer(Composite parent, Personalization perso, DedicatedFileIdentifier dedicatedFileIdentifier) {
		super(parent, SWT.NONE);
		setLayout(new GridLayout(2, false));
		
		MasterFile mf = PersonalizationHelper.getUniqueCompatibleLayer(perso.getLayerList(), CommandProcessor.class).getObjectTree();
		
		Collection<CardObject> currentDfCandidates = mf.findChildren(dedicatedFileIdentifier);
		
		//FIXME check for size, type etc.
		if (currentDfCandidates.isEmpty()) {
			return;
		}
		DedicatedFile df = (DedicatedFile) currentDfCandidates.iterator().next();
		
		
		for (CardObject elementaryFile : df.findChildren(new TypeIdentifier(ElementaryFile.class))) {
			try {
				ElementaryFile ef = (ElementaryFile)elementaryFile;
				byte [] content = ef.getContent();
				
				Label lblDgName = new Label(this, SWT.NONE);
				lblDgName.setBounds(0, 0, 76, 18);
				
				ShortFileIdentifier sfi = null;
				
				for (CardObjectIdentifier identifierCandidate : ef.getAllIdentifiers()) {
					if (identifierCandidate instanceof ShortFileIdentifier) {
						sfi = (ShortFileIdentifier) identifierCandidate;
					}
				}
				
				lblDgName.setText("DG " + sfi.getShortFileIdentifier());
				
				Text text = new Text(this, SWT.BORDER);
				text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				text.setBounds(0, 0, 83, 34);
				text.setText(HexString.encode(content));
				
			} catch (AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
