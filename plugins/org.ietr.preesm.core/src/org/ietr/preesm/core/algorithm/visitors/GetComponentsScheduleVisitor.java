/**
 * Copyright or © or Copr. IETR/INSA - Rennes (2012 - 2017) :
 *
 * Antoine Morvan <antoine.morvan@insa-rennes.fr> (2017)
 * Clément Guy <clement.guy@insa-rennes.fr> (2014 - 2015)
 * Karol Desnos <karol.desnos@insa-rennes.fr> (2012)
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
package org.ietr.preesm.core.algorithm.visitors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.ietr.dftools.algorithm.model.dag.DAGEdge;
import org.ietr.dftools.algorithm.model.dag.DAGVertex;
import org.ietr.dftools.algorithm.model.dag.DirectedAcyclicGraph;
import org.ietr.dftools.algorithm.model.visitors.IGraphVisitor;
import org.ietr.dftools.algorithm.model.visitors.SDF4JException;
import org.ietr.dftools.architecture.slam.ComponentInstance;

// TODO: Auto-generated Javadoc
/**
 * Visitor to retrieve the schedule of each component of a mapped Directed Acyclic Graph.
 *
 * @author kdesnos
 */
public class GetComponentsScheduleVisitor implements IGraphVisitor<DirectedAcyclicGraph, DAGVertex, DAGEdge> {

  /** The components schedule. */
  protected Map<ComponentInstance, ArrayList<DAGVertex>> _componentsSchedule;

  /**
   * Constructor of the GetComponentsScheduleVisitor.
   */
  public GetComponentsScheduleVisitor() {
    this._componentsSchedule = new LinkedHashMap<>();
  }

  /**
   * Return the result of the visitor algorithm.
   *
   * @return An LinkedHashMap containing the schedule of each component of the DAG
   */
  public Map<ComponentInstance, ArrayList<DAGVertex>> getResult() {
    return this._componentsSchedule;
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractEdge)
   */
  @Override
  public void visit(final DAGEdge currentEdge) {
    // Nothing to do here for this visitor
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractGraph)
   */
  @Override
  public void visit(final DirectedAcyclicGraph dag) throws SDF4JException {
    // Iterate the vertices of the DirectedAcyclicGraph in their
    // total scheduling order
    final Iterator<DAGVertex> iterator = dag.vertexSet().iterator();
    while (iterator.hasNext()) {
      final DAGVertex currentVertex = iterator.next();
      currentVertex.accept(this);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see org.ietr.dftools.algorithm.model.visitors.IGraphVisitor#visit(org.ietr.dftools.algorithm.model.AbstractVertex)
   */
  @Override
  public void visit(final DAGVertex dagVertex) throws SDF4JException {
    // We only add "task" vertices to schedules
    if (dagVertex.getPropertyBean().getValue("vertexType").toString().equals("task")) {
      // Retrieve the component on which the vertex is mapped
      final ComponentInstance component = (ComponentInstance) dagVertex.getPropertyBean().getValue("Operator");
      if (component != null) {
        // If a component was retrieved, add the current vertex
        // to the schedule of this component.
        ArrayList<DAGVertex> schedule = this._componentsSchedule.get(component);
        // If the component had no existing schedule, create one
        if (schedule == null) {
          schedule = new ArrayList<>();
          this._componentsSchedule.put(component, schedule);
        }
        schedule.add(dagVertex);
      }
    }
  }

}
