/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

/**
 * Routes the communications coming from or going to 
 * 
 * Based on bridge design pattern
 * 
 * @author mpelcat
 */
public class CommunicationRouter extends AbstractCommunicationRouter {

	private boolean handleOverheads;

	public CommunicationRouter(boolean handleOverheads) {
		super();
		this.handleOverheads = handleOverheads;
	}
}
