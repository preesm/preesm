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


package org.ietr.preesm.core.scenario;

import java.util.HashSet;
import java.util.Set;

import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.sdf4j.model.sdf.SDFAbstractVertex;

/**
 * container and manager of Constraint groups. It can load and store constraint
 * groups
 * 
 * @author mpelcat
 */
public class ConstraintGroupManager {

	/**
	 * List of all constraint groups
	 */
	private Set<ConstraintGroup> constraintgroups;

	public ConstraintGroupManager() {
		constraintgroups = new HashSet<ConstraintGroup>();
	}

	public void addConstraintGroup(ConstraintGroup cg) {

		constraintgroups.add(cg);
	}

	/**
	 * Adding a simple constraint on one vertex and one core
	 */
	public void addConstraint(OperatorDefinition opdef, SDFAbstractVertex vertex) {

		Set<ConstraintGroup> cgSet = getOpdefConstraintGroups(opdef);
		
		if(cgSet.isEmpty()){
			ConstraintGroup cg = new ConstraintGroup();
			cg.addOperatorDefinition(opdef);
			cg.addVertex(vertex);
			constraintgroups.add(cg);
		}
		else{
			((ConstraintGroup)cgSet.toArray()[0]).addVertex(vertex);
		}
	}

	/**
	 * Adding a constraint group on several vertices and one core
	 */
	public void addConstraints(OperatorDefinition opdef, Set<SDFAbstractVertex> vertexSet) {

		Set<ConstraintGroup> cgSet = getOpdefConstraintGroups(opdef);
		
		if(cgSet.isEmpty()){
			ConstraintGroup cg = new ConstraintGroup();
			cg.addOperatorDefinition(opdef);
			cg.addVertices(vertexSet);
			constraintgroups.add(cg);
		}
		else{
			((ConstraintGroup)cgSet.toArray()[0]).addVertices(vertexSet);
		}
	}

	/**
	 * Removing a simple constraint on one vertex and one core
	 */
	public void removeConstraint(OperatorDefinition opdef, SDFAbstractVertex vertex) {

		Set<ConstraintGroup> cgSet = getOpdefConstraintGroups(opdef);
		
		if(!cgSet.isEmpty()){
			for(ConstraintGroup cg:cgSet){
				cg.removeVertex(vertex);
			}
		}
	}

	/**
	 * Removing a constraint group on several vertices and one core
	 */
	public void removeConstraints(OperatorDefinition opdef, Set<SDFAbstractVertex> vertexSet) {

		Set<ConstraintGroup> cgSet = getOpdefConstraintGroups(opdef);
		
		if(!cgSet.isEmpty()){
			for(ConstraintGroup cg:cgSet){
				cg.removeVertices(vertexSet);
			}
		}
	}

	public Set<ConstraintGroup> getConstraintGroups() {

		return new HashSet<ConstraintGroup>(constraintgroups);
	}

	public Set<ConstraintGroup> getGraphConstraintGroups(
			SDFAbstractVertex vertex) {
		Set<ConstraintGroup> graphConstraintGroups = new HashSet<ConstraintGroup>();

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.hasVertex(vertex))
				graphConstraintGroups.add(cg);
		}

		return graphConstraintGroups;
	}

	public Set<ConstraintGroup> getOpdefConstraintGroups(
			OperatorDefinition opdef) {
		Set<ConstraintGroup> graphConstraintGroups = new HashSet<ConstraintGroup>();

		for (ConstraintGroup cg : constraintgroups) {
			if (cg.hasOperatorDefinition(opdef))
				graphConstraintGroups.add(cg);
		}

		return graphConstraintGroups;
	}

	public void removeAll() {

		constraintgroups.clear();
	}
}
