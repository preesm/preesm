/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Peng Cheng Mu, Jean-François Nezan, Mickaël Raulet

[mwipliez,jpiat,mpelcat,pmu,jnezan,mraulet]@insa-rennes.fr

This software is a computer program whose purpose is to prototype
parallel applications.

This software is governed by the CeCILL-C license under French law and
abiding by the rules of distribution of free software.  You can  use, 
modify and/ or redistribute the software under the terms of the CeCILL-C
license as circulated by CEA, CNRS and INRIA at the following URL
"http://www.cecill.info". 

As a counterpart to the access to the source code and  rights to copy,
modify and redistribute granted by the license, users are provided only
with a limited warranty  and the software's author,  the holder of the
economic rights,  and the successive licensors  have only  limited
liability. 

In this respect, the user's attention is drawn to the risks associated
with loading,  using,  modifying and/or developing or reproducing the
software by the user in light of its specific status of free software,
that may mean  that it is complicated to manipulate,  and  that  also
therefore means  that it is reserved for developers  and  experienced
professionals having in-depth computer knowledge. Users are therefore
encouraged to load and test the software's suitability as regards their
requirements in conditions enabling the security of their systems and/or 
data to be ensured and,  more generally, to use and operate it in the 
same conditions as regards security. 

The fact that you are presently reading this means that you have had
knowledge of the CeCILL-C license and that you accept its terms.
 *********************************************************/
package org.ietr.preesm.plugin.mapper.listsched.descriptor;

import java.util.Vector;

/**
 * This class gives the description of a route
 * 
 * @author pmu
 * 
 */
public class RouteDescriptor {
	/**
	 * A list of links composing the route
	 */
	private Vector<LinkDescriptor> linkList;

	/**
	 * Construct a RouteDescriptor
	 */
	public RouteDescriptor() {
		linkList = new Vector<LinkDescriptor>();
	}

	/**
	 * Add a link to the route at the specified location
	 * 
	 * @param index
	 *            The specified location
	 * @param link
	 *            A link
	 */
	public void addLink(int index, LinkDescriptor link) {
		linkList.add(index, link);
	}

	/**
	 * Add a link to the route at the end of the list
	 * 
	 * @param link
	 *            A link
	 */
	public void addLink(LinkDescriptor link) {
		linkList.add(link);
	}

	/**
	 * Get the first link of the route
	 * 
	 * @return The first link of the route
	 */
	public LinkDescriptor getFirstLink() {
		return linkList.firstElement();
	}

	/**
	 * Get the last link of the route
	 * 
	 * @return The last link of the route
	 */
	public LinkDescriptor getLastLink() {
		return linkList.lastElement();
	}

	/**
	 * Get the link at the specified location
	 * 
	 * @param index
	 *            The specified location
	 * @return The link
	 */
	public LinkDescriptor getLink(int index) {
		return linkList.get(index);
	}

	/**
	 * Get the list of link
	 * 
	 * @return The list of link
	 */
	public Vector<LinkDescriptor> getLinkList() {
		return linkList;
	}

}
