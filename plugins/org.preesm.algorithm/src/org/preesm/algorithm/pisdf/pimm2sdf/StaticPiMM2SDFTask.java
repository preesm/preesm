/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2014 - 2024) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024)
 *
 * This software is a computer program whose purpose is to help prototyping
 * parallel applications using dataflow formalism.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
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
 * knowledge of the CeCILL license and that you accept its terms.
 */
package org.preesm.algorithm.pisdf.pimm2sdf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.scenario.Scenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * The Class StaticPiMM2SDFTask.
 */
@PreesmTask(id = "org.ietr.preesm.experiment.pimm2sdf.StaticPiMM2SDFTask", name = "Static PiMM to IBSDF",
    category = "Graph Transformation",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class) },
    outputs = { @Port(name = "SDF", type = SDFGraph.class) },

    shortDescription = "Transforms a static PiSDF Graph into an equivalent IBSDF graph.",

    description = "In Preesm, since version 2.0.0, the Parameterized and Interfaced SDF (PiSDF) "
        + "model of computa tion is used as the frontend model in the graphical editor of dataflow"
        + " graphs. This model makes it possible to design dynamically reconfigurable dataflow graphs"
        + " where the value of parameters, and production/consumption rates depending on them, might"
        + " change during the execution of the application. In former versions, the Interface Based "
        + "SDF (IBSDF) model of computation was used as the front end model for application design. "
        + "Contrary to the PiSDF, the IBSDF is a static model of computation where production and "
        + "consumption rates of actors is fixed at compile-time.\n" + "\n"
        + "The purpose of this workflow task is to transform a static PiSDF graph into an equivalent "
        + "IBSDF graph. A static PiSDF graph is a PiSDF graph where dynamic reconfiguration "
        + "features of the PiSDF model of computation are not used.",

    seeAlso = {
        "**IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous "
            + "data-flow graphs. In SiPS Proceedings, 2009.",
        "**PiSDF**: K. Desnos, M. Pelcat, J.-F. Nezan, S.S. Bhattacharyya, and S. Aridhi. PiMM: Parameterized "
            + "and interfaced dataflow meta-model for MPSoCs runtime reconfiguration. In Embedded Computer "
            + "Systems: Architectures, Modeling, and Simulation (SAMOS XIII), 2013 International Conference "
            + "on, pages 41–48. IEEE, 2013." })
// @Deprecated
public class StaticPiMM2SDFTask extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {

    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final StopWatch timer = new StopWatch();
    timer.start();

    final StaticPiMM2SDFLauncher launcher = new StaticPiMM2SDFLauncher(scenario, graph);
    SDFGraph result;
    result = launcher.launch();

    timer.stop();
    PreesmLogger.getLogger().info(() -> "PiMM2SDF transformation: " + timer.toString() + "s.");
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH, result);
    return output;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    return Collections.emptyMap();
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Transforming PiGraph to SDFGraphs";
  }
}
