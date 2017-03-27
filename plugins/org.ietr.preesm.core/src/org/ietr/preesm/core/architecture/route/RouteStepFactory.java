/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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

package org.ietr.preesm.core.architecture.route;

import java.util.List;

import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.architecture.slam.component.Dma;
import org.ietr.dftools.architecture.slam.component.Mem;
import org.ietr.dftools.architecture.slam.component.impl.DmaImpl;
import org.ietr.dftools.architecture.slam.component.impl.MemImpl;
import org.ietr.dftools.architecture.slam.link.ControlLink;
import org.ietr.dftools.architecture.slam.link.Link;

/**
 * Depending on the architecture nodes separating two operators, generates a
 * suited route step. The route steps represents one type of connection between
 * two connected operators
 * 
 * @author mpelcat
 */
public class RouteStepFactory {

	private Design archi = null;

	public RouteStepFactory(Design archi) {
		super();
		this.archi = archi;
	}

	/**
	 * Generates the suited route steps from intermediate nodes
	 */
	public AbstractRouteStep getRouteStep(ComponentInstance source,
			List<ComponentInstance> nodes, ComponentInstance target) {
		AbstractRouteStep step = null;

		Dma dma = getDma(nodes, source);
		Mem mem = getRam(nodes, source);
		if (dma != null) {
			step = new DmaRouteStep(source, nodes, target, dma);
		} else if (mem != null) {
			step = new MemRouteStep(source, nodes, target, mem,
					getRamNodeIndex(nodes));
		} else {
			step = new MessageRouteStep(source, nodes, target);
		}

		return step;
	}

	/**
	 * Gets the dma corresponding to the step if any exists. The Dma must have a
	 * setup link with the source.
	 */
	private Dma getDma(List<ComponentInstance> nodes, ComponentInstance dmaSetup) {
		ComponentInstance dmaInst = null;
		for (ComponentInstance node : nodes) {
			for (Link i : archi.getLinks()) {
				if (i.getSourceComponentInstance().getInstanceName()
						.equals(node.getInstanceName())
						|| i.getDestinationComponentInstance()
								.getInstanceName()
								.equals(node.getInstanceName())) {
					if (i.getSourceComponentInstance().getComponent() instanceof DmaImpl)
						dmaInst = i.getSourceComponentInstance();
					if (i.getDestinationComponentInstance().getComponent() instanceof DmaImpl)
						dmaInst = i.getDestinationComponentInstance();

					if (dmaInst != null) {
						if (existSetup(dmaInst, dmaSetup)) {
							return (Dma) dmaInst.getComponent();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the ram corresponding to the step if any exists. The ram must have a
	 * setup link with the source.
	 */
	private Mem getRam(List<ComponentInstance> nodes, ComponentInstance ramSetup) {
		ComponentInstance ramInst = null;
		for (ComponentInstance node : nodes) {
			for (Link i : archi.getLinks()) {
				if (i.getSourceComponentInstance().getInstanceName()
						.equals(node.getInstanceName())
						|| i.getDestinationComponentInstance()
								.getInstanceName()
								.equals(node.getInstanceName())) {
					if (i.getSourceComponentInstance().getComponent() instanceof MemImpl)
						ramInst = i.getSourceComponentInstance();
					if (i.getDestinationComponentInstance().getComponent() instanceof MemImpl)
						ramInst = i.getDestinationComponentInstance();

					if (ramInst != null) {
						if (existSetup(ramInst, ramSetup)) {
							return (Mem) ramInst.getComponent();
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the ram corresponding to the step if any exists. The ram must have a
	 * setup link with the source.
	 */
	private int getRamNodeIndex(List<ComponentInstance> nodes) {
		ComponentInstance ramInst = null;
		for (ComponentInstance node : nodes) {
			for (Link i : archi.getLinks()) {
				if (i.getSourceComponentInstance().getInstanceName()
						.equals(node.getInstanceName())
						|| i.getDestinationComponentInstance()
								.getInstanceName()
								.equals(node.getInstanceName())) {
					if (i.getSourceComponentInstance().getComponent() instanceof MemImpl)
						ramInst = i.getSourceComponentInstance();
					if (i.getDestinationComponentInstance().getComponent() instanceof MemImpl)
						ramInst = i.getDestinationComponentInstance();

					if (ramInst != null) {
						return nodes.indexOf(node);
					}
				}
			}
		}
		return -1;
	}

	/**
	 * Checks if a setup link exists between cmp and operator
	 */
	private boolean existSetup(ComponentInstance cmp, ComponentInstance op) {

		for (Link i : archi.getLinks()) {
			if (i.getSourceComponentInstance().getInstanceName()
					.equals(op.getInstanceName())
					&& i.getDestinationComponentInstance().getInstanceName()
							.equals(cmp.getInstanceName())
					&& (i instanceof ControlLink)) {
				return true;
			}
		}

		return false;
	}
}
