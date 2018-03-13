package de.persosim.editor.ui.editor.handlers;

public abstract class AbstractObjectModifier implements ObjectModifier {

	@Override
	public boolean getActivationState() {
		return getValue() != null;
	}

	@Override
	public void setActivationState() {
		//do nothing
	}
	
	@Override
	public void remove() {
		//do nothing
	}

}
