package de.persosim.editor.ui.editor.handlers;

/**
 * This interface provides an adapter for modification of objects that are represented by a {@link String}.
 * @author mboonk
 *
 */
public interface ObjectModifier {

	/**
	 * @return the objects value into its {@link String} representation
	 */
	String getValue();
	
	/**
	 * @return the objects activation state, i.e. if it is considered active or "usable"
	 */
	boolean getActivationState();
	
	/**
	 * Modifies the objects activation state, i.e. if it is considered active or "usable"
	 */
	void setActivationState(boolean active);

	/**
	 * Sets the objects value by conversion from the {@link String} representation
	 * @param string
	 */
	void setValue(String string);

	/**
	 * Removes the object from its parent structure, e.g. deleting a tlv object from its parent constructed object
	 */
	void remove();

}
