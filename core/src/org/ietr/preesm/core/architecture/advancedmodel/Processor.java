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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ietr.preesm.core.architecture.ArchitectureComponent;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IOperator;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNode;
import org.ietr.preesm.core.architecture.simplemodel.ParallelNodeDefinition;

/**
 * A processor is a hardware entity used to execute computations and perform
 * communications.
 * 
 * @author pmu
 */
public class Processor extends ArchitectureComponent implements IOperator,
		ITerminal, ICommunicationPerformer {

	/**
	 * ID used to reference the element in a property bean in case of a
	 * computation vertex
	 */
	public static final String propertyBeanName = "processor";

	/**
	 * A communicator can be configured by this processor by using some time.
	 * The used time for the communicator is stored in setupTimes.
	 */
	private Map<String, Double> setupTimes;

	/**
	 * Communication performers are communicators and processors that can access
	 * this processor.
	 */
	private Set<ICommunicationPerformer> commPerformers;

	public Processor(String name, ProcessorDefinition definition) {
		super(name, definition);
		setupTimes = new HashMap<String, Double>();
		commPerformers = new HashSet<ICommunicationPerformer>();
	}

	public void addCommunicationPerformer(ICommunicationPerformer commPerformer) {
		commPerformers.add(commPerformer);
	}

	public void addSetupTime(String commName, double time) {
		setupTimes.put(commName, time);
	}

	public Set<ICommunicationPerformer> getCommunicationPerformers() {
		return commPerformers;
	}

	public Set<String> getCommunicators() {
		return setupTimes.keySet();
	}

	public double getSetupTime(String commName) {
		return setupTimes.get(commName);
	}

	public Map<String, Double> getSetupTimes() {
		return setupTimes;
	}

	public ArchitectureComponentType getType() {
		return ArchitectureComponentType.processor;
	}

	@Override
	public ArchitectureComponent clone() {
		return new Processor(getName(),null);
	}
}
