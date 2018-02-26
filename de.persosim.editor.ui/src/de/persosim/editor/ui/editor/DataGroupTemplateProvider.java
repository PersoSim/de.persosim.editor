package de.persosim.editor.ui.editor;

import java.util.Collection;

import de.persosim.simulator.cardobjects.ElementaryFile;

public interface DataGroupTemplateProvider {

	public Collection<String> supportedDgNames();

	public ElementaryFile getDgForName(String name);

}
