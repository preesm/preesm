/**
 * 
 */
package org.ietr.preesm.plugin.abc.route;

/**
 * Routes a communication and creates the necessary communication vertices
 * 
 * @author mpelcat
 */
public abstract class AbstractCommunicationRouter {

	private CommunicationRouterImplementer implementer;

	public AbstractCommunicationRouter() {
		super();
		this.implementer = null;
	}
}
