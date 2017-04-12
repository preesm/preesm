/*******************************************************************************
 * Copyright or © or Copr. 2015 - 2017 IETR/INSA:
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2016)
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
package org.ietr.preesm.algorithm.optimization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.ietr.dftools.algorithm.model.parameters.InvalidExpressionException;
import org.ietr.dftools.algorithm.model.sdf.SDFGraph;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.workflow.WorkflowException;
import org.ietr.dftools.workflow.elements.Workflow;
import org.ietr.dftools.workflow.implement.AbstractTaskImplementation;
import org.ietr.dftools.workflow.implement.AbstractWorkflowNodeImplementation;
import org.ietr.preesm.algorithm.optimization.clean.joinfork.JoinForkCleaner;

// TODO: Auto-generated Javadoc
/**
 * Head class to launch optimizations on a SDFGraph.
 *
 * @author cguy
 */
public class MultiAlgorithmOptimizationTask extends AbstractTaskImplementation {

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.workflow.implement.AbstractTaskImplementation#execute(java.util.Map,
   * java.util.Map, org.eclipse.core.runtime.IProgressMonitor, java.lang.String,
   * org.ietr.dftools.workflow.elements.Workflow)
   */
  @Override
  public Map<String, Object> execute(final Map<String, Object> inputs,
      final Map<String, String> parameters, final IProgressMonitor monitor, final String nodeName,
      final Workflow workflow) throws WorkflowException {

    // Get the SDFGraph to optimize
    @SuppressWarnings("unchecked")
    final Set<SDFGraph> graphs = (Set<SDFGraph>) inputs
        .get(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPHS_SET);

    // First pass is to clean the graph from useless pairs of join-fork
    // vertices which can hinder scheduling

    // JoinForkCleaner evaluates some rates and delays expressions, and thus
    // can throw InvalidExpressionExceptions, even if at this point of the
    // workflow, there should have been already raised
    for (final SDFGraph graph : graphs) {
      try {
        while (JoinForkCleaner.cleanJoinForkPairsFrom(graph)) {
          ;
        }
      } catch (final InvalidExpressionException e) {
        System.err.println("SDFGraph " + graph.getName() + " contains invalid expressions.");
        e.printStackTrace();
      } catch (final SDF4JException e) {
        System.err.println("Error when cleaning fork/join pairs in SDFGraph " + graph.getName());
        e.printStackTrace();
      }
    }

    final Map<String, Object> outputs = new HashMap<>();
    outputs.put(AbstractWorkflowNodeImplementation.KEY_SDF_GRAPHS_SET, graphs);
    return outputs;
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
    return "Starting optimization of SDFGraph";
  }

}
