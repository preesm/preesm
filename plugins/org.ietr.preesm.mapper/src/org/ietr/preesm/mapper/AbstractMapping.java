/*********************************************************
Copyright or © or Copr. IETR/INSA: Matthieu Wipliez, Jonathan Piat,
Maxime Pelcat, Jean-François Nezan, Mickaël Raulet

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

package org.ietr.preesm.mapper;

import java.util.HashMap;
import java.util.Map;

import net.sf.dftools.architecture.slam.Design;
import net.sf.dftools.workflow.WorkflowException;
import net.sf.dftools.workflow.elements.Workflow;
import net.sf.dftools.workflow.implement.AbstractTaskImplementation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.preesm.core.scenario.PreesmScenario;
import org.ietr.preesm.mapper.abc.impl.latency.SpanLengthCalculator;
import org.ietr.preesm.mapper.abc.route.calcul.RouteCalculator;
import org.ietr.preesm.mapper.model.MapperDAG;
import org.ietr.preesm.mapper.params.AbcParameters;

/**
 * Generic class representing the scheduling algorithm behaviour
 * 
 * @author pmenuet
 * @author mpelcat
 */
public abstract class AbstractMapping extends AbstractTaskImplementation {

	@Override
	public Map<String, Object> execute(Map<String, Object> inputs,
			Map<String, String> parameters, IProgressMonitor monitor,
			String nodeName, Workflow workflow) throws WorkflowException {

		Design architecture = (Design) inputs.get("architecture");
		PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

		// Asking to recalculate routes
		RouteCalculator.recalculate(architecture, scenario);
		return null;
	}

	/**
	 * Generic mapping message
	 */
	@Override
	public String monitorMessage() {
		return "Mapping/Scheduling";
	}

	/**
	 * Returns parameters common to all mappers
	 */
	@Override
	public Map<String, String> getDefaultParameters() {
		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("simulatorType", "LooselyTimed");
		parameters.put("edgeSchedType", "Simple");
		parameters.put("balanceLoads", "false");
		return parameters;
	}

	protected void clean(Design architecture, PreesmScenario scenario) {
		// Asking to delete route
		RouteCalculator.deleteRoutes(architecture, scenario);
	}

	/**
	 * Calculates the DAG span length on the architecture main operator (the
	 * tasks that cannot be executed by the main operator are deported without
	 * transfer time to other operator)
	 */
	protected void calculateSpan(MapperDAG dag, Design archi,
			PreesmScenario scenario, AbcParameters parameters) {

		SpanLengthCalculator spanCalc = new SpanLengthCalculator(parameters,
				dag, archi, parameters.getSimulatorType().getTaskSchedType(),
				scenario);
		spanCalc.resetDAG();

	}
}
