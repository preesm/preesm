/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2025) :
 *
 * Antoine Morvan [antoine.morvan@insa-rennes.fr] (2017 - 2019)
 * Clément Guy [clement.guy@insa-rennes.fr] (2014 - 2015)
 * Florian Arrestier [florian.arrestier@insa-rennes.fr] (2018)
 * Hugo Miomandre [hugo.miomandre@insa-rennes.fr] (2024 - 2025)
 * Julien Hascoet [jhascoet@kalray.eu] (2016 - 2017)
 * Jonathan Piat [jpiat@laas.fr] (2008 - 2011)
 * Karol Desnos [karol.desnos@insa-rennes.fr] (2015)
 * Matthieu Wipliez [matthieu.wipliez@insa-rennes.fr] (2008)
 * Maxime Pelcat [maxime.pelcat@insa-rennes.fr] (2008 - 2012)
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
package org.preesm.algorithm.transforms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.core.runtime.IProgressMonitor;
import org.preesm.algorithm.model.sdf.SDFGraph;
import org.preesm.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.preesm.algorithm.model.sdf.visitors.ConsistencyChecker;
import org.preesm.commons.doc.annotations.DocumentedError;
import org.preesm.commons.doc.annotations.Port;
import org.preesm.commons.doc.annotations.PreesmTask;
import org.preesm.commons.exceptions.PreesmException;
import org.preesm.commons.exceptions.PreesmRuntimeException;
import org.preesm.commons.logger.PreesmLogger;
import org.preesm.workflow.elements.Workflow;
import org.preesm.workflow.implement.AbstractTaskImplementation;
import org.preesm.workflow.implement.AbstractWorkflowNodeImplementation;

/**
 * Class used to flatten the hierarchy of a given graph.
 *
 * @author jpiat
 * @author mpelcat
 */
@PreesmTask(id = "org.ietr.preesm.plugin.transforms.flathierarchy", name = "Hierarchy Flattening",
    category = "Graph Transformation",

    inputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH, type = SDFGraph.class) },
    outputs = { @Port(name = AbstractWorkflowNodeImplementation.KEY_SDF_GRAPH, type = SDFGraph.class) },

    shortDescription = "Transforms a hierarchical IBSDF graph into an equivalent SDF graph.",

    description = "The purpose of this workflow task is to flatten several levels of the hierarchy of an IBSDF graph"
        + " and produce an equivalent SDF graph. A hierarchical IBSDF graph is a graph where the internal behavior "
        + "of some actors is described using another IBSDF subgraph instead of a C header file. When applying this"
        + " transformation, hierarchical IBSDF actors of the first n levels of hierarchy are replaced with the actors "
        + "of the IBSDF subgraph with which these hierarchical actors are associated.",

    documentedErrors = { @DocumentedError(message = "Inconsistent Hierarchy, graph can’t be flattened",
        explanation = "Flattening of the IBSDF graph was aborted because one of the graph composing the application,"
            + " at the top level or deeper in the hierarchy, was not consistent.") },

    seeAlso = {
      "**IBSDF**: J. Piat, S.S. Bhattacharyya, and M. Raulet. Interface-based hierarchy for synchronous "
          + "data-flow graphs. In SiPS Proceedings, 2009.",
      "**Graph consistency**: E.A. Lee and D.G. Messerschmitt. Synchronous data flow. Proceedings of the IEEE, 75(9):"
          + "1235 – 1245, sept. 1987." })
@Deprecated
public class HierarchyFlattening extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map,
   * org.eclipse.core.runtime.IProgressMonitor, java.lang.String, org.preesm.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters,
      final IProgressMonitor monitor, final String nodeName, final Workflow workflow) {

    final Map<String, Object> outputs = new LinkedHashMap<>();
    final SDFGraph algorithm = (SDFGraph) inputs.get(KEY_SDF_GRAPH);
    final String depthS = parameters.get("depth");
    final StopWatch timer = new StopWatch();
    timer.start();

    final int decodedDepth;
    if (depthS != null) {
      decodedDepth = Integer.decode(depthS);
    } else {
      decodedDepth = 1;
    }

    if (decodedDepth == 0) {
      /* we now extract repetition vector into non-flattened hierarchical actors. */
      outputs.put(KEY_SDF_GRAPH, algorithm.copy());
      PreesmLogger.getLogger().log(Level.INFO, "flattening depth = 0: no flattening");
      return outputs;
    }

    final int depth;
    if (decodedDepth < 0) {
      depth = Integer.MAX_VALUE;
    } else {
      depth = decodedDepth;
    }

    final ConsistencyChecker checkConsistent = new ConsistencyChecker();
    if (!checkConsistent.verifyGraph(algorithm)) {
      throw new PreesmRuntimeException("Inconsistent Hierarchy, graph can't be flattened");
    }
    PreesmLogger.getLogger().finer(() -> "flattening application " + algorithm.getName() + " at level " + depth);

    final IbsdfFlattener flattener = new IbsdfFlattener(algorithm, depth);
    algorithm.insertBroadcasts();
    try {
      final boolean validateModel = algorithm.validateModel();
      if (!validateModel) {
        final String message = "Could not compute a schedule, graph can't be flattened";
        throw new PreesmRuntimeException(message);
      }

      flattener.flattenGraph();

      PreesmLogger.getLogger().info(() -> "Flattening complete with depth " + depth);
      final SDFGraph resultGraph = flattener.getFlattenedGraph();

      outputs.put(KEY_SDF_GRAPH, resultGraph);
    } catch (final PreesmException e) {
      throw new PreesmRuntimeException(e.getMessage(), e);
    }

    timer.stop();
    PreesmLogger.getLogger().info(() -> "Flattening: " + timer.toString() + "s.");

    return outputs;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractTaskImplementation#getDefaultParameters()
   */
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> parameters = new LinkedHashMap<>();

    parameters.put("depth", "-1");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.preesm.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Flattening algorithm hierarchy.";
  }
}
