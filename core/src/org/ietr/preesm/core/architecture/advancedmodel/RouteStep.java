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

import java.util.ArrayList;
import java.util.List;

/**
 * A route step is a part of a route. It starts on a terminal and finishes on
 * another terminal. A processor is used to control the communication on this
 * route step.
 * 
 * @author pmu
 */
public class RouteStep {

	private Processor processor;

	private Communicator communicator = null;

	private ITerminal startTerminal;

	private ILink startLink = null;

	private List<NodeLinkTuple> nodeLinkTuples;

	private ITerminal finishTerminal;

	public RouteStep(Processor processor, ITerminal startTerminal,
			ITerminal finishTerminal) {
		this.processor = processor;
		this.startTerminal = startTerminal;
		this.finishTerminal = finishTerminal;
		nodeLinkTuples = new ArrayList<NodeLinkTuple>();
	}

	public void addNodeLinkTuple(int index, NodeLinkTuple nodeLinkTuple) {
		nodeLinkTuples.add(index, nodeLinkTuple);
	}

	public void addNodeLinkTuple(NodeLinkTuple nodeLinkTuple) {
		nodeLinkTuples.add(nodeLinkTuple);
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public ITerminal getFinishTerminal() {
		return finishTerminal;
	}

	public List<NodeLinkTuple> getNodeLinkTuples() {
		return nodeLinkTuples;
	}

	public Processor getProcessor() {
		return processor;
	}

	public ILink getStartLink() {
		return startLink;
	}

	public ITerminal getStartTerminal() {
		return startTerminal;
	}

	public void setCommunicator(Communicator communicator) {
		this.communicator = communicator;
	}

	public void setFinishTerminal(ITerminal finishTerminal) {
		this.finishTerminal = finishTerminal;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public void setStartLink(ILink startLink) {
		this.startLink = startLink;
	}

	public void setStartTerminal(ITerminal startTerminal) {
		this.startTerminal = startTerminal;
	}

	@Override
	public String toString() {
		if (communicator == null) {
			return "{" + "(" + startTerminal.toString() + " ... "
					+ finishTerminal.toString() + ")" + "}";
		} else {
			return "{" + communicator.toString() + ", ("
					+ startTerminal.toString() + " ... "
					+ finishTerminal.toString() + ")" + "}";
		}
	}
}
