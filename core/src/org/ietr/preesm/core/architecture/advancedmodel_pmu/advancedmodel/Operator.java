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

import java.util.HashSet;
import java.util.Set;

/**
 * An operator is a hardware entity used to process data. Operators are
 * connected with communication links.
 * 
 * @author pmu
 */
public abstract class Operator extends AbstractNode {

	/**
	 * Communication performers are communicators and processors that can access
	 * this operator.
	 */
	private Set<AbstractNode> commPerformers;

	public Operator(String name, OperatorDefinition definition) {
		super(name, definition);
		commPerformers = new HashSet<AbstractNode>();
	}

	public boolean addCommunicationPerformer(AbstractNode commPerformer) {
		if (commPerformer instanceof Communicator
				|| commPerformer instanceof Processor) {
			return commPerformers.add(commPerformer);
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Operator) {
			Operator op = (Operator) obj;
			return this.getName().compareToIgnoreCase(op.getName()) == 0;
		}
		return false;
	}

	public Set<AbstractNode> getCommunicationPerformers() {
		return commPerformers;
	}

	@Override
	public OperatorDefinition getDefinition() {
		return (OperatorDefinition) definition;
	}

}
