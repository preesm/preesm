/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.workflow.elements;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Workflow graph
 * 
 * @author mpelcat
 */
public class Workflow extends
		DirectedMultigraph<AbstractWorkflowNode, WorkflowEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -908014142930559238L;

	public Workflow() {
		super(WorkflowEdge.class);
	}

	public List<AbstractWorkflowNode> vertexTopologicalList() {
		List<AbstractWorkflowNode> nodeList = new ArrayList<AbstractWorkflowNode>();
		TopologicalOrderIterator<AbstractWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<AbstractWorkflowNode, WorkflowEdge>(
				this);

		while (it.hasNext()) {
			AbstractWorkflowNode node = it.next();
			nodeList.add(node);
		}

		return nodeList;
	}

	public boolean hasScenario() {
		int nbScenarios = 0;
		for (AbstractWorkflowNode node : this.vertexSet()) {
			if (node.isScenarioNode()) {
				nbScenarios++;
			}
		}

		if (nbScenarios == 1) {
			return true;
		}

		return false;
	}
}