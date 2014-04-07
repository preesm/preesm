package org.ietr.preesm.experiment.pimm.cppgenerator.utils;

import java.util.HashMap;
import java.util.Map;

import org.ietr.preesm.experiment.model.pimm.AbstractActor;
import org.ietr.preesm.experiment.model.pimm.Fifo;
import org.ietr.preesm.experiment.model.pimm.Parameter;
import org.ietr.preesm.experiment.model.pimm.PiGraph;

public class CPPNameGenerator {
	/**
	 * Returns the name of the subgraph pg
	 */
	public String getSubraphName(PiGraph pg) {
		return pg.getName() + "_subGraph";
	}

	/**
	 * Returns the name of the variable pointing to the C++ object corresponding
	 * to AbstractActor aa
	 */
	public String getVertexName(AbstractActor aa) {
		return "vx" + aa.getName();
	}

	/**
	 * Returns the name of the building method for the PiGraph pg
	 */
	public String getMethodName(PiGraph pg) {
		return pg.getName();
	}

	/**
	 * Returns the name of the parameter
	 */
	public String getParameterName(Parameter p) {
		return "param_" + p.getName();
	}

	/**
	 * Returns the position of the C++ edge corresponding to Fifo f in the
	 * collection of edges of graph pg
	 */
	private Map<AbstractActor, ActorEdgesCounters> counterMap = new HashMap<AbstractActor, ActorEdgesCounters>();
	private Map<Fifo, Integer> edgeMap = new HashMap<Fifo, Integer>();

	public int getEdgeNumber(AbstractActor aa, Fifo f, EdgeKind kind) {
		if (!counterMap.containsKey(aa)) {
			counterMap.put(aa, new ActorEdgesCounters());
		}
		if (!edgeMap.containsKey(f)) {
			int edgeCounter = 0;
			switch (kind) {
			case in:
				edgeCounter = counterMap.get(aa).inputEdgesCounter;
				counterMap.get(aa).inputEdgesCounter = edgeCounter + 1;
				break;
			case out:
				edgeCounter = counterMap.get(aa).outputEdgesCounter;
				counterMap.get(aa).outputEdgesCounter = edgeCounter + 1;
				break;
			}
			edgeMap.put(f, edgeCounter);
		}
		return edgeMap.get(f);
	}

	private class ActorEdgesCounters {
		public int inputEdgesCounter;
		public int outputEdgesCounter;

		public ActorEdgesCounters() {
			this.inputEdgesCounter = 0;
			this.outputEdgesCounter = 0;
		}

	}
}
