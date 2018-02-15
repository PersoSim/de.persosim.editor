package de.persosim.editor.ui.editor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
import de.persosim.simulator.tlv.TlvDataObjectFactory;

public class DatagroupEditor extends Composite{

	public DatagroupEditor(Composite parent, Personalization perso, DedicatedFileIdentifier dedicatedFileIdentifier) {
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
				GridData lblDgNameGridData = new GridData();
				lblDgNameGridData.verticalAlignment = SWT.TOP;
				lblDgName.setLayoutData(lblDgNameGridData);
				
				ShortFileIdentifier sfi = null;
				
				for (CardObjectIdentifier identifierCandidate : ef.getAllIdentifiers()) {
					if (identifierCandidate instanceof ShortFileIdentifier) {
						sfi = (ShortFileIdentifier) identifierCandidate;
					}
				}
				
				lblDgName.setText("DG " + sfi.getShortFileIdentifier());
				
				Composite contentComposite = new Composite(this, SWT.NONE);
				GridData contentCompositeGridData = new GridData();
				contentCompositeGridData.grabExcessHorizontalSpace = true;
				contentComposite.setLayoutData(contentCompositeGridData);
				
				new TlvEditor(contentComposite, TlvDataObjectFactory.createTLVDataObject(content));
				
			} catch (AccessDeniedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
