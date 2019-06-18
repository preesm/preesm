/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018 - 2019)
 * Florian Arrestier <florian.arrestier@insa-rennes.fr> (2018)
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
/**
 *
 */
package org.preesm.algorithm.pisdf.pimm2srdag;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.mapper.model.MapperDAG;
import org.preesm.algorithm.model.dag.DirectedAcyclicGraph;
import org.preesm.commons.doc.annotations.DocumentedError;
import org.preesm.commons.doc.annotations.Parameter;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.doc.annotations.Value;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRateTask;
import org.preesm.model.scenario.Scenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * @author farresti
 *
 * @deprecated use {@link PiSDFToSingleRateTask} instead
 *
 */
@PreesmTask(id = "org.ietr.preesm.experiment.pimm2srdag.StaticPiMM2SrDAGTask", name = "Single-Rate Transformation",
    category = "Graph Transformation",

    inputs = { @Port(name = "PiMM", type = PiGraph.class), @Port(name = "scenario", type = Scenario.class),
        @Port(name = "architecture", type = Design.class) },

    outputs = { @Port(name = "DAG", type = DirectedAcyclicGraph.class), @Port(name = "PiMM", type = PiGraph.class) },

    shortDescription = "Transforms an SDF graph into an equivalent single-rate SDF graph.",

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

    parameters = { @Parameter(name = "ExplodeImploreSuppr",
        description = "_(Deprecated: use at your own risks)_\n" + "\n"
            + "This parameter makes it possible to remove most of the explode and implode actors that are inserted in "
            + "the graph during the single-rate transformation. The resulting SDF graph is an ill-constructed graph "
            + "where a single data input/output port of an actor may be connected to several First-In, First-Out "
            + "queues (Fifos).",
        values = {
            @Value(name = "false",
                effect = "(default) The suppression of explode/implode special actors is not activated."),
            @Value(name = "true", effect = "The suppression of explode/implode special actors is activated.") }) },

    documentedErrors = { @DocumentedError(message = "Graph not valid, not schedulable",
        explanation = "Single-rate transformation of the SDF graph was aborted because the top level was not "
            + "consistent, or it was consistent but did not contained enough delays — i.e. initial data "
            + "tokens — to make it schedulable.") },

    seeAlso = {
        "**Single-rate transformation**: J.L. Pino, S.S. Bhattacharyya, and E.A. Lee. A hierarchical multiprocessor "
            + "scheduling framework for synchronous dataflow graphs. Electronics Research Laboratory, College of "
            + "Engineering, University of California, 1995.",
        "**Special actors**: Karol Desnos, Maxime Pelcat, Jean-François Nezan, and Slaheddine Aridhi. On memory "
            + "reuse between inputs and outputs of dataflow actors. ACM Transactions on Embedded Computing Systems, "
            + "15(30):25, January 2016.",
        "**Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):"
            + "1235 – 1245, sept. 1987." })
@Deprecated
public class StaticPiMM2SrDAGTask extends AbstractTaskImplementation {

  public static final String CONSISTENCY_METHOD = "Consistency_Method";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final Scenario scenario = (Scenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final MapperDAG result;
    final Logger logger = PreesmLogger.getLogger();
    logger.log(Level.INFO, "Computing Repetition Vector for graph [" + graph.getName() + "]");
    // Check the consistency of the PiGraph and compute the associated Basic Repetition Vector
    // We use Topology-Matrix based method by default
    final String consistencyMethod = parameters.get(StaticPiMM2SrDAGTask.CONSISTENCY_METHOD);
    final BRVMethod method = BRVMethod.getByName(consistencyMethod);

    if (method == null) {
      throw new PreesmRuntimeException("Unsupported method for checking consistency [" + consistencyMethod + "]");
    }

    // Convert the PiGraph to the Single-Rate Directed Acyclic Graph
    final PiGraph resultPi = PiSDFToSingleRate.compute(graph, method);

    result = StaticPiMM2MapperDAGVisitor.convert(resultPi, architecture, scenario);

    final String message = "mapping a DAG with " + result.vertexSet().size() + " vertices and "
        + result.edgeSet().size() + " edges";
    PreesmLogger.getLogger().log(Level.INFO, message);

    final Map<String, Object> output = new LinkedHashMap<>();
    output.put(AbstractWorkflowNodeImplementation.KEY_SDF_DAG, result);
    output.put(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH, resultPi);
    return output;
  }

  @Override
  public Map<String, String> getDefaultParameters() {
    final LinkedHashMap<String, String> res = new LinkedHashMap<>();
    res.put(StaticPiMM2SrDAGTask.CONSISTENCY_METHOD, BRVMethod.LCM.getLiteral());
    return res;
  }

  @Override
  public String monitorMessage() {
    return "Transforming PiGraph to Single-Rate Directed Acyclic Graph (MapperDAG).";
  }

}
