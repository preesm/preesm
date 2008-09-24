/**
 * 
 */
package org.ietr.preesm.plugin.mapper;

import org.ietr.preesm.core.task.TextParameters;

/**
 * Common behavior of all the mapping algorithm parameters
 * 
 * @author mpelcat
 */
public abstract class AbstractParameters {

	protected TextParameters textParameters = null;

	/**
	 * Constructor creating a new text parameter
	 */
	public AbstractParameters() {
		textParameters = new TextParameters();
	}
	
	/**
	 * Constructor from textual parameters
	 */
	public AbstractParameters(TextParameters textParameters) {
		this.textParameters = textParameters;
	}
	
	
	/**
	 * Generates textual parameters from its internal parameters
	 */
	public TextParameters textParameters(){
		return textParameters;
	}
}
