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

package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.IOperatorDefinition;
import org.ietr.preesm.core.architecture.simplemodel.Operator;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.Timing;
import org.ietr.preesm.plugin.abc.SpecialVertexManager;

/**
 * Properties of an implanted vertex set when converting dag to mapper dag
 * 
 * @author mpelcat
 */
public class InitialVertexProperty {

	/**
	 * Corresponding vertex
	 */
	private MapperDAGVertex parentVertex;

	/**
	 * Timings on available operators
	 */
	private List<Timing> timings;

	/**
	 * Available operators
	 */
	private Set<Operator> operators;

	/**
	 * Number of repetitions that ponderates the timing
	 */
	private int nbRepeat;


	public int getNbRepeat() {
		return nbRepeat;
	}

	public InitialVertexProperty() {
		super();
		timings = new ArrayList<Timing>();
		this.nbRepeat = 1;
		parentVertex = null;
		operators = new HashSet<Operator>();
	}

	public void setNbRepeat(int nbRepeat) {
		this.nbRepeat = nbRepeat;
	}
	
	public void addTiming(Timing timing) {
		if(getTiming(timing.getOperatorDefinition()) == null)
			this.timings.add(timing);
	}
	
	public void addOperator(Operator op) {
		this.operators.add(op);
	}

	public InitialVertexProperty clone(MapperDAGVertex parentVertex) {

		InitialVertexProperty property = new InitialVertexProperty();

		if (parentVertex != null)
			property.setParentVertex(parentVertex);

		Iterator<Timing> it = getTimings().iterator();
		while (it.hasNext()) {
			Timing next = it.next();
			property.addTiming(next);
		}

		Iterator<Operator> it2 = operators.iterator();
		while (it2.hasNext()) {
			Operator next = it2.next();
			property.addOperator(next);
		}
		
		property.setNbRepeat(nbRepeat);

		return property;

	}

	public Set<Operator> getOperatorSet() {
		return operators;
	}

	public MapperDAGVertex getParentVertex() {
		return parentVertex;
	}

	/**
	 * Returns the timing of the operation = number of repetitions * scenario time
	 */
	public int getTime(Operator operator) {

		Timing returntiming = Timing.UNAVAILABLE;
		int time = 0;

		if (operator != Operator.NO_COMPONENT) {

			returntiming = getTiming((OperatorDefinition) operator
					.getDefinition());
			
			if(returntiming != Timing.UNAVAILABLE){
				
				if(SpecialVertexManager.isBroadCast(parentVertex)){
					time = Timing.DEFAULT_BROADCAST_TIME;
				} else if(SpecialVertexManager.isFork(parentVertex)){
					time = Timing.DEFAULT_FORK_TIME;
				} else if(SpecialVertexManager.isJoin(parentVertex)){
					time = Timing.DEFAULT_JOIN_TIME;
				} else if(SpecialVertexManager.isInit(parentVertex)){
					time = Timing.DEFAULT_INIT_TIME;
				} else if(returntiming.getTime() != 0){
					// The basic timing is multiplied by the number of repetitions
					time = returntiming.getTime() * this.nbRepeat;
				}else{
					time = Timing.DEFAULT_TASK_TIME;
				}
			}
		}

		return time;
	}

	public Timing getTiming(IOperatorDefinition operatordef) {

		Timing returntiming = Timing.UNAVAILABLE;

		Iterator<Timing> iterator = timings.iterator();

		while (iterator.hasNext()) {
			Timing currenttiming = iterator.next();

			if (operatordef.equals(currenttiming.getOperatorDefinition())) {
				returntiming = currenttiming;
				break;
			}
		}

		return returntiming;
	}

	public List<Timing> getTimings() {
		return timings;
	}

	/**
	 * Checks in the vertex initial properties if it can be implanted on the
	 * given operator
	 */
	public boolean isImplantable(Operator operator) {
		
		if(SpecialVertexManager.isBroadCast(parentVertex)){
			return true;
		}
		else if(SpecialVertexManager.isFork(parentVertex)){
			return true;
		}
		else if(SpecialVertexManager.isJoin(parentVertex)){
			return true;
		}
		else if(SpecialVertexManager.isInit(parentVertex)){
			return true;
		}
		else{
			boolean isImplantable=false;
			
			for(Operator op:operators){
				if(op.equals(operator))
					return true;
			}
			
			return isImplantable;
		}
		
	}

	public void setParentVertex(MapperDAGVertex parentVertex) {
		this.parentVertex = parentVertex;
	}
}
