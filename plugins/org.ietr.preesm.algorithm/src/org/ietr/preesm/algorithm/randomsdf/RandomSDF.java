/*******************************************************************************
 * Copyright or © or Copr. %%LOWERDATE%% - %%UPPERDATE%% IETR/INSA:
 *
 * %%AUTHORS%%
 *
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/

package org.ietr.preesm.algorithm.randomsdf;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.generator.SDFRandomGraph;
import org.ietr.dftools.algorithm.model.sdf.SDFAbstractVertex;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;
import org.ietr.dftools.architecture.slam.Design;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.preesm.core.scenario.ConstraintGroupManager;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.core.scenario.Timing;

/**
 * This Workflow element is used to generate random SDF graphs The user can
 * define the following options: - Number of vertices - Minimum/maximum number
 * of input/output per actor - Minimum/maximum input/output rates -
 * Minimum/maximum execution time
 * 
 * The Workflow element must have a scenario, a sdf and an architecture as
 * inputs and outputs a SDF and a Scenario
 * 
 * This Workflow element is experimental and probably have many flaws. It should
 * be used with great caution.
 * 
 * @author kdesnos
 *
 */
public class RandomSDF extends AbstractTaskImplementation {

	private int nbVertex;
	private int minInDegree;
	private int maxInDegree;
	private int minOutDegree;
	private int maxOutDegree;
	private int minRate;
	private int maxRate;
	private int minTime;
	private int maxTime;
	// All prod./Cons. rate will be multiplied by the value.
	// This does not change the consistency, only make
	// the production/consumption rates to be more significant
	private int rateMultiplier;

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		retrieveParameters(parameters);

		// Retrieve inputs
		Map<String, Object> outputs = new HashMap<String, Object>();
		SDFGraph sdf = (SDFGraph) inputs.get("SDF");

		Design architecture = (Design) inputs.get("architecture");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// Creates a random SDF graph
		SDFRandomGraph graphGenerator = new SDFRandomGraph();
		SDFGraph generatedGraph = null;
		try {
			generatedGraph = graphGenerator.createRandomGraph(nbVertex,
					minInDegree, maxInDegree, minOutDegree, maxOutDegree,
					minRate, maxRate, rateMultiplier);
		} catch (SDF4JException e1) {
			e1.printStackTrace();
		}

		if (generatedGraph != null) {
			Random generator = new Random();

			sdf = generatedGraph;

			// If the generated graph is not null, update
			// the scenario so that all task can be scheduled
			// on all operators, and all have the same runtime.
			HashMap<String, Integer> verticesNames = new HashMap<String, Integer>();
			for (SDFAbstractVertex vertex : sdf.vertexSet()) {
				int time = generator.nextInt(maxTime - minTime + 1) + minTime;
				verticesNames.put(vertex.getName(), time);
				vertex.setInfo(vertex.getName());
				vertex.setId(vertex.getName());
				// vertex.getPropertyBean().setValue("TIMING_PROPERTY", time);
			}

			ConstraintGroupManager constraint = scenario
					.getConstraintGroupManager();
			for (ComponentInstance component : architecture
					.getComponentInstances()) {
				constraint.addConstraints(component.getInstanceName(),
						verticesNames.keySet());
				for (SDFAbstractVertex vertex : sdf.vertexSet()) {
					Timing t = scenario.getTimingManager().addTiming(
							vertex.getName(),
							component.getComponent().getVlnv().getName());
					t.setTime(verticesNames.get(vertex.getName()));
				}
			}

		}

		outputs.put("scenario", scenario);
		outputs.put("SDF", sdf);
		return outputs;
	}

	/**
	 * This method retrieve the parameters set by the user
	 * 
	 * @param parameters
	 *            the list of parameters and their values
	 */
	private void retrieveParameters(Map<String, String> parameters) {
		String param = parameters.get("nbVertex");
		nbVertex = (param != null) ? Integer.decode(param) : 10;

		param = parameters.get("minInDegree");
		minInDegree = (param != null) ? Integer.decode(param) : 1;

		param = parameters.get("maxInDegree");
		maxInDegree = (param != null) ? Integer.decode(param) : 5;

		param = parameters.get("minOutDegree");
		minOutDegree = (param != null) ? Integer.decode(param) : 1;

		param = parameters.get("maxOutDegree");
		maxOutDegree = (param != null) ? Integer.decode(param) : 5;

		param = parameters.get("minRate");
		minRate = (param != null) ? Integer.decode(param) : 1;

		param = parameters.get("maxRate");
		maxRate = (param != null) ? Integer.decode(param) : 4;

		param = parameters.get("minTime");
		minTime = (param != null) ? Integer.decode(param) : 100;

		param = parameters.get("maxTime");
		maxTime = (param != null) ? Integer.decode(param) : 1000;

		param = parameters.get("rateMultiplier");
		rateMultiplier = (param != null) ? Integer.decode(param) : 1000;
	}

	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("nbVertex", "10");
		parameters.put("minInDegree", "1");
		parameters.put("maxInDegree", "5");
		parameters.put("minOutDegree", "1");
		parameters.put("maxOutDegree", "5");
		parameters.put("minRate", "1");
		parameters.put("maxRate", "4");
		parameters.put("minTime", "100");
		parameters.put("maxTime", "1000");
		parameters.put("rateMultiplier", "1000");

		return parameters;
	}

	@Override
	public String monitorMessage() {
		return "Generating random SDF.";
	}

}
