package org.ietr.preesm.plugin.mapper.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ietr.preesm.core.architecture.Operator;
import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.ietr.preesm.core.constraints.Timing;

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
	 * Number of repetitions that ponderates the timing
	 */
	private int nbRepeat;


	public InitialVertexProperty() {
		super();
		timings = new ArrayList<Timing>();
	}

	public void setNbRepeat(int nbRepeat) {
		this.nbRepeat = nbRepeat;
	}
	
	public void addTiming(Timing timing) {
		this.timings.add(timing);
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
		
		property.setNbRepeat(nbRepeat);

		return property;

	}

	public Set<OperatorDefinition> getOperatorDefinitionSet() {

		Set<OperatorDefinition> opset = new HashSet<OperatorDefinition>();

		Iterator<Timing> iterator = timings.iterator();

		while (iterator.hasNext()) {
			Timing currenttiming = iterator.next();

			opset.add(currenttiming.getOperatorDefinition());
		}

		return opset;
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
				time = returntiming.getTime() * this.nbRepeat;
			}
		}

		return time;
	}

	public Timing getTiming(OperatorDefinition operatordef) {

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
		return isImplantable((OperatorDefinition) operator.getDefinition());
	}

	/**
	 * Checks in the vertex initial properties if it can be implanted on the
	 * given operator
	 */
	public boolean isImplantable(OperatorDefinition operatordef) {
		return (getTiming(operatordef) != Timing.UNAVAILABLE);
	}

	public void setParentVertex(MapperDAGVertex parentVertex) {
		this.parentVertex = parentVertex;
	}
}
