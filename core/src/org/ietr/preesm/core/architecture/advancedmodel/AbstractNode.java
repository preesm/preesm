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

package org.ietr.preesm.core.architecture.advancedmodel;

import java.util.Iterator;
import java.util.Set;

/**
 * Common features of nodes in an architecture. Nodes include processor, memory,
 * communicator and communicationNode.
 * 
 * @author pmu
 */
public abstract class AbstractNode extends Component {

	/**
	 * Constructor from a type and a name
	 */

	public AbstractNode(String name, AbstractNodeDefinition definition) {
		super(name, definition);
	}

	public boolean addInterface(SpiritInterface intf) {
		return availableInterfaces.add(intf);
	}

	public boolean canConnectTo(AbstractLink link) {
		boolean compatible = false;
		AbstractLinkDefinition linkDef = link.getDefinition();
		Set<SpiritInterface> nodeIntfSet = this.getInterfaces(linkDef);
		Set<SpiritInterface> linkIntfSet = this.getInterfaces(linkDef);
		Iterator<SpiritInterface> nodeIt = nodeIntfSet.iterator();
		while (nodeIt.hasNext()) {
			SpiritInterface intfNode = nodeIt.next();
			Iterator<SpiritInterface> linkIt = linkIntfSet.iterator();
			while (linkIt.hasNext()) {
				SpiritInterface intfLink = linkIt.next();
				if (intfNode.canConnectTo(intfLink)) {
					compatible = true;
					break;
				}
			}
			if (compatible) {
				break;
			}
		}
		return compatible;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AbstractNode) {
			AbstractNode node = (AbstractNode) obj;
			return this.getName().compareToIgnoreCase(node.getName()) == 0;
		}
		return false;
	}

	@Override
	public AbstractNodeDefinition getDefinition() {
		return (AbstractNodeDefinition) definition;
	}
}
