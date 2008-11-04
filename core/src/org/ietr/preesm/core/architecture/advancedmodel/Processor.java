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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;

/**
 * A processor is a hardware entity used to execute computations and perform
 * communications.
 * 
 * @author pmu
 */
public class Processor extends ArchitectureComponent implements Terminal,
		CommunicationPerformer {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * computation vertex
	 */
	public static final String propertyBeanName = "processor";

	/**
	 * A communicator can be configured by this processor by using some time.
	 * The used time for the communicator is stored in setupTimes.
	 */
	private Map<Communicator, Double> setupTimes;

	/**
	 * Communication performers are communicators and processors that can access
	 * this processor.
	 */
	private Set<CommunicationPerformer> commPerformers;

	public Processor(String name, ProcessorDefinition definition) {
		super(name, definition);
		setupTimes = new HashMap<Communicator, Double>();
	}

	public void addSetupTime(Communicator comm, double time) {
		setupTimes.put(comm, time);
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.processor;
	}

	public Set<Communicator> getCommunicators() {
		return setupTimes.keySet();
	}

	public double getSetupTime(Communicator comm) {
		return setupTimes.get(comm);
	}

	public Map<Communicator, Double> getSetupTimes() {
		return setupTimes;
	}

	public void addCommunicationPerformer(CommunicationPerformer commPerformer) {
		commPerformers.add(commPerformer);

	}

	public Set<CommunicationPerformer> getCommunicationPerformers() {
		return commPerformers;
	}
}
