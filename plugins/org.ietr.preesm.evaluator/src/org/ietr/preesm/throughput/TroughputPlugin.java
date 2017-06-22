/*********************************************************
Copyright or © or Copr. IETR/INSA: Maxime Pelcat, Jean-François Nezan,
Karol Desnos, Hamza Deroui

[mpelcat,jnezan,kdesnos, hderoui]@insa-rennes.fr

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

package org.ietr.preesm.throughput;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;
import org.ietr.preesm.core.scenario.PreesmScenario;

/**
 * @author hderoui
 *
 *         Throughput plug-in for the evaluation of IBSDF and SDF graphs throughput
 */
public class TroughputPlugin extends AbstractTaskImplementation {

  /**
   * @author hderoui
   *
   *         The supported methods
   */
  public static enum ThroughputMethod {
    SR, // Schedule-Replace technique
    ESR, // Evaluate-Schedule-Replace method
    HPeriodic, // Hierarchical Periodic Schedule method
    Periodic, // Periodic schedule based method
    ASAPSDF3 // ASAP based method (SDF3)
  }

  // Plug-in parameters
  public static final String PARAM_METHOD               = "method";
  public static final String PARAM_METHOD_DEFAULT_VALUE = "SR";

  @Override
  public Map<String, Object> execute(Map<String, Object> inputs, Map<String, String> parameters, IProgressMonitor monitor, String nodeName, Workflow workflow)
      throws WorkflowException {

    // get the input graph, the scenario for actors duration, and the method to use
    SDFGraph inputGraph = (SDFGraph) inputs.get("SDF");
    PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");
    ThroughputMethod method = ThroughputMethod.valueOf(parameters.get("method"));

    // test the inputs
    test.start(inputGraph, scenario);

    // Compute the throughput of the graph
    double throughput = 0;
    switch (method) {
      case SR:
        // Schedule-Replace technique
        ScheduleReplace sr = new ScheduleReplace();
        throughput = sr.evaluate(inputGraph, scenario);
        break;

      case ESR:
        // Evaluate-Schedule-Replace method
        EvaluateScheduleReplace esr = new EvaluateScheduleReplace();
        throughput = esr.evaluate(inputGraph, scenario);
        break;

      case HPeriodic:
        // Hierarchical Periodic Schedule method
        HPeriodicSchedule HPeriodic = new HPeriodicSchedule();
        throughput = HPeriodic.evaluate(inputGraph, scenario);
        break;

      case Periodic:
        // Periodic schedule based method
        // TODO : convert first the IBSDF Graph to a srSDR graph
        PeriodicSchedule periodic = new PeriodicSchedule();
        throughput = periodic.evaluate(inputGraph, scenario);
        break;

      default:
        WorkflowLogger.getLogger().log(Level.WARNING, "Method not yet suported !");
        break;
    }

    // print the computed throughput
    WorkflowLogger.getLogger().log(Level.INFO, "Throughput value = " + throughput);

    // set the outputs
    Map<String, Object> outputs = new HashMap<String, Object>();
    outputs.put("SDF", inputGraph);
    outputs.put("Throughput", throughput);

    return outputs;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put(PARAM_METHOD, PARAM_METHOD_DEFAULT_VALUE);
    return parameters;
  }

  @Override
  public String monitorMessage() {
    return "Evaluating the graph throughput";
  }

}
