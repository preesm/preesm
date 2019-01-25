/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2015 - 2019) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2019)
 * blaunay <bapt.launay@gmail.com> (2015)
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
package org.preesm.algorithm.evaluator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFAbstractVertex;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.model.scenario.PreesmScenario;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;

// TODO: Auto-generated Javadoc
/**
 * Main class used to compute the optimal periodic schedule and its throughput for a given SDF or IBSDF, returns the
 * throughput and the graph normalized (such that for each actor, prod and cons rates are the same).
 *
 * @author blaunay
 */
public class PeriodicEvaluator extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) throws PreesmException {

    // Retrieve the input dataflow and the scenario
    final SDFGraph inputGraph = (SDFGraph) inputs.get("SDF");
    final PreesmScenario scenario = (PreesmScenario) inputs.get("scenario");

    // Normalize the graph (for each actor, ins=outs)
    PreesmLogger.getLogger().log(Level.INFO, "Normalization");
    final NormalizeVisitor normalize = new NormalizeVisitor();
    try {
      inputGraph.accept(normalize);
    } catch (final PreesmException e) {
      throw new PreesmRuntimeException("The graph cannot be normalized");
    }
    final SDFGraph NormSDF = normalize.getOutput();
    PreesmLogger.getLogger().log(Level.INFO, "Normalization finished");

    // Find out if graph hierarchic (IBSDF) or not
    boolean hierarchical = false;
    for (final SDFAbstractVertex vertex : NormSDF.vertexSet()) {
      hierarchical = hierarchical
          || ((vertex.getGraphDescription() != null) && (vertex.getGraphDescription() instanceof SDFGraph));
    }

    // if IBSDF -> hierarchical algorithm
    ThroughputEvaluator scheduler;
    if (hierarchical) {
      scheduler = new IBSDFThroughputEvaluator();
    } else {
      // if SDF -> linear program for periodic schedule
      scheduler = new SDFThroughputEvaluator();
    }
    PreesmLogger.getLogger().log(Level.INFO, "Computation of the optimal periodic schedule");
    scheduler.setScenar(scenario);
    final double period = scheduler.launch(NormSDF);
    final double throughput = scheduler.throughputComputation(period, inputGraph);

    final Map<String, Object> outputs = new LinkedHashMap<>();
    // Normalized graph in the outputs
    outputs.put("SDF", NormSDF);
    // Throughput in the outputs
    outputs.put("Throughput", throughput);

    return outputs;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Evaluation of the throughput with a periodic schedule ";
  }
}
