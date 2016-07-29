/*******************************************************************************
 * Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
 * Karol Desnos, Julien Heulot, Clément Guy, Yaset Oliva Venegas
 * 
 * [mpelcat,jnezan,kdesnos,jheulot,cguy,yoliva]@insa-rennes.fr
 * 
 * This software is a computer program whose purpose is to prototype
 * parallel applications.
 * 
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
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
 ******************************************************************************/
package org.ietr.preesm.pimm.algorithm.pimm2sdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.preesm.core.scenario.ParameterValue;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.experiment.model.pimm.PiGraph;
import org.ietr.preesm.pimm.algorithm.pimm2sdf.visitor.StaticPiMM2SDFVisitor;

public class StaticPiMM2SDFLauncher {

	private PreesmScenario scenario;
	private PiGraph graph;

	public StaticPiMM2SDFLauncher(PreesmScenario scenario, PiGraph graph) {
		this.scenario = scenario;
		this.graph = graph;
	}

	/**
	 * Precondition: All
	 * 
	 * @return the SDFGraph obtained by visiting graph
	 * @throws StaticPiMM2SDFException
	 */
	public SDFGraph launch() throws StaticPiMM2SDFException {
		SDFGraph result;

		// Get all the available values for all the parameters
		Map<String, List<Integer>> parametersValues = getParametersValues();

		// Visitor creating the SDFGraph
		StaticPiMM2SDFVisitor visitor;
		PiGraphExecution execution = new PiGraphExecution(graph, parametersValues);
		visitor = new StaticPiMM2SDFVisitor(execution);
		graph.accept(visitor);

		result = visitor.getResult();
		return result;
	}

	private Map<String, List<Integer>> getParametersValues() throws StaticPiMM2SDFException {
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();

		for (ParameterValue paramValue : scenario.getParameterValueManager().getParameterValues()) {
			switch (paramValue.getType()) {
			case ACTOR_DEPENDENT:
				throw new StaticPiMM2SDFException("Parameter " + paramValue.getName()
						+ " is depends on a configuration actor. It is thus impossible to use the Static PiMM 2 SDF transformation. Try instead the Dynamic PiMM 2 SDF transformation (id: org.ietr.preesm.experiment.pimm2sdf.PiMM2SDFTask)");
			case INDEPENDENT:
				try {
					int value = Integer.parseInt(paramValue.getValue());
					List<Integer> values = new ArrayList<Integer>();
					values.add(value);
					result.put(paramValue.getName(), values);
					break;
				} catch (NumberFormatException e) {
					// The expression associated to the parameter is an
					// expression (and not an constant int value).
					// Leave it as it is, it will be solved later.
					break;
				}
			default:
				break;
			}
		}

		return result;
	}

	public class StaticPiMM2SDFException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8272147472427685537L;

		public StaticPiMM2SDFException(String message) {
			super(message);
		}
	}

}
