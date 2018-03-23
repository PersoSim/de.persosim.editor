
package de.persosim.editor.ui.editor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.swt.widgets.Composite;

public class PersoEditorPart {

	private PersoEditorView editor;

	@PostConstruct
	public void postConstruct(Composite parent) {
		editor = new PersoEditorView();
		editor.createEditor(parent);
	}

	@PreDestroy
	public void preDestroy() {
		
	}

	@Focus
	public void onFocus() {
		
	}

	@Persist
	public void save() {
	}

}