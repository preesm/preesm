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

package org.ietr.preesm.core.architecture.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.ietr.preesm.core.architecture.IArchitecture;

/**
 * A multiprocessor architecture contains a fixed number of processors.
 * 
 * @author pmu
 */
public class MultiProcessorArchitecture {

	/**
	 * Set of the components
	 */
	private Set<Component> components;

	/**
	 * Set of the interconnections between interfaces
	 */
	private Set<SpiritInterconnection> interconnections;

	/**
	 * Main processor of the architecture
	 */
	private Processor mainProcessor = null;

	/**
	 * Main communicator of the architecture
	 */
	private Communicator mainCommunicator = null;

	/**
	 * Name of the architecture
	 */
	private String name;

	/**
	 * Creating an empty architecture.
	 */
	public MultiProcessorArchitecture(String name) {
		components = new HashSet<Component>();
		interconnections = new HashSet<SpiritInterconnection>();
		this.name = name;
	}

	

	/**
	 * Adds a processor to the architecture
	 */
	public Processor addProcessor(Processor proc, boolean isMain) {
		components.add(proc);

		// The first added processor is the main one unless isMain
		// is set to true on another core
		if (isMain || components.isEmpty()) {
			mainProcessor = proc;
		}
		return proc;
	}

	/**
	 * Adds a communication node to the architecture
	 */
	public CommunicationNode addCommunicationNode(CommunicationNode cn) {
		components.add(cn);
		return cn;
	}

	@Override
	public MultiProcessorArchitecture clone() {

		// Creating archi
		MultiProcessorArchitecture newArchi = new MultiProcessorArchitecture(this.name);

		// Iterating on media
		Iterator<Medium> mediaIt = this.getMedia().iterator();

		while (mediaIt.hasNext()) {
			Medium next = mediaIt.next();

			// each medium is cloned and added to the new archi
			newArchi.architectureComponents.add(next.clone());
		}

		// Iterating on operators
		Iterator<AbstractNode> opIt = this.getOperators().iterator();

		while (opIt.hasNext()) {
			AbstractNode next = opIt.next();

			// each operator is cloned and added to the new archi
			// The archi is given to the clone method to reference
			// the already added media
			newArchi.architectureComponents.add(next.clone(this));
		}

		// Main core and media are set

		if (getMainOperator() != null)
			newArchi.mainCore = newArchi.getOperator(getMainOperator()
					.getName());

		if (getMainMedium() != null)
			newArchi.mainMedium = newArchi.getMedium(getMainMedium().getName());

		// We iterate again on both operators and media to add interconnexions
		opIt = this.getOperators().iterator();

		while (opIt.hasNext()) {
			AbstractNode nextOp = opIt.next();
			mediaIt = this.getMedia().iterator();

			while (mediaIt.hasNext()) {
				Medium nextMedium = mediaIt.next();

				if (this.existInterconnection(nextMedium, nextOp)) {
					newArchi.connect(newArchi.getMedium(nextMedium.getName()),
							newArchi.getOperator(nextOp.getName()));
				}
			}
		}
		return newArchi;
	}

	/**
	 * Connects a medium and an operator
	 * 
	 * @return true if the medium could be added
	 */
	public boolean connect(Medium medium, AbstractNode abstractNode) {
		boolean b = false;

		if (abstractNode.canConnectTo(medium)) {
			spiritInterconnections.add(new SpiritInterconnection(abstractNode,
					medium));
			b = true;
		}

		return b;
	}

	/**
	 * 
	 */
	private boolean existInterconnection(SpiritInterface mediumIntf,
			SpiritInterface operatorIntf) {

		Iterator<SpiritInterconnection> iterator = interconnections
				.iterator();

		while (iterator.hasNext()) {
			SpiritInterconnection currentInter = iterator.next();

			if (currentInter.getMediumInterface().equals(mediumIntf)
					&& currentInter.getOperatorInterface().equals(operatorIntf))
				return true;
		}

		return false;
	}

	/**
	 * 
	 */
	private boolean existInterconnection(Medium medium,
			AbstractNode abstractNode) {

		SpiritInterface mediumIntf = medium
				.getInterface((MediumDefinition) medium.getDefinition());
		SpiritInterface operatorIntf = abstractNode
				.getInterface((MediumDefinition) medium.getDefinition());

		return existInterconnection(mediumIntf, operatorIntf);
	}

	@Override
	public Medium getMainMedium() {
		return mainMedium;
	}

	@Override
	public AbstractNode getMainOperator() {
		return mainCore;
	}

	/**
	 * Returns all the media
	 */
	public Set<Medium> getMedia() {
		Set<Medium> media = new HashSet<Medium>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Medium) {
				media.add((Medium) currentCmp);
			}
		}

		return media;
	}

	/**
	 * 
	 */
	public Set<Medium> getMedia(AbstractNode op) {
		Set<Medium> media = new HashSet<Medium>();
		Iterator<Medium> iterator = getMedia().iterator();

		while (iterator.hasNext()) {

			Medium currentMedium = iterator.next();

			if (existInterconnection(currentMedium, op)) {
				media.add(currentMedium);
			}
		}

		return media;
	}

	public Set<Medium> getMedia(AbstractNode op1, AbstractNode op2) {

		Set<Medium> intersection = getMedia(op1);
		intersection.retainAll(getMedia(op2));

		return intersection;
	}

	/**
	 * Returns all the operators
	 */
	@Override
	public Medium getMedium(String name) {
		Iterator<Medium> iterator = getMedia().iterator();

		while (iterator.hasNext()) {
			Medium currentmed = iterator.next();

			if (currentmed.getName().compareToIgnoreCase(name) == 0) {
				return (currentmed);
			}
		}

		return null;
	}

	public MediumDefinition getMediumDefinition(String id) {
		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Medium) {
				if (currentCmp.getDefinition().getId().equalsIgnoreCase(id)) {
					return (MediumDefinition) currentCmp.getDefinition();
				}
			}
		}

		return null;
	}

	@Override
	public int getNumberOfOperators() {
		return getOperators().size();
	}

	/**
	 * Returns all the operators
	 */
	@Override
	public AbstractNode getOperator(String name) {
		Iterator<AbstractNode> iterator = getOperators().iterator();

		while (iterator.hasNext()) {
			AbstractNode currentop = iterator.next();

			if (currentop.getName().compareToIgnoreCase(name) == 0) {
				return (currentop);
			}
		}

		return null;
	}

	/**
	 * Returns the operator definition with the given id
	 */
	@Override
	public OperatorDefinition getOperatorDefinition(String id) {
		Iterator<AbstractNode> iterator = getOperators().iterator();

		while (iterator.hasNext()) {
			AbstractNode currentop = iterator.next();

			if (currentop.getDefinition().getId().compareToIgnoreCase(id) == 0) {
				return ((OperatorDefinition) currentop.getDefinition());
			}
		}

		return null;
	}

	/**
	 * Returns all the operators
	 */
	@Override
	public Set<AbstractNode> getOperators() {
		Set<AbstractNode> ops = new HashSet<AbstractNode>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof AbstractNode) {
				ops.add((AbstractNode) currentCmp);
			}
		}

		return ops;
	}

	/**
	 * Returns the operators of the given definition
	 */
	@Override
	public Set<AbstractNode> getOperators(OperatorDefinition def) {
		Set<AbstractNode> ops = new HashSet<AbstractNode>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof AbstractNode) {
				if (currentCmp.getDefinition().getId().equalsIgnoreCase(
						def.getId())) {
					ops.add((AbstractNode) currentCmp);
				}
			}
		}

		return ops;
	}

	public int getNumberOfSwitches() {
		return getSwitches().size();
	}

	/**
	 * Returns the switch with the given name
	 */
	@Override
	public CommunicationNode getSwitch(String name) {
		Iterator<CommunicationNode> iterator = getSwitches().iterator();

		while (iterator.hasNext()) {
			CommunicationNode currentsw = iterator.next();

			if (currentsw.getName().compareToIgnoreCase(name) == 0) {
				return (currentsw);
			}
		}

		return null;
	}

	/**
	 * Returns the switch definition with the given id
	 */
	@Override
	public CommunicationNodeDefinition getSwitchDefinition(String id) {
		Iterator<CommunicationNode> iterator = getSwitches().iterator();

		while (iterator.hasNext()) {
			CommunicationNode currentsw = iterator.next();

			if (currentsw.getDefinition().getId().compareToIgnoreCase(id) == 0) {
				return ((CommunicationNodeDefinition) currentsw.getDefinition());
			}
		}

		return null;
	}

	/**
	 * Returns all the switches
	 */
	@Override
	public Set<CommunicationNode> getSwitches() {
		Set<CommunicationNode> sws = new HashSet<CommunicationNode>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof CommunicationNode) {
				sws.add((CommunicationNode) currentCmp);
			}
		}

		return sws;
	}

	/**
	 * Returns the switches of the given definition
	 */
	@Override
	public Set<CommunicationNode> getSwitches(CommunicationNodeDefinition def) {
		Set<CommunicationNode> sws = new HashSet<CommunicationNode>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof CommunicationNode) {
				if (currentCmp.getDefinition().getId().equalsIgnoreCase(
						def.getId())) {
					sws.add((CommunicationNode) currentCmp);
				}
			}
		}

		return sws;
	}

	/**
	 * Returns all the interconnections
	 */
	@Override
	public Set<SpiritInterconnection> getInterconnections() {
		return spiritInterconnections;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<OperatorDefinition> getOperatorDefinitions() {
		Set<OperatorDefinition> opdefs = new HashSet<OperatorDefinition>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof AbstractNode) {
				if (!opdefs.contains(currentCmp.getDefinition()))
					opdefs.add((OperatorDefinition) currentCmp.getDefinition());
			}
		}

		return opdefs;
	}
}
