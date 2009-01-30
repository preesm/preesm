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

import java.util.ArrayList;
import java.util.List;

import org.ietr.preesm.core.architecture.ArchitectureComponentDefinition;
import org.ietr.preesm.core.architecture.ArchitectureComponentType;
import org.ietr.preesm.core.architecture.IOperatorDefinition;
import org.ietr.preesm.core.architecture.MultiCoreArchitecture;
import org.ietr.preesm.core.architecture.simplemodel.OperatorDefinition;
import org.ietr.preesm.core.scenario.editor.timings.ExcelTimingParser;
import org.sdf4j.model.dag.DAGVertex;
import org.sdf4j.model.sdf.SDFAbstractVertex;
import org.sdf4j.model.sdf.SDFGraph;
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

	/**
	 * Path to a file containing timings
	 */
	private String timingFileURL = "";

	public TimingManager() {
		timings = new ArrayList<Timing>();
		defaultTiming = new Timing(new OperatorDefinition("default"),
				new SDFVertex(), Timing.DEFAULT_TASK_TIME);
	}

	public Timing addTiming(SDFAbstractVertex graph,
			IOperatorDefinition operator) {

		Timing newt = new Timing(operator, graph);
		for (Timing timing : timings) {
			if (timing.equals(newt)) {
				return timing;
			}
		}

		timings.add(newt);
		return newt;
	}

	public void setTiming(SDFAbstractVertex graph,
			IOperatorDefinition operator, int time) {

		addTiming(graph, operator).setTime(time);
	}

	public Timing addTiming(Timing newt) {

		for (Timing timing : timings) {
			if (timing.equals(newt)) {
				timing.setTime(newt.getTime());
				return timing;
			}
		}

		timings.add(newt);
		return newt;
	}

	public List<Timing> getGraphTimings(DAGVertex dagVertex,
			MultiCoreArchitecture architecture) {
		SDFAbstractVertex sdfVertex = dagVertex.getCorrespondingSDFVertex();
		List<Timing> vals = new ArrayList<Timing>();

		if (sdfVertex.getGraphDescription() == null) {
			for (Timing timing : timings) {
				if (timing.getVertex().getName().equals(sdfVertex.getId())) {
					vals.add(timing);
				}
			}
		} else if(sdfVertex.getGraphDescription() instanceof SDFGraph){
			// Adds timings for all operators in hierarchy if they can be calculated
			// from underlying vertices
			for (ArchitectureComponentDefinition opDef : architecture
					.getComponentDefinitions(ArchitectureComponentType.operator)) {
				Timing t = generateVertexTimingFromHierarchy(dagVertex, (IOperatorDefinition) opDef);
				if(t!=null)
					vals.add(t);
			}
		}

		return vals;
	}

	/**
	 * Calculates a vertex timing from its underlying vertices
	 */
	public Timing generateVertexTimingFromHierarchy(DAGVertex dagVertex, IOperatorDefinition opDef) {

		SDFAbstractVertex sdfVertex = dagVertex.getCorrespondingSDFVertex();
		
		//TODO: time calculation for underlying tasks not ready
		return(new Timing(opDef,sdfVertex,100));
		
		
		/*
		SDFGraph graph = (SDFGraph)sdfVertex.getGraphDescription();

		int time = 0;
		for(SDFAbstractVertex v : graph.vertexSet()){
			if(sdfVertex.getGraphDescription() == null){
				time += sdfVertex.
			}
		}

		if(time>=0)
			return(new Timing(opDef,sdfVertex,time));
		else
			return null;*/
	}

	/**
	 * Looks for a timing entered in scenario editor. If there is none, returns
	 * a default value
	 */
	public int getTimingOrDefault(SDFAbstractVertex vertex,
			IOperatorDefinition operator) {
		Timing val = null;

		for (Timing timing : timings) {
			if (timing.getVertex().getName().equals(vertex.getName())
					&& timing.getOperatorDefinition().equals(operator)) {
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

	public String getTimingFileURL() {
		return timingFileURL;
	}

	public void setTimingFileURL(String timingFileURL, Scenario currentScenario) {
		this.timingFileURL = timingFileURL;

		if (!timingFileURL.isEmpty() && currentScenario != null) {
			ExcelTimingParser parser = new ExcelTimingParser(currentScenario);
			parser.parse(timingFileURL);
		}
	}
}
