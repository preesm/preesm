/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2011 - 2018) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017 - 2018)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Matthieu Wipliez <matthieu.wipliez@insa-rennes.fr> (2011)
 * Maxime Pelcat <maxime.pelcat@insa-rennes.fr> (2011 - 2012)
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
package org.ietr.dftools.workflow.elements;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Workflow graph.
 *
 * @author mpelcat
 */
public class Workflow extends DirectedMultigraph<AbstractWorkflowNode, WorkflowEdge> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -908014142930559238L;

  /** Path of the file that contains the workflow. */
  private IPath path = null;

  /**
   * Instantiates a new workflow.
   */
  public Workflow() {
    super(WorkflowEdge.class);
  }

  /**
   * Vertex topological list.
   *
   * @return the list
   */
  public List<AbstractWorkflowNode> vertexTopologicalList() {
    final List<AbstractWorkflowNode> nodeList = new ArrayList<>();
    final TopologicalOrderIterator<AbstractWorkflowNode, WorkflowEdge> it = new TopologicalOrderIterator<>(this);

    while (it.hasNext()) {
      final AbstractWorkflowNode node = it.next();
      nodeList.add(node);
    }

    return nodeList;
  }

  /**
   * Sets the path.
   *
   * @param path
   *          the new path
   */
  public void setPath(final IPath path) {
    this.path = path;
  }

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return this.path.segment(0);
  }

  /**
   * Checks for scenario.
   *
   * @return true, if successful
   */
  public boolean hasScenario() {
    int nbScenarios = 0;
    for (final AbstractWorkflowNode node : vertexSet()) {
      if (node.isScenarioNode()) {
        nbScenarios++;
      }
    }
    return nbScenarios == 1;
  }
}
