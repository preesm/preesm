/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2008 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Julien_Hascoet <jhascoet@kalray.eu> (2016 - 2017)
 * Jonathan Piat <jpiat@laas.fr> (2008 - 2011)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2008)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2008 - 2012)
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
package org.ietr.preesm.algorithm.transforms;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.sdf.transformations.IbsdfFlattener;
import org.ietr.dftools.algorithm.model.sdf.visitors.ConsistencyChecker;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.algorithm.model.visitors.VisitorOutput;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.tools.WorkflowLogger;

/**
 * Class used to flatten the hierarchy of a given graph.
 *
 * @author jpiat
 * @author mpelcat
 */
public class HierarchyFlattening extends AbstractTaskImplementation {

  private static final WorkflowLogger LOGGER = WorkflowLogger.getLogger();

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map, java.util.Map, org.eclipse.core.runtime.IProgressMonitor,
   * java.lang.String, org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs, final Map<String, String> parameters, final IProgressMonitor monitor,
      final String nodeName, final Workflow workflow) throws WorkflowException {

    final Map<String, Object> outputs = new LinkedHashMap<>();
    final SDFGraph algorithm = (SDFGraph) inputs.get("SDF");
    final String depthS = parameters.get("depth");

    int depth;
    if (depthS != null) {
      depth = Integer.decode(depthS);
    } else {
      depth = 1;
    }

    if (depth == 0) {
      outputs.put("SDF", algorithm.clone()); /* we now extract repetition vector into non-flattened hierarchical actors. */
      LOGGER.log(Level.INFO, "flattening depth = 0: no flattening");
      return outputs;
    }

    LOGGER.setLevel(Level.FINEST);
    VisitorOutput.setLogger(LOGGER);
    final ConsistencyChecker checkConsistent = new ConsistencyChecker();
    if (checkConsistent.verifyGraph(algorithm)) {
      LOGGER.log(Level.FINER, "flattening application " + algorithm.getName() + " at level " + depth);

      final IbsdfFlattener flattener = new IbsdfFlattener(algorithm, depth);
      VisitorOutput.setLogger(LOGGER);
      try {
        final boolean validateModel = algorithm.validateModel(LOGGER);
        if (validateModel) {
          try {
            flattener.flattenGraph();
          } catch (final SDF4JException e) {
            throw (new WorkflowException(e.getMessage(), e));
          }
          LOGGER.log(Level.INFO, "Flattening complete with depth " + depth);
          final SDFGraph resultGraph = flattener.getFlattenedGraph();// flatHier.getOutput();

          // for (final SDFEdge e : resultGraph.edgeSet()) {
          // p("Flatenning " + e.getSourceLabel() + " " + e.getTargetLabel() + " rate " + e.getDataType().toString());
          // }

          outputs.put("SDF", resultGraph);
        } else {
          final String message = "Could not schedule the Graph because its model is not valid.";
          throw (new WorkflowException(message));
        }
      } catch (final SDF4JException e) {
        throw (new WorkflowException(e.getMessage(), e));
      }
    } else {
      LOGGER.log(Level.SEVERE, "Inconsistent Hierarchy, graph can't be flattened");
      outputs.put("SDF", algorithm.clone());
      throw (new WorkflowException("Inconsistent Hierarchy, graph can't be flattened"));
    }

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

    parameters.put("depth", "1");
    return parameters;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation#monitorMessage()
   */
  @Override
  public String monitorMessage() {
    return "Flattening algorithm hierarchy.";
  }
}
