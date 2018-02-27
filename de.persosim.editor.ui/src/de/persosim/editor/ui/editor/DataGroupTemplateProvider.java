package de.persosim.editor.ui.editor;

import java.util.Collection;

import de.persosim.simulator.cardobjects.ElementaryFile;

public interface DataGroupTemplateProvider {

	public Collection<Integer> supportedDgNumbers();
	
	public ElementaryFile getDgForNumber(int number, String variant);

	public Collection<String> getVariants(int number);

}
