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

package org.ietr.preesm.core.architecture;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Architecture based on a fixed number of cores
 * 
 * @author mpelcat
 */
public class MultiCoreArchitecture implements IArchitecture {

	/**
	 * List of the cores + accelerators + media.
	 */
	private Set<ArchitectureComponent> architectureComponents;

	/**
	 * List of the interconnections between media and components.
	 */
	private Set<Interconnection> interconnections;

	/**
	 * main core of the archi.
	 */
	private Operator mainCore = null;

	/**
	 * main medium of the archi.
	 */
	private Medium mainMedium = null;

	/**
	 * name of the archi.
	 */
	private String name;

	/**
	 * Creating an empty architecture.
	 */
	public MultiCoreArchitecture(String name) {
		architectureComponents = new HashSet<ArchitectureComponent>();
		interconnections = new HashSet<Interconnection>();

		this.name = name;
	}

	/**
	 * Adds a medium to the architecture only if this medium can be connected to
	 * the two operators
	 * 
	 * @return true if the medium could be added
	 */
	public Medium addMedium(Medium medium, Operator operator1,
			Operator operator2, boolean isMain) {
		if (operator1.canConnectTo(medium)) {
			if (operator2.canConnectTo(medium)) {
				architectureComponents.add(medium);

				interconnections.add(new Interconnection(operator1, medium));
				interconnections.add(new Interconnection(operator2, medium));
			}
		}

		if (isMain || getMedia().isEmpty()) {
			mainMedium = medium;
		}

		return medium;
	}

	/**
	 * Adds a medium to the architecture only if this medium can be connected to
	 * the operator and the switch
	 * 
	 * @return true if the medium could be added
	 */
	public Medium addMedium(Medium medium, Operator op, Switch sw,
			boolean isMain) {
		if (op.canConnectTo(medium)) {
			if (sw.canConnectTo(medium)) {
				architectureComponents.add(medium);

				interconnections.add(new Interconnection(op, medium));
				interconnections.add(new Interconnection(sw, medium));
			}
		}

		if (isMain || getMedia().isEmpty()) {
			mainMedium = medium;
		}

		return medium;
	}

	/**
	 * Adds an operator to the architecture
	 */
	public Operator addOperator(Operator op, boolean isMain) {
		architectureComponents.add(op);

		// The first added core is the main one unless isMain
		// is set to true on another core
		if (isMain || architectureComponents.isEmpty()) {
			mainCore = op;
		}

		return op;
	}

	/**
	 * Adds a switch to the architecture
	 */
	public Switch addSwitch(Switch sw) {
		architectureComponents.add(sw);
		return sw;
	}

	@Override
	public MultiCoreArchitecture clone() {

		// Creating archi
		MultiCoreArchitecture newArchi = new MultiCoreArchitecture(this.name);

		// Iterating on media
		Iterator<Medium> mediaIt = this.getMedia().iterator();

		while (mediaIt.hasNext()) {
			Medium next = mediaIt.next();

			// each medium is cloned and added to the new archi
			newArchi.architectureComponents.add(next.clone());
		}

		// Iterating on operators
		Iterator<Operator> opIt = this.getOperators().iterator();

		while (opIt.hasNext()) {
			Operator next = opIt.next();

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
			Operator nextOp = opIt.next();
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
	public boolean connect(Medium medium, Operator operator) {
		boolean b = false;

		if (operator.canConnectTo(medium)) {
			interconnections.add(new Interconnection(operator, medium));
			b = true;
		}

		return b;
	}

	/**
	 * 
	 */
	private boolean existInterconnection(ArchitectureInterface mediumIntf,
			ArchitectureInterface operatorIntf) {

		Iterator<Interconnection> iterator = interconnections.iterator();

		while (iterator.hasNext()) {
			Interconnection currentInter = iterator.next();

			if (currentInter.getMediumInterface().equals(mediumIntf)
					&& currentInter.getOperatorInterface().equals(operatorIntf))
				return true;
		}

		return false;
	}

	/**
	 * 
	 */
	private boolean existInterconnection(Medium medium, Operator operator) {

		ArchitectureInterface mediumIntf = medium
				.getInterface((MediumDefinition) medium.getDefinition());
		ArchitectureInterface operatorIntf = operator
				.getInterface((MediumDefinition) medium.getDefinition());

		return existInterconnection(mediumIntf, operatorIntf);
	}

	@Override
	public Medium getMainMedium() {
		return mainMedium;
	}

	@Override
	public Operator getMainOperator() {
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
	public Set<Medium> getMedia(Operator op) {
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

	public Set<Medium> getMedia(Operator op1, Operator op2) {

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
	public Operator getOperator(String name) {
		Iterator<Operator> iterator = getOperators().iterator();

		while (iterator.hasNext()) {
			Operator currentop = iterator.next();

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
		Iterator<Operator> iterator = getOperators().iterator();

		while (iterator.hasNext()) {
			Operator currentop = iterator.next();

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
	public Set<Operator> getOperators() {
		Set<Operator> ops = new HashSet<Operator>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Operator) {
				ops.add((Operator) currentCmp);
			}
		}

		return ops;
	}

	/**
	 * Returns the operators of the given definition
	 */
	@Override
	public Set<Operator> getOperators(OperatorDefinition def) {
		Set<Operator> ops = new HashSet<Operator>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Operator) {
				if (currentCmp.getDefinition().getId().equalsIgnoreCase(
						def.getId())) {
					ops.add((Operator) currentCmp);
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
	public Switch getSwitch(String name) {
		Iterator<Switch> iterator = getSwitches().iterator();

		while (iterator.hasNext()) {
			Switch currentsw = iterator.next();

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
	public SwitchDefinition getSwitchDefinition(String id) {
		Iterator<Switch> iterator = getSwitches().iterator();

		while (iterator.hasNext()) {
			Switch currentsw = iterator.next();

			if (currentsw.getDefinition().getId().compareToIgnoreCase(id) == 0) {
				return ((SwitchDefinition) currentsw.getDefinition());
			}
		}

		return null;
	}

	/**
	 * Returns all the switches
	 */
	@Override
	public Set<Switch> getSwitches() {
		Set<Switch> sws = new HashSet<Switch>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Switch) {
				sws.add((Switch) currentCmp);
			}
		}

		return sws;
	}

	/**
	 * Returns the switches of the given definition
	 */
	@Override
	public Set<Switch> getSwitches(SwitchDefinition def) {
		Set<Switch> sws = new HashSet<Switch>();

		Iterator<ArchitectureComponent> iterator = architectureComponents
				.iterator();

		while (iterator.hasNext()) {
			ArchitectureComponent currentCmp = iterator.next();

			if (currentCmp instanceof Switch) {
				if (currentCmp.getDefinition().getId().equalsIgnoreCase(
						def.getId())) {
					sws.add((Switch) currentCmp);
				}
			}
		}

		return sws;
	}

	/**
	 * Returns all the interconnections
	 */
	@Override
	public Set<Interconnection> getInterconnections() {
		return interconnections;
	}

	@Override
	public String getName() {
		return name;
	}
}
