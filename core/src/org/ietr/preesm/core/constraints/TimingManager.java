package org.ietr.preesm.core.constraints;

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.architecture.OperatorDefinition;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFVertex;

/**
 * Manager of the graphs timings
 * 
 * @author mpelcat
 * 
 */
public class TimingManager {

	/**
	 * Default timing when none was set
	 */
	private Timing defaultTiming = null;

	/**
	 * List of all timings
	 */
	private List<Timing> timings;

	public TimingManager() {
		timings = new ArrayList<Timing>();
		defaultTiming = new Timing(new OperatorDefinition("default"),
				new SDFVertex(), 1000);
	}

	public Timing addTiming(SDFAbstractVertex graph, OperatorDefinition operator) {

		Timing newt = new Timing(operator, graph);
		for (Timing timing : timings) {
			if (timing.equals(newt)) {
				return timing;
			}
		}

		timings.add(newt);
		return newt;
	}

	public Timing addTiming(Timing newt) {

		for (Timing timing : timings) {
			if (timing.equals(newt)) {
				return timing;
			}
		}

		timings.add(newt);
		return newt;
	}

	public List<Timing> getGraphTimings(String graphName) {
		List<Timing> vals = new ArrayList<Timing>();

		for (Timing timing : timings) {
			if (timing.getVertex().getName().equals(graphName)) {
				vals.add(timing);
			}
		}
		return vals;
	}

	public int getTimingOrDefault(SDFAbstractVertex graph,
			OperatorDefinition operator) {
		Timing val = null;

		for (Timing timing : timings) {
			if (timing.getVertex() == graph
					&& timing.getOperatorDefinition() == operator) {
				val = timing;
			}
		}

		if (val == null) {
			val = defaultTiming;
		}

		return val.getTime();
	}

	public List<Timing> getTimings() {

		return timings;
	}

	public void removeAll() {

		timings.clear();
	}
}
