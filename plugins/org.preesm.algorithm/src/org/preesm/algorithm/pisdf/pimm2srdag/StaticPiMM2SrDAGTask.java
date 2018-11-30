/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2018)
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
import org.preesm.algorithm.model.visitors.VisitorOutput;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.pisdf.PiGraph;
import org.preesm.model.pisdf.brv.BRVMethod;
import org.preesm.model.pisdf.statictools.PiSDFToSingleRate;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.model.slam.Design;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * @author farresti
 *
 */
public class StaticPiMM2SrDAGTask extends AbstractTaskImplementation {

  public static final String CONSISTENCY_METHOD = "Consistency_Method";

  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {
    final Design architecture = (Design) inputs.get(AbstractWorkflowNodeImplementation.KEY_ARCHITECTURE);
    final PreesmScenario scenario = (PreesmScenario) inputs.get(AbstractWorkflowNodeImplementation.KEY_SCENARIO);
    final PiGraph graph = (PiGraph) inputs.get(AbstractWorkflowNodeImplementation.KEY_PI_GRAPH);

    final MapperDAG result;
    final Logger logger = PreesmLogger.getLogger();
    VisitorOutput.setLogger(logger);
    logger.log(Level.INFO, "Computing Repetition Vector for graph [" + graph.getName() + "]");
    // Check the consistency of the PiGraph and compute the associated Basic Repetition Vector
    // We use Topology-Matrix based method by default
    final String consistencyMethod = parameters.get(StaticPiMM2SrDAGTask.CONSISTENCY_METHOD);
    final BRVMethod method = BRVMethod.getByName(consistencyMethod);

    if (method == null) {
      throw new PreesmException("Unsupported method for checking consistency [" + consistencyMethod + "]");
    }

    // Convert the PiGraph to the Single-Rate Directed Acyclic Graph
    final PiGraph resultPi = PiSDFToSingleRate.compute(graph, method);

    result = covnertToMapperDAG(resultPi, architecture, scenario);

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
    res.put(CONSISTENCY_METHOD, BRVMethod.LCM.getLiteral());
    return res;
  }

  @Override
  public String monitorMessage() {
    return "Transforming PiGraph to Single-Rate Directed Acyclic Graph.";
  }

  /**
   * Converts the single rate acyclic PiSDF to {@link MapperDAG} and aggregate edges
   */
  public MapperDAG covnertToMapperDAG(final PiGraph resultPi, final Design architecture,
      final PreesmScenario scenario) {
    // Convert the PiMM vertices to DAG vertices
    final StaticPiMM2MapperDAGVisitor visitor = new StaticPiMM2MapperDAGVisitor(resultPi, architecture, scenario);
    visitor.doSwitch(resultPi);
    return visitor.getResult();
  }
}
