/*********************************************************
Copyright or � or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-Fran�ois Nezan, Micka�l Raulet

[mwipliez,jpiat,mpelcat,jnezan,mraulet]@insa-rennes.fr

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.serialize.CsvTimingParser;
import org.ietr.preesm.core.scenario.serialize.ExcelTimingParser;

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
	private String excelFileURL = "";

	/**
	 * Storing setup time and speed of memcpy for each type of operator
	 */
	private Map<String, MemCopySpeed> memcpySpeeds;

	/**
	 * Default value for a memcpy setup time
	 */
	private final static long DEFAULTMEMCPYSETUPTIME = 1;

	/**
	 * Default value for a memcpy speed
	 */
	private final static float DEFAULTMEMCPYTIMEPERUNIT = 1.0f;

	public TimingManager() {
		timings = new ArrayList<Timing>();
		memcpySpeeds = new HashMap<String, MemCopySpeed>();
		defaultTiming = new Timing("default", "default",
				Timing.DEFAULT_TASK_TIME);
	}

	public Timing addTiming(String sdfVertexId, String operatorDefinitionId) {

		Timing newt = new Timing(operatorDefinitionId, sdfVertexId);
		for (Timing timing : timings) {
			if (timing.equals(newt)) {
				return timing;
			}
		}

		timings.add(newt);
		return newt;
	}

	public void setTiming(String sdfVertexId, String operatorDefinitionId,
			long time) {
		addTiming(sdfVertexId, operatorDefinitionId).setTime(time);
	}

	public void setTiming(String sdfVertexId, String operatorDefinitionId,
			String value) {
		addTiming(sdfVertexId, operatorDefinitionId).setStringValue(value);
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
			Set<String> operatorDefinitionIds) {
		SDFAbstractVertex sdfVertex = dagVertex.getCorrespondingSDFVertex();
		List<Timing> vals = new ArrayList<Timing>();

		if (sdfVertex.getGraphDescription() == null) {
			for (Timing timing : timings) {
				if (timing.getVertexId().equals(sdfVertex.getId())) {
					vals.add(timing);
				}
			}
		} else if (sdfVertex.getGraphDescription() instanceof SDFGraph) {
			// Adds timings for all operators in hierarchy if they can be
			// calculated
			// from underlying vertices
			for (String opDefId : operatorDefinitionIds) {
				Timing t = generateVertexTimingFromHierarchy(
						dagVertex.getCorrespondingSDFVertex(), opDefId);
				if (t != null)
					vals.add(t);
			}
		}

		return vals;
	}

	private Timing getVertexTiming(SDFAbstractVertex sdfVertex, String opDefId) {
		for (Timing timing : timings) {
			if (timing.getVertexId().equals(sdfVertex.getName())
					&& timing.getOperatorDefinitionId().equals(opDefId)) {
				return timing;
			}
		}
		return null;
	}

	/**
	 * Calculates a vertex timing from its underlying vertices
	 */
	public Timing generateVertexTimingFromHierarchy(
			SDFAbstractVertex sdfVertex, String opDefId) {
		long maxTime = 0;
		SDFGraph graphDescription = (SDFGraph) sdfVertex.getGraphDescription();

		for (SDFAbstractVertex vertex : graphDescription.vertexSet()) {
			Timing vertexTiming;
			if (vertex.getGraphDescription() != null) {
				maxTime += generateVertexTimingFromHierarchy(vertex, opDefId)
						.getTime();
			} else if ((vertexTiming = getVertexTiming(vertex, opDefId)) != null) {
				try {
					maxTime += vertexTiming.getTime()
							* vertex.getNbRepeatAsInteger();
				} catch (InvalidExpressionException e) {
					maxTime += vertexTiming.getTime();
				}
			}
			if (maxTime < 0) {
				maxTime = Integer.MAX_VALUE;
				break;
			}
		}
		// TODO: time calculation for underlying tasks not ready
		return (new Timing(opDefId, sdfVertex.getName(), maxTime));

		/*
		 * SDFGraph graph = (SDFGraph)sdfVertex.getGraphDescription();
		 * 
		 * int time = 0; for(SDFAbstractVertex v : graph.vertexSet()){
		 * if(sdfVertex.getGraphDescription() == null){ time += sdfVertex. } }
		 * 
		 * if(time>=0) return(new Timing(opDef,sdfVertex,time)); else return
		 * null;
		 */
	}

	/**
	 * Looks for a timing entered in scenario editor. If there is none, returns
	 * a default value
	 */
	public Timing getTimingOrDefault(String sdfVertexId,
			String operatorDefinitionId) {
		Timing val = null;

		for (Timing timing : timings) {
			if (timing.getVertexId().equals(sdfVertexId)
					&& timing.getOperatorDefinitionId().equals(
							operatorDefinitionId)) {
				val = timing;
			}
		}

		if (val == null) {
			val = defaultTiming;
		}

		return val;
	}

	public List<Timing> getTimings() {

		return timings;
	}

	public void removeAll() {

		timings.clear();
	}

	public String getExcelFileURL() {
		return excelFileURL;
	}

	public void setExcelFileURL(String excelFileURL) {
		this.excelFileURL = excelFileURL;
	}

	public void importTimings(PreesmScenario currentScenario) {
		if (!excelFileURL.isEmpty() && currentScenario != null) {
			ExcelTimingParser excelParser = new ExcelTimingParser(currentScenario);
			CsvTimingParser csvParser = new CsvTimingParser(currentScenario);

			try {
				String[] fileExt = excelFileURL.split("\\.");
				switch(fileExt[fileExt.length-1]){
				case "xls":
					excelParser.parse(excelFileURL, currentScenario.getOperatorDefinitionIds());
					break;
				case "csv":
					csvParser.parse(excelFileURL, currentScenario.getOperatorDefinitionIds());
					break;
				}					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * For a type of operator, sets a memcopy setup time and speed
	 */
	public void putMemcpySpeed(MemCopySpeed speed) {
		memcpySpeeds.put(speed.getOperatorDef(), speed);
	}

	/**
	 * For a type of operator, gets a memcopy setup time
	 */
	public long getMemcpySetupTime(String operatorDef) {
		return memcpySpeeds.get(operatorDef).getSetupTime();
	}

	/**
	 * For a type of operator, gets the INVERSED memcopy speed (time per memory
	 * unit
	 */
	public float getMemcpyTimePerUnit(String operatorDef) {
		return memcpySpeeds.get(operatorDef).getTimePerUnit();
	}

	public Map<String, MemCopySpeed> getMemcpySpeeds() {
		return memcpySpeeds;
	}

	public boolean hasMemCpySpeed(String operatorDef) {
		return memcpySpeeds.keySet().contains(operatorDef);
	}

	public void setDefaultMemCpySpeed(String operatorDef) {
		putMemcpySpeed(new MemCopySpeed(operatorDef, DEFAULTMEMCPYSETUPTIME,
				DEFAULTMEMCPYTIMEPERUNIT));
	}

	public void clear() {
		getTimings().clear();
	}
}
